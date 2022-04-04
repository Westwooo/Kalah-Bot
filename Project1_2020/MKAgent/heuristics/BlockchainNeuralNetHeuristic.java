package MKAgent.heuristics;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Side;

public class BlockchainNeuralNetHeuristic extends Heuristic {

    @Override
    public double evaluate(Board board) {

        int northSeeds = board.getSeedsInStore(Side.NORTH);

        if (northSeeds > 49) {
            return Integer.MIN_VALUE;
        }

        int southSeeds = board.getSeedsInStore(Side.SOUTH);

        if (southSeeds > 49) {
            return Integer.MAX_VALUE;
        }

        int f0 = southSeeds - northSeeds;

        int f1 = 0;
        for (int i = 1; i <= board.getNoOfHoles(); i++) {
            if (board.getSeeds(Side.NORTH, i) == 0) {
                f1 -= 1;
            }

            if (board.getSeeds(Side.SOUTH, i) == 0) {
                f1 += 1;
            }
        }

        int f2 = f0;

        for (int i = 1; i <= board.getNoOfHoles(); i++) {
            f2 -= board.getSeeds(Side.NORTH, i);
            f2 += board.getSeeds(Side.SOUTH, i);
        }

        int f3 = board.getSeeds(Side.NORTH, 1);
        int f4 = board.getSeeds(Side.NORTH, 2);
        int f5 = board.getSeeds(Side.NORTH, 3);
        int f6 = board.getSeeds(Side.NORTH, 4);
        int f7 = board.getSeeds(Side.NORTH, 5);
        int f8 = board.getSeeds(Side.NORTH, 6);
        int f9 = board.getSeeds(Side.NORTH, 7);

        int f10 = board.getSeeds(Side.SOUTH, 1);
        int f11 = board.getSeeds(Side.SOUTH, 2);
        int f12 = board.getSeeds(Side.SOUTH, 3);
        int f13 = board.getSeeds(Side.SOUTH, 4);
        int f14 = board.getSeeds(Side.SOUTH, 5);
        int f15 = board.getSeeds(Side.SOUTH, 6);
        int f16 = board.getSeeds(Side.SOUTH, 7);

        int f17 = (board.getSeeds(Side.NORTH, 1) == 7) ? 1 : 0;
        int f18 = (board.getSeeds(Side.NORTH, 2) == 6) ? 1 : 0;
        int f19 = (board.getSeeds(Side.NORTH, 3) == 5) ? 1 : 0;
        int f20 = (board.getSeeds(Side.NORTH, 4) == 4) ? 1 : 0;
        int f21 = (board.getSeeds(Side.NORTH, 5) == 3) ? 1 : 0;
        int f22 = (board.getSeeds(Side.NORTH, 6) == 2) ? 1 : 0;
        int f23 = (board.getSeeds(Side.NORTH, 7) == 1) ? 1 : 0;

        int f24 = (board.getSeeds(Side.SOUTH, 1) == 7) ? 1 : 0;
        int f25 = (board.getSeeds(Side.SOUTH, 2) == 6) ? 1 : 0;
        int f26 = (board.getSeeds(Side.SOUTH, 3) == 5) ? 1 : 0;
        int f27 = (board.getSeeds(Side.SOUTH, 4) == 4) ? 1 : 0;
        int f28 = (board.getSeeds(Side.SOUTH, 5) == 3) ? 1 : 0;
        int f29 = (board.getSeeds(Side.SOUTH, 6) == 2) ? 1 : 0;
        int f30 = (board.getSeeds(Side.SOUTH, 7) == 1) ? 1 : 0;

        int f0Weight = 90712;
        int f1Weight = -128895;
        int f2Weight = 8149;
        int f3Weight = 7241;
        int f4Weight = 8779;
        int f5Weight = 4322;
        int f6Weight = 9899;
        int f7Weight = 7648;
        int f8Weight = 15989;
        int f9Weight = -11565;
        int f10Weight = -3838;
        int f11Weight = -11490;
        int f12Weight = -10393;
        int f13Weight = -5728;
        int f14Weight = -8655;
        int f15Weight = -3401;
        int f16Weight = 3258;
        int f17Weight = -287816;
        int f18Weight = -222225;
        int f19Weight = -173298;
        int f20Weight = -289551;
        int f21Weight = -244525;
        int f22Weight = -69074;
        int f23Weight = -107388;
        int f24Weight = 279874;
        int f25Weight = 367245;
        int f26Weight = 251856;
        int f27Weight = 146304;
        int f28Weight = 105534;
        int f29Weight = 178869;
        int f30Weight = 79834;


        return ((f0 * f0Weight) +
                (f1 * f1Weight) +
                (f2 * f2Weight) +
                (f3 * f3Weight) +
                (f4 * f4Weight) +
                (f5 * f5Weight) +
                (f6 * f6Weight) +
                (f7 * f7Weight) +
                (f8 * f8Weight) +
                (f9 * f9Weight) +
                (f10 * f10Weight) +
                (f11 * f11Weight) +
                (f12 * f12Weight) +
                (f13 * f13Weight) +
                (f14 * f14Weight) +
                (f15 * f15Weight) +
                (f16 * f16Weight) +
                (f17 * f17Weight) +
                (f18 * f18Weight) +
                (f19 * f19Weight) +
                (f20 * f20Weight) +
                (f21 * f21Weight) +
                (f22 * f22Weight) +
                (f23 * f23Weight) +
                (f24 * f24Weight) +
                (f25 * f25Weight) +
                (f26 * f26Weight) +
                (f27 * f27Weight) +
                (f28 * f28Weight) +
                (f29 * f29Weight) +
                (f30 * f30Weight)
        );

    }
}