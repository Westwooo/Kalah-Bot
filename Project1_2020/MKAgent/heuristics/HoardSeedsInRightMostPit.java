package MKAgent.heuristics;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Side;

public class HoardSeedsInRightMostPit extends Heuristic {

    @Override
    public double evaluate(Board board) {
        return board.getSeeds(Side.SOUTH, board.getNoOfHoles());
    }
}
