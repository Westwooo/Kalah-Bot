// 
// Decompiled by Procyon v0.5.36
// 

package ManKalahVerbose;

import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;

public class PrintingBoardObserver implements Observer {
    protected PrintStream outputStream;

    public PrintingBoardObserver(final PrintStream outputStream) {
        if (outputStream == null) {
            throw new NullPointerException();
        }
        this.outputStream = outputStream;
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final Board board = (Board) o;
        if (arg != null && arg instanceof Move) {
            final Move move = (Move) arg;
            this.outputStream.print("Move: ");
            switch (move.getSide()) {
                case NORTH: {
                    this.outputStream.print("North");
                    break;
                }
                case SOUTH: {
                    this.outputStream.print("South");
                    break;
                }
            }
            this.outputStream.println(" - " + move.getHole());
        }
        this.outputStream.print(board);
    }
}
