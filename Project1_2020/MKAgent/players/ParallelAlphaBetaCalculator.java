package MKAgent.players;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Kalah;
import MKAgent.gameplay.Move;
import MKAgent.gameplay.Side;
import MKAgent.heuristics.FinalFinalHeuristic;
import MKAgent.heuristics.FinalHeuristic;
import MKAgent.heuristics.Heuristic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class ParallelAlphaBetaCalculator implements Callable<Move> {

    private static Heuristic boardEvaluationFunction = new FinalHeuristic();
    private static Heuristic sortingBoardEvaluationFunction = new FinalFinalHeuristic();

    private final Board board;
    private final int searchDepth;
    private final Side mySide;
    private Move bestMove = null;

    public ParallelAlphaBetaCalculator(Board board, int searchDepth, Side mySide) {
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

        bestMove = sortedMoves.get(0);

        if (side == Side.SOUTH) {

            if (depth == searchDepth - 2) {
                Move firstMove = sortedMoves.get(0);
                Board firstMoveBoard = new Board(board);
                Side newSide = Kalah.makeMove(firstMoveBoard, firstMove);

                // Evaluate the first child first.
                temp = alphaBeta(firstMoveBoard, alpha, beta, depth - 1, newSide);
                if (temp > value) { //NOTE update?
                    value = temp;
                    bestMove = firstMove;
                }
                if (value > alpha) alpha = value;

                if (beta > value) {
                    // Evaluate the siblings next with improved bounds!
                    List<CompletableFuture<Double>> siblingMoveValueFutures = new ArrayList<>(sortedMoves.size());
                    final double alphaCopy = alpha;
                    final double betaCopy = beta;
                    for (int i = 1; i < sortedMoves.size(); i++) {
                        Move siblingMove = sortedMoves.get(i);
                        Board siblingMoveBoard = new Board(board);
                        Side nextSide = Kalah.makeMove(siblingMoveBoard, siblingMove);
                        siblingMoveValueFutures.add(
                                CompletableFuture.supplyAsync(
                                        () -> alphaBeta(siblingMoveBoard, alphaCopy, betaCopy, depth - 1, nextSide)
                                )
                        );
                    }

                    // Find all the sibling values and update value, alpha and bestMove if necessary.
                    for (int i = 0; i < siblingMoveValueFutures.size(); i++) {
                        CompletableFuture<Double> siblingValueFuture = siblingMoveValueFutures.get(i);
                        if (siblingValueFuture != null && !siblingValueFuture.isCancelled()) {
                            double siblingValue = siblingValueFuture.join();
                            if (siblingValue > value) {
                                value = siblingValue;
                                bestMove = sortedMoves.get(i + 1);
                            }
                            if (value > alpha) alpha = value;
                            if (beta <= value)
                                break;
                        }
                    }

                    for (CompletableFuture<Double> futureValue : siblingMoveValueFutures) {
                        if (!futureValue.isCancelled()) {
                            futureValue.cancel(true);
                        }
                    }
                }
            } else {
                for (Move move : sortedMoves) {
                    Board newBoard = new Board(board);
                    Side newSide = Kalah.makeMove(newBoard, move);

                    temp = alphaBeta(newBoard, alpha, beta, depth - 1, newSide);
                    if (temp > value) {
                        value = temp;
                        bestMove = move;
                    }
                    if (value > alpha) alpha = value;
                    if (beta <= value)
                        break;
                }
            }

        } else {

            if (depth == searchDepth - 2) {
                Move firstMove = sortedMoves.get(0);
                Board firstMoveBoard = new Board(board);
                Side newSide = Kalah.makeMove(firstMoveBoard, firstMove);

                // Evaluate the first child first.
                temp = alphaBeta(firstMoveBoard, alpha, beta, depth - 1, newSide);
                if (temp < value) {
                    value = temp;
                    bestMove = firstMove;
                }
                if (value < beta) beta = value;

                if (value > alpha) {
                    // Evaluate the siblings next with improved bounds!
                    List<CompletableFuture<Double>> siblingMoveValueFutures = new ArrayList<>(sortedMoves.size());
                    final double alphaCopy = alpha;
                    final double betaCopy = beta;
                    for (int i = 1; i < sortedMoves.size(); i++) {
                        Move siblingMove = sortedMoves.get(i);
                        Board siblingMoveBoard = new Board(board);
                        Side nextSide = Kalah.makeMove(siblingMoveBoard, siblingMove);
                        siblingMoveValueFutures.add(
                                CompletableFuture.supplyAsync(
                                        () -> alphaBeta(siblingMoveBoard, alphaCopy, betaCopy, depth - 1, nextSide)
                                )
                        );
                    }

                    // Find all the sibling values and update value, beta and bestMove if necessary.
                    for (int i = 0; i < siblingMoveValueFutures.size(); i++) {
                        CompletableFuture<Double> siblingValueFuture = siblingMoveValueFutures.get(i);
                        if (siblingValueFuture != null && !siblingValueFuture.isCancelled()) {
                            double siblingValue = siblingValueFuture.join();
                            if (siblingValue < value) {
                                value = siblingValue;
                                bestMove = sortedMoves.get(i + 1);
                            }
                            if (value < beta) beta = value;
                            if (value <= alpha)
                                break;
                        }
                    }

                    for (CompletableFuture<Double> futureValue : siblingMoveValueFutures) {
                        if (!futureValue.isCancelled()) {
                            futureValue.cancel(true);
                        }
                    }
                }
            } else {
                for (Move move : sortedMoves) {
                    Board newBoard = new Board(board);
                    Side newSide = Kalah.makeMove(newBoard, move);
                    temp = alphaBeta(newBoard, alpha, beta, depth - 1, newSide);
                    if (temp < value) {
                        value = temp;
                        bestMove = move;
                    }
                    if (value < beta) beta = value;
                    if (value <= alpha)
                        break;
                }
            }
        }

        if (depth == searchDepth)
            this.bestMove = bestMove;

        return value;
    }

}
