package MKAgent;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Kalah;
import MKAgent.gameplay.Move;
import MKAgent.gameplay.Side;
import MKAgent.heuristics.BlockchainNeuralNetHeuristic;
import MKAgent.heuristics.Heuristic;
import MKAgent.heuristics.QuestionableHeuristic;

import java.util.List;

public class Test {

    public static void main(String args[]) {
        Heuristic safH = new BlockchainNeuralNetHeuristic();
        Heuristic paperH = new QuestionableHeuristic();

        Board board = buildBoard();
        System.out.println(board);

        Move maxMove = null;
        double maxScore = Double.NEGATIVE_INFINITY;

        Move minMove = null;
        double minScore = Double.POSITIVE_INFINITY;

        Side playingSide = Side.SOUTH;

        List<Move> legalMoves = Kalah.getLegalMoves(board, playingSide);
        for (Move move : legalMoves) {
            Board newBoard = new Board(board);
            Kalah.makeMove(board, move);

            double safHScore = safH.evaluate(newBoard);
            double paperHScore = paperH.evaluate(newBoard);

            if(safHScore > maxScore) {
                maxScore = safHScore;
                maxMove = move;
            }

            if(safHScore < minScore) {
                minScore = safHScore;
                minMove = move;
            }

            System.out.println("Move: " + move.getSide().toString() + " - " + move.getHole());
            System.out.println("  BlockChainNeuralNetHeuristic: " + safHScore);
            System.out.println("  QuestionableHeuristic: " + paperHScore);
        }

        System.out.println("Max Move: " + maxMove);
        System.out.println("Max Move: " + minMove);
    }

    private static Board buildBoard() {
        String northRow = "38  --  0  10  1  0  1  2  0";
        String southRow = "2  0  0  0  1  1  10  --  32";

        Board board = new Board(7, 7);

        String[] northParts = northRow.split("\\s\\s");
        board.setSeedsInStore(Side.NORTH, Integer.parseInt(northParts[0]));
        for (int i = 2; i < northParts.length; i++) {
            board.setSeeds(Side.NORTH, 8 - (i - 1), Integer.parseInt(northParts[i]));
        }

        String[] southParts = southRow.split("  ");
        board.setSeedsInStore(Side.SOUTH, Integer.parseInt(southParts[8]));
        for (int i = 0; i < southParts.length - 2; i++) {
            board.setSeeds(Side.SOUTH, i + 1, Integer.parseInt(southParts[i]));
        }

        return board;
    }

}
