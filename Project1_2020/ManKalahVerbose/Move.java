// 
// Decompiled by Procyon v0.5.36
// 

package ManKalahVerbose;

public class Move {
    private final Side side;
    private final int hole;

    public Move(final Side side, final int hole) throws IllegalArgumentException {
        if (hole < 1) {
            throw new IllegalArgumentException("Hole numbers must be >= 1, but " + hole + " was given.");
        }
        this.side = side;
        this.hole = hole;
    }

    public Side getSide() {
        return this.side;
    }

    public int getHole() {
        return this.hole;
    }
}
