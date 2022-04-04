// 
// Decompiled by Procyon v0.5.36
// 

package ManKalahVerbose;

public class Protocol {
    public static String createStartMsg(final Side side) {
        if (side == Side.NORTH) {
            return "START;North\n";
        }
        return "START;South\n";
    }

    public static String createStateMsg(final Move move, final Board board, final boolean end, final boolean turn) {
        final StringBuilder message = new StringBuilder("CHANGE;" + move.getHole() + ";");
        for (int i = 1; i <= board.getNoOfHoles(); ++i) {
            message.append(board.getSeeds(Side.NORTH, i));
            message.append(",");
        }
        message.append(board.getSeedsInStore(Side.NORTH));
        message.append(",");
        for (int i = 1; i <= board.getNoOfHoles(); ++i) {
            message.append(board.getSeeds(Side.SOUTH, i));
            message.append(",");
        }
        message.append(board.getSeedsInStore(Side.SOUTH));
        message.append(";");
        if (end) {
            message.append("END");
        } else if (turn) {
            message.append("YOU");
        } else {
            message.append("OPP");
        }
        message.append("\n");
        return message.toString();
    }

    public static String createSwapInfoMsg(final Board board) {
        final StringBuilder message = new StringBuilder("CHANGE;SWAP;");
        for (int i = 1; i <= board.getNoOfHoles(); ++i) {
            message.append(board.getSeeds(Side.NORTH, i));
            message.append(",");
        }
        message.append(board.getSeedsInStore(Side.NORTH));
        message.append(",");
        for (int i = 1; i <= board.getNoOfHoles(); ++i) {
            message.append(board.getSeeds(Side.SOUTH, i));
            message.append(",");
        }
        message.append(board.getSeedsInStore(Side.SOUTH));
        message.append(";YOU\n");
        return message.toString();
    }

    public static String createEndMsg() {
        return "END\n";
    }

    public static MsgType getMessageType(final String msg) throws InvalidMessageException {
        if (msg.startsWith("MOVE;")) {
            return MsgType.MOVE;
        }
        if (msg.equals("SWAP\n")) {
            return MsgType.SWAP;
        }
        throw new InvalidMessageException("Could not determine message type.");
    }

    public static int interpretMoveMsg(final String msg) throws InvalidMessageException {
        if (msg.charAt(msg.length() - 1) != '\n') {
            throw new InvalidMessageException("Message not terminated with 0x0A character.");
        }
        final String move = msg.substring(5, msg.length() - 1);
        try {
            return Integer.parseInt(move);
        } catch (NumberFormatException e) {
            throw new InvalidMessageException("Illegal value for move parameter: " + e.getMessage());
        }
    }
}
