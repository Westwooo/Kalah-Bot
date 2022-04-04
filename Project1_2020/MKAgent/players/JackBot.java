package MKAgent.players;

import MKAgent.gameplay.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JackBot implements Bot {

    private final static int SWAP_MOVE = -1;

    // Used to execute threads that calculate next moves.
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    // The depth to which this bot searches.
    private final int searchDepth;

    // The current state of the board.
    private final Board board;

    // Is the game over?
    private boolean gameOver = false;

    // Is it our tur next?
    private boolean isNextTurn;

    // True only when we are taking our first turn.
    private boolean firstTurn = true;

    // The side of the board we are playing from.
    private Side mySide;

    // The last move played by our bot.
    private Move lastMovePlayed;

    // Maps from a board hashcode to a future that is currently calculating the best move for that board.
    private Map<Board, Future<Move>> bestNextMoveFutures = new HashMap<>();

    public JackBot(int holes, int seeds, int searchDepth) {
        board = new Board(holes, seeds);
        this.searchDepth = searchDepth;
    }

    @Override
    public void processMessage(String msg) throws InvalidMessageException {
        MsgType msgType = Protocol.getMessageType(msg);
        switch (msgType) {
            case START:
                boolean iAmSouth = Protocol.interpretStartMsg(msg);
                mySide = iAmSouth ? Side.SOUTH : Side.NORTH;
                isNextTurn = iAmSouth;
                break;
            case STATE:
                Protocol.MoveTurn moveTurn = Protocol.interpretStateMsg(msg, board, mySide);
                isNextTurn = moveTurn.again;
                if (moveTurn.move == SWAP_MOVE) {
                    mySide = mySide.opposite();
                }
                gameOver = moveTurn.end;
                break;
            case END:
                gameOver = true;
                break;
            default:
                break;
        }
    }

    int hit = 0;
    int round = 0;

    @Override
    public String getNextMove() throws Exception {
        // If we have the second move then always swap.
        if (firstTurn && mySide == Side.NORTH) {
            System.err.println("I WAS NORTH SO I SWAPPED");
            firstTurn = false;
            mySide = mySide.opposite();
            return Protocol.createSwapMsg();
        }

        // The future object that will contain our best move when it has been calculated.
        Future<Move> bestMoveFuture;

        if (bestNextMoveFutures.get(board) != null
                && !bestNextMoveFutures.get(board).isCancelled()) {
            hit++;
            // If we have already begun precalculating the best move for this board then we will just wait
            // for that to finish.
            bestMoveFuture = bestNextMoveFutures.get(board);

            // We must also remove the entry containing this future from bestNextMoveFutures leaving us free
            // to cancel all the other futures left in the map.
            bestNextMoveFutures.remove(board);
        } else {
            // If we have not begun precalculating the next best move then we kick of a new thread to calculate it.
            ParallelAlphaBetaCalculator bestMoveCalculator = new ParallelAlphaBetaCalculator(board, searchDepth, mySide);
            bestMoveFuture = executorService.submit(bestMoveCalculator);
        }

        System.err.println("Round: " + round + " Hits: " + hit);
        round++;

        // We should cancel any calculations that are ongoing to save on cpu cycles. This won't affect our current
        // thread as it will have been removed already if it came from the nextBestMoveFutures map.
        cancelAndClearAllBestNextMoveFutures();

        // Now we wait for our best move to finish being calculated.
        while (!bestMoveFuture.isDone()) {
        }

        // Our next move is finished calculating so we can just call get() on the future to
        // retrieve our best move.
        Move bestMove = bestMoveFuture.get();

        // Our first turn is over.
        firstTurn = false;

        // Update last move played so we can reference it in the future.
        lastMovePlayed = bestMove;

        // Start precalculating the next best moves so we can respond faster unless it is our turn anyway.
        precalculateNextMoves();

        // And we can create the necessary move message.
        return Protocol.createMoveMsg(bestMove.getHole());
    }

    private void cancelAndClearAllBestNextMoveFutures() {
        // Cancel any futures that were calculating moves for old boards.
        for (Future<Move> moveFuture : bestNextMoveFutures.values()) {
            if (!moveFuture.isCancelled()) {
                moveFuture.cancel(true);
            }
        }

        // Clear the map of all the old futures.
        bestNextMoveFutures.clear();
    }

    private void precalculateNextMoves() {
        // First clone the current board and apply the last move we played as it will not be updated yet.
        Board currentBoard = new Board(board);
        Side nextSide = Kalah.makeMove(currentBoard, lastMovePlayed);

        // If next side is ours again then don't bother because speed up is probably negligible and
        // we don't need to start the extra threads (TODO: test this is actually the case).
        if (nextSide == mySide.opposite()) {
            calculate(currentBoard, nextSide);
        }
    }

    private void calculate(Board board, Side side) {

        // Get a list of all the moves our opponent can make next.
        List<Move> possibleOpponentNextMoves = Kalah.getLegalMoves(board, side);

        // Play out each move and if it will result in us having a move immediately after then we can start
        // precalculating our response.
        for (Move possibleOppNextMove : possibleOpponentNextMoves) {
            Board newBoard = new Board(board);
            Side sideAfterOpponentMove = Kalah.makeMove(newBoard, possibleOppNextMove);
            if (sideAfterOpponentMove == mySide) {
                ParallelAlphaBetaCalculator bestMoveCalculator = new ParallelAlphaBetaCalculator(newBoard, searchDepth, mySide);
                bestNextMoveFutures.put(newBoard, executorService.submit(bestMoveCalculator));
            } else {
                calculate(newBoard, sideAfterOpponentMove);
            }
        }
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public boolean isNextTurn() {
        return isNextTurn;
    }
}
