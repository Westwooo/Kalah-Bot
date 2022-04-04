package MKAgent.heuristics;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Kalah;
import MKAgent.gameplay.Side;

public class HaveManyMovesHeuristic extends Heuristic {

    @Override
    public double evaluate(Board board) {
        return Kalah.getLegalMoves(board, Side.SOUTH).size();
    }

}
