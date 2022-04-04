package MKAgent.heuristics;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Side;

public class KeepMovesOnMySideHeuristic extends Heuristic {

    @Override
    public double evaluate(Board board) {
        int seedsOnSouthRow = 0;

        //calculates number of seeds on either row including seeds in store
        for (int i = 1; i <= board.getNoOfHoles(); i++) {
            seedsOnSouthRow += board.getSeeds(Side.SOUTH, i);
        }

        seedsOnSouthRow += board.getSeedsInStore(Side.SOUTH);

        return seedsOnSouthRow;
    }
}
