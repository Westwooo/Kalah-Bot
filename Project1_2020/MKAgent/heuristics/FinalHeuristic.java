package MKAgent.heuristics;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Side;

public class FinalHeuristic extends Heuristic {

    @Override
    public double evaluate(Board board) {

        int f0 = board.getSeedsInStore(Side.SOUTH);

        if (f0 > 49)
        {
            return Integer.MAX_VALUE;
        }

        int f1 = board.getSeedsInStore(Side.NORTH);

        if (f1 > 49)
        {
            return Integer.MIN_VALUE;
        }

        int f2 = board.getSeeds(Side.SOUTH, 1);
        int f3 = board.getSeeds(Side.SOUTH, 2);
        int f4 = board.getSeeds(Side.SOUTH, 3);
        int f5 = board.getSeeds(Side.SOUTH, 4);
        int f6 = board.getSeeds(Side.SOUTH, 5);
        int f7 = board.getSeeds(Side.SOUTH, 6);
        int f8 = board.getSeeds(Side.SOUTH, 7);

        int f9 = board.getSeeds(Side.NORTH, 1);
        int f10 = board.getSeeds(Side.NORTH, 2);
        int f11 = board.getSeeds(Side.NORTH, 3);
        int f12 = board.getSeeds(Side.NORTH, 4);
        int f13 = board.getSeeds(Side.NORTH, 5);
        int f14 = board.getSeeds(Side.NORTH, 6);
        int f15 = board.getSeeds(Side.NORTH, 7);

        int f16 = 0, f17 = 0, f18 = -1;

        int f19 = 0, f20 = 0, f21 = -1;

        for (int i = 1; i < 8; i++)
        {
            if (board.getSeeds(Side.SOUTH, i) == 0)
            {
                f16 += 1;
                f17 += board.getSeeds(Side.NORTH, 8 - i);
            }

            if (board.getSeeds(Side.SOUTH, i) > f18)
            {
                f18 = board.getSeeds(Side.SOUTH, i);
            }

            if (board.getSeeds(Side.NORTH, i) == 0)
            {
                f19 += 1;
                f20 += board.getSeeds(Side.SOUTH, 8 - i);
            }

            if (board.getSeeds(Side.NORTH, i) > f21)
            {
                f21 = board.getSeeds(Side.NORTH, i);
            }
        }

        int f22 = 0, f23 = 0, southTemp;

        int f24 = 0, f25 = 0, northTemp;

        for (int i = 1; i < 8; i++)
        {
            southTemp = board.getSeeds(Side.SOUTH, i) % 15;

            if (southTemp == (8 - i))
                f22 += 1;

            if ((southTemp + i) < 8)
                f23 += 1;
            else if ((southTemp + i) > 15)
                f23 += 1;

            northTemp = board.getSeeds(Side.NORTH, i) % 15;

            if (northTemp == (8 - i))
                f24 += 1;

            if ((northTemp + i) < 8)
                f25 += 1;
            else if ((northTemp + i) > 15)
                f25 += 1;
        }

        int f0Weight =  9999;
        int f1Weight =  -10038;
        int f2Weight =  573;
        int f3Weight =  650;
        int f4Weight =  587;
        int f5Weight =  373;
        int f6Weight =  -114;
        int f7Weight =  171;
        int f8Weight =  1293;
        int f9Weight =  -694;
        int f10Weight = -580;
        int f11Weight = -326;
        int f12Weight = -23;
        int f13Weight = 86;
        int f14Weight = -103;
        int f15Weight = -871;
        int f16Weight = -22267;
        int f17Weight = 173;
        int f18Weight = 163;
        int f19Weight = 22867;
        int f20Weight = -316;
        int f21Weight = -443;
        int f22Weight = 25917;
        int f23Weight = 19881;
        int f24Weight = -26870;
        int f25Weight = -19791;
        int f26Weight = 627;
        int f27Weight = 162;
        int f28Weight = -2654;
        int f29Weight = -1874;
        int f30Weight = -2718;
        int f31Weight = -3241;
        int f32Weight = -3626;
        int f33Weight = -5387;
        int f34Weight = -6126;
        int f35Weight = 20985;
        int f36Weight = -6347;
        int f37Weight = -10689;
        int f38Weight = -9566;
        int f39Weight = -1767;
        int f40Weight = -957;
        int f41Weight = 3322;
        int f42Weight = 7380;
        int f43Weight = -18624;
        int f44Weight = -88;
        int f45Weight = -126;
        int f46Weight = 107;
        int f47Weight = 331;
        int f48Weight = 481;
        int f49Weight = 163;
        int f50Weight = -742;
        int f51Weight = -1393;
        int f52Weight = 126;
        int f53Weight = 35;




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
                (f25 * f25Weight)
//                (f26 * f26Weight) +
//                (f27 * f27Weight) +
//                (f28 * f28Weight) +
//                (f29 * f29Weight) +
//                (f30 * f30Weight) +
//                (f31 * f31Weight) +
//                (f32 * f32Weight) +
//                (f33 * f33Weight) +
//                (f34 * f34Weight) +
//                (f35 * f35Weight) +
//                (f36 * f36Weight) +
//                (f37 * f37Weight) +
//                (f38 * f38Weight) +
//                (f39 * f39Weight) +
//                (f40 * f40Weight) +
//                (f41 * f41Weight) +
//                (f42 * f42Weight) +
//                (f43 * f43Weight) +
//                (f44 * f44Weight) +
//                (f45 * f45Weight) +
//                (f46 * f46Weight) +
//                (f47 * f47Weight) +
//                (f48 * f48Weight) +
//                (f49 * f49Weight) +
//                (f50 * f50Weight) +
//                (f51 * f51Weight) +
//                (f52 * f52Weight) +
//                (f53 * f53Weight)
        );

    }
}
