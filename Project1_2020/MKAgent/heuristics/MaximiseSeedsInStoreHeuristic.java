package MKAgent.heuristics;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Side;

public class MaximiseSeedsInStoreHeuristic extends Heuristic {

    @Override
    public double evaluate(Board board) {
        return board.getSeedsInStore(Side.SOUTH);
    }

}
