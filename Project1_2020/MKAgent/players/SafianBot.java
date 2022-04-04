package MKAgent.players;

import MKAgent.gameplay.*;
import MKAgent.gametree.Node;

public class SafianBot implements Bot {

    private final static int SWAP_MOVE = -1;

    private final Board board;
    private final Kalah kalah;
    private final int searchDepth;
    private Side mySide;
    private boolean gameOver = false;
    private boolean isNextTurn;
    private boolean firstTurn = true;

    public SafianBot(int holes, int seeds, int searchDepth) {
        board = new Board(holes, seeds);
        kalah = new Kalah(board);
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

    @Override
    public String getNextMove() throws Exception {
        // We always want to be south?
        if (firstTurn && mySide == Side.NORTH) {
            firstTurn = false;
            mySide = mySide.opposite();
            return Protocol.createSwapMsg();
        }

        // Once we make our first turn we set this flag to false.
        firstTurn = false;

        Board copyOfBoard = new Board(board);
        Node root = new Node(
                copyOfBoard,
                mySide,
                searchDepth,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY
        );

        return Protocol.createMoveMsg(root.getBestMove().getHole());
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
