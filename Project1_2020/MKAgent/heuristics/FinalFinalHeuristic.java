package MKAgent.heuristics;

import MKAgent.gameplay.Board;
import MKAgent.gameplay.Side;

public class FinalFinalHeuristic extends Heuristic {

    @Override
    public double evaluate(Board board) {
        Side side = board.getNextPlayerSide();

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

        int f2, f3;

        if (side == Side.SOUTH)
        {
            f2 = 1;
            f3 = 0;
        }
        else
        {
            f2 = 0;
            f3 = 1;
        }

        int f4 = board.getSeeds(Side.SOUTH, 1);
        int f5 = board.getSeeds(Side.SOUTH, 2);
        int f6 = board.getSeeds(Side.SOUTH, 3);
        int f7 = board.getSeeds(Side.SOUTH, 4);
        int f8 = board.getSeeds(Side.SOUTH, 5);
        int f9 = board.getSeeds(Side.SOUTH, 6);
        int f10 = board.getSeeds(Side.SOUTH, 7);

        int f11 = board.getSeeds(Side.NORTH, 1);
        int f12 = board.getSeeds(Side.NORTH, 2);
        int f13 = board.getSeeds(Side.NORTH, 3);
        int f14 = board.getSeeds(Side.NORTH, 4);
        int f15 = board.getSeeds(Side.NORTH, 5);
        int f16 = board.getSeeds(Side.NORTH, 6);
        int f17 = board.getSeeds(Side.NORTH, 7);

        int f18 = 0, f19 = 0, f20 = 0;
        int f30 = 0;

        int southCaptureOpps[] = new int[8];

        int f21 = 0, f22 = 0, f23 = 0;
        int f31 = 0;

        int northCaptureOpps[] = new int[8];

        int f34 = 0, f35 = 0;

        int temp;

        int f36 = 0, f37 = 0;

        for (int i = 1; i < 8; i++)
        {
            temp = board.getSeeds(Side.SOUTH, i);

            if (temp > f34)
            {
                f34 = temp;
            }

            if (temp == 8 - i)
            {
                f30 += 1;
            }
            else if (temp != 0)
            {
                if (temp + i < 8)
                {
                    f36 += 1;
                    if (board.getSeeds(Side.SOUTH, temp + i) == 0)
                        southCaptureOpps[temp + i] = board.getSeeds(Side.NORTH, 8 - (temp + i)) + 1;
                }
                else if (temp == 15)
                    southCaptureOpps[i] = board.getSeeds(Side.NORTH, 8 - i) + 2;
                else if ((temp + i > 15) && (temp + i < 23))
                {
                    f36 += 1;
                    temp = temp + i - 15;
                    if (board.getSeeds(Side.SOUTH, temp) == 0)
                        southCaptureOpps[temp] = board.getSeeds(Side.NORTH, 8 - temp) + 2;
                }
            }
            else
            {
                f19 += 1;
            }

            temp = board.getSeeds(Side.NORTH, i);

            if (temp > f35)
            {
                f35 = temp;
            }

            if (temp == 8 - i)
            {
                f31 += 1;
            }
            else if (temp != 0)
            {
                if (temp + i < 8)
                {
                    f37 += 1;
                    if (board.getSeeds(Side.NORTH, temp + i) == 0)
                        northCaptureOpps[temp + i] = board.getSeeds(Side.SOUTH, 8 - (temp + i)) + 1;
                }
                else if (temp == 15)
                    northCaptureOpps[i] = board.getSeeds(Side.SOUTH, 8 - i) + 2;
                else if ((temp + i > 15) && (temp + i < 23))
                {
                    f37 += 1;
                    temp = temp + i - 15;
                    if (board.getSeeds(Side.NORTH, temp) == 0)
                        northCaptureOpps[temp] = board.getSeeds(Side.SOUTH, 8 - temp) + 2;
                }
            }
            else
            {
                f22 += 1;
            }

        }

        for (int i = 1; i < 8; i++)
        {
            if (southCaptureOpps[i] != 0)
            {
                f18 += 1;
            }

            f20 += southCaptureOpps[i];


            if (northCaptureOpps[i] != 0)
            {
                f21 += 1;
            }

            f23 += northCaptureOpps[i];
        }

        int f24 = f18 * f2;
        int f25 = f19 * f2;
        int f26 = f20 * f2;

        int f27 = f21 * f3;
        int f28 = f22 * f3;
        int f29 = f23 * f3;

        int f32 = f30 * f2;
        int f33 = f31 * f3;

        int f0Weight  = 10422;
        int f1Weight  = -9352;
        int f2Weight  = 14361;
        int f3Weight  = -14349;
        int f4Weight  = -1094;
        int f5Weight  = -606;
        int f6Weight  = -695;
        int f7Weight  = -659;
        int f8Weight  = -169;
        int f9Weight  = -44;
        int f10Weight = 1163;
        int f11Weight = 1171;
        int f12Weight = 106;
        int f13Weight = 628;
        int f14Weight = 495;
        int f15Weight = 585;
        int f16Weight = 32;
        int f17Weight = -809;
        int f18Weight = -5764;
        int f19Weight = -1383;
        int f20Weight = 964;
        int f21Weight = -2152;
        int f22Weight = -7901;
        int f23Weight = -553;
        int f24Weight = -10788;
        int f25Weight = -4608;
        int f26Weight = 5781;
        int f27Weight = 10797;
        int f28Weight = 3189;
        int f29Weight = -6083;
        int f30Weight = 10191;
        int f31Weight = -17354;
        int f32Weight = 20718;
        int f33Weight = -18493;
        int f34Weight = 1606;
        int f35Weight = -466;
        int f36Weight = 12046;
        int f37Weight = -11414;

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
                (f30 * f30Weight) +
                (f31 * f31Weight) +
                (f32 * f32Weight) +
                (f33 * f33Weight) +
                (f34 * f34Weight) +
                (f35 * f35Weight) +
                (f36 * f36Weight) +
                (f37 * f37Weight)
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

