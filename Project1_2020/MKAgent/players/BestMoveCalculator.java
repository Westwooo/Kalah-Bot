package MKAgent.players;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Kalah;
import MKAgent.gameplay.Move;
import MKAgent.gameplay.Side;
import MKAgent.heuristics.FinalHeuristic;
import MKAgent.heuristics.Heuristic;

import java.util.List;
import java.util.concurrent.Callable;

class BestMoveCalculator implements Callable<Move> {

    private static Heuristic boardEvaluationFunction = new FinalHeuristic();
    private static Heuristic sortingBoardEvaluationFunction = new FinalHeuristic();

    private final Board board;
    private final int searchDepth;
    private final Side mySide;
    private Move bestMove = null;

    public BestMoveCalculator(Board board, int searchDepth, Side mySide) {
        this.board = board;
        this.searchDepth = searchDepth;
        this.mySide = mySide;
    }

    @Override
    public Move call() throws Exception {
        try {
            alphaBeta(board, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, searchDepth, mySide);
        } catch (Exception ex) {
            return null;
        }
        return bestMove;
    }

    private double alphaBeta(Board board, double alpha, double beta, int depth, Side side) {
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException();
        }

        List<Move> legalMoves = Kalah.getLegalMoves(board, side);
        List<Move> sortedMoves;
        Move bestMove;
        double value;
        double temp;

        if (depth == 0 || legalMoves.isEmpty()) {
            temp = boardEvaluationFunction.evaluate(board);
            return temp;
        }

        value = (side == Side.SOUTH) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        sortedMoves = PlayerUtils.sortMovesForBoard(legalMoves, board, sortingBoardEvaluationFunction);

        bestMove = legalMoves.get(0);
        for (Move move : sortedMoves) {
            Board newBoard = new Board(board);
            Side newSide = Kalah.makeMove(newBoard, move);

            if (side == Side.SOUTH) {

                temp = alphaBeta(newBoard, alpha, beta, depth - 1, newSide);
                if (temp > value) {
                    value = temp;
                    bestMove = move;
                }
                alpha = Math.max(alpha, value);
                if (beta <= value)
                    break;
            } else {

                temp = alphaBeta(newBoard, alpha, beta, depth - 1, newSide);
                if (temp < value) {
                    value = temp;
                    bestMove = move;
                }
                beta = Math.min(beta, value);
                if (value <= alpha)
                    break;
            }
        }

        if (depth == searchDepth)
            this.bestMove = bestMove;

        return value;
    }

}

