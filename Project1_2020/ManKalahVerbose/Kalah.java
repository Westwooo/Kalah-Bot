// 
// Decompiled by Procyon v0.5.36
// 

package ManKalahVerbose;

public class Kalah {
    private final Board board;

    public Kalah(final Board board) throws NullPointerException {
        if (board == null) {
            throw new NullPointerException();
        }
        this.board = board;
    }

    public static boolean isLegalMove(final Board board, final Move move) {
        return move.getHole() <= board.getNoOfHoles() && board.getSeeds(move.getSide(), move.getHole()) != 0;
    }

    public static Side makeMove(final Board board, final Move move) {
        final int seedsToSow = board.getSeeds(move.getSide(), move.getHole());
        board.setSeeds(move.getSide(), move.getHole(), 0);
        final int holes = board.getNoOfHoles();
        final int receivingPits = 2 * holes + 1;
        final int rounds = seedsToSow / receivingPits;
        int extra = seedsToSow % receivingPits;
        if (rounds != 0) {
            for (int hole = 1; hole <= holes; ++hole) {
                board.addSeeds(Side.NORTH, hole, rounds);
                board.addSeeds(Side.SOUTH, hole, rounds);
            }
            board.addSeedsToStore(move.getSide(), rounds);
        }
        Side sowSide = move.getSide();
        int sowHole = move.getHole();
        while (extra > 0) {
            if (++sowHole == 1) {
                sowSide = sowSide.opposite();
            }
            Label_0180:
            {
                if (sowHole > holes) {
                    if (sowSide == move.getSide()) {
                        sowHole = 0;
                        board.addSeedsToStore(sowSide, 1);
                        break Label_0180;
                    }
                    sowSide = sowSide.opposite();
                    sowHole = 1;
                }
                board.addSeeds(sowSide, sowHole, 1);
            }
            --extra;
        }
        if (sowSide == move.getSide() && sowHole > 0 && board.getSeeds(sowSide, sowHole) == 1 && board.getSeedsOp(sowSide, sowHole) > 0) {
            board.addSeedsToStore(move.getSide(), 1 + board.getSeedsOp(move.getSide(), sowHole));
            board.setSeeds(move.getSide(), sowHole, 0);
            board.setSeedsOp(move.getSide(), sowHole, 0);
        }
        Side finishedSide = null;
        if (holesEmpty(board, move.getSide())) {
            finishedSide = move.getSide();
        } else if (holesEmpty(board, move.getSide().opposite())) {
            finishedSide = move.getSide().opposite();
        }
        if (finishedSide != null) {
            int seeds = 0;
            final Side collectingSide = finishedSide.opposite();
            for (int hole2 = 1; hole2 <= holes; ++hole2) {
                seeds += board.getSeeds(collectingSide, hole2);
                board.setSeeds(collectingSide, hole2, 0);
            }
            board.addSeedsToStore(collectingSide, seeds);
        }
        board.notifyObservers(move);
        if (sowHole == 0) {
            return move.getSide();
        }
        return move.getSide().opposite();
    }

    protected static boolean holesEmpty(final Board board, final Side side) {
        for (int hole = 1; hole <= board.getNoOfHoles(); ++hole) {
            if (board.getSeeds(side, hole) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean gameOver(final Board board) {
        return holesEmpty(board, Side.NORTH) || holesEmpty(board, Side.SOUTH);
    }

    public Board getBoard() {
        return this.board;
    }

    public boolean isLegalMove(final Move move) {
        return isLegalMove(this.board, move);
    }

    public Side makeMove(final Move move) {
        return makeMove(this.board, move);
    }

    public boolean gameOver() {
        return gameOver(this.board);
    }
}
