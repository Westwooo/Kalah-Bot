// 
// Decompiled by Procyon v0.5.36
// 

package ManKalahVerbose;

public enum Side {
    NORTH("NORTH", 0),
    SOUTH("SOUTH", 1);

    Side(final String name, final int ordinal) {
    }

    public Side opposite() {
        switch (this) {
            case NORTH: {
                return Side.SOUTH;
            }
            case SOUTH: {
                return Side.NORTH;
            }
            default: {
                return Side.NORTH;
            }
        }
    }
}
