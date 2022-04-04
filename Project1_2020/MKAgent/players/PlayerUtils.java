package MKAgent.players;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Kalah;
import MKAgent.gameplay.Move;
import MKAgent.gameplay.Side;
import MKAgent.heuristics.Heuristic;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtils {

    public static List<Move> sortMovesForBoard(List<Move> unsortedMoves, Board board, Heuristic heuristic) {
        double[] moveValues = new double[unsortedMoves.size()];
        int[] moveIndex = new int[unsortedMoves.size()];
        List<Move> sorted = new ArrayList<Move>();
        Double score;
        int index = 0;
        for (Move move : unsortedMoves) {
            Board newBoard = new Board(board);
            Kalah.makeMove(newBoard, move);
            score = heuristic.evaluate(newBoard);
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

    private static int partition(double[] arr, int[] indexes, int low, int high) {
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

    private static void sort(double[] arr, int[] indexes, int low, int high) {
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

}
