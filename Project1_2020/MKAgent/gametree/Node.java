package MKAgent.gametree;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Kalah;
import MKAgent.gameplay.Move;
import MKAgent.gameplay.Side;
import MKAgent.heuristics.BlockchainNeuralNetHeuristic;
import MKAgent.heuristics.QuestionableHeuristic;
import MKAgent.heuristics.Heuristic;

import java.util.ArrayList;
import java.util.List;

public class Node {

    // This is the heuristic function that is used evaluate board states
    // (defaults to a basic a heuristic written by safian).
    private static Heuristic boardEvaluationFunction = new BlockchainNeuralNetHeuristic();
    private static Heuristic sortingBoardEvaluationFunction = new QuestionableHeuristic();

    // Board associated with node.
    private final Board board;

    // Children of node.
    private final List<Node> children = new ArrayList<Node>();

    // Depth that this (sub)tree needs to have after tree has been constructed.
    private int depthRemaining;

    // Score of leaf node if bestMove is chosen all the way down the tree.
    private double bestScore;

    private Move bestMove;

    public Node(Board board, Side side, int depthRemaining, double alpha, double beta) {
        this.board = board;

        // Get all possible legal moves for the current side on our board.
        List<Move> legalMoves = Kalah.getLegalMoves(board, side);

        // If we have reached our maximum search depth or have no legal moves left to play
        // then we evaluate the board state.
        if (depthRemaining <= 0 || legalMoves.isEmpty()) {
            bestScore = boardEvaluationFunction.evaluate(board);
            return;
        }

        // We want to maximise for the south side and minimise for the north side.
        bestScore = (side == Side.SOUTH) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        // Sort nodes to improve alpha beta pruning.
        legalMoves = sortMoves(legalMoves, board);

        for (Move move : legalMoves) {

            // For each move, clone the board and try that move on our new board.
            Board newBoard = new Board(board);
            Side newSide = Kalah.makeMove(newBoard, move);
            Node newNode = new Node(newBoard, newSide, depthRemaining - 1, alpha, beta);
            children.add(newNode);

            if (side == Side.SOUTH) {
                // If the current move was made by the south side then we're maximising.
                if (newNode.getBestScore() > bestScore) {
                    bestScore = newNode.getBestScore();
                    bestMove = move;
                }
                alpha = Math.max(alpha, newNode.getBestScore());
            } else {
                // If the current move was made by the north side then we're minimising.
                if (newNode.getBestScore() < bestScore) {
                    bestScore = newNode.getBestScore();
                    bestMove = move;
                }
                beta = Math.min(beta, newNode.getBestScore());
            }

            // If beta <= alpha then we prune.
            if (beta <= alpha) break;
        }
    }

    private List<Move> sortMoves(List<Move> unsortedMoves, Board board) {
        double[] moveValues = new double[unsortedMoves.size()];
        int[] moveIndex = new int[unsortedMoves.size()];
        List<Move> sorted = new ArrayList<Move>();
        Double score;
        int index = 0;
        for (Move move : unsortedMoves) {
            Board newBoard = new Board(board);
            Kalah.makeMove(newBoard, move);
            score = sortingBoardEvaluationFunction.evaluate(newBoard);
            moveValues[index] = score;
            moveIndex[index] = index;
            index++;
        }
        sort(moveValues, moveIndex, 0, index - 1);
        if (!(unsortedMoves.get(0).getSide() == Side.SOUTH)) {
            for (int i = 0; i < index; i++)
                sorted.add(unsortedMoves.get(moveIndex[i]));
        } else {
            for (int i = index - 1; i >= 0; i--)
                sorted.add(unsortedMoves.get(moveIndex[i]));
        }
        //System.err.println(moveValues);
        return sorted;
    }

    int partition(double[] arr, int[] indexes, int low, int high) {
        double pivot = arr[high];
        int i = (low - 1); // index of smaller element
        for (int j = low; j < high; j++) {
            // If current element is smaller than the pivot
            if (arr[j] < pivot) {
                i++;

                // swap arr[i] and arr[j]
                double temp = arr[i];
                int temp2 = indexes[i];
                arr[i] = arr[j];
                indexes[i] = indexes[j];
                arr[j] = temp;
                indexes[j] = temp2;
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        double temp = arr[i + 1];
        int temp2 = indexes[i + 1];
        arr[i + 1] = arr[high];
        indexes[i + 1] = indexes[high];
        arr[high] = temp;
        indexes[high] = temp2;

        return i + 1;
    }

    void sort(double[] arr, int[] indexes, int low, int high) {
        if (low < high) {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(arr, indexes, low, high);

            // Recursively sort elements before
            // partition and after partition
            sort(arr, indexes, low, pi - 1);
            sort(arr, indexes, pi + 1, high);
        }
    }

    public double getBestScore() {
        return bestScore;
    }

    public Move getBestMove() {
        return bestMove;
    }

}
