// 
// Decompiled by Procyon v0.5.36
// 

package ManKalahVerbose;

import java.io.*;

public class Player {
    private final String name;
    private final int playerNumber;
    private final Process agent;
    private final Writer agentInput;
    private final Reader agentOutput;
    private TimeoutReader readerThread;
    private Side side;
    private int moveCount;
    private long overallResponseTime;

    public Player(final int playerNumber, final String name, final Process agent, final Side initialSide) {
        this.readerThread = null;
        this.moveCount = 0;
        this.overallResponseTime = 0L;
        this.playerNumber = playerNumber;
        this.name = name;
        this.agent = agent;
        this.side = initialSide;
        this.agentOutput = new BufferedReader(new InputStreamReader(agent.getInputStream()));
        this.agentInput = new BufferedWriter(new OutputStreamWriter(agent.getOutputStream()));
    }

    public String getName() {
        return this.name;
    }

    public Side getSide() {
        return this.side;
    }

    public int getPlayerNumber() {
        return this.playerNumber;
    }

    public void changeSide() {
        this.side = this.side.opposite();
    }

    public int getMoveCount() {
        return this.moveCount;
    }

    public void incrementMoveCount() {
        ++this.moveCount;
    }

    public long getOverallResponseTime() {
        return this.overallResponseTime;
    }

    public void incrementOverallResponseTime(final long additionalResponseTime) {
        this.overallResponseTime += additionalResponseTime;
    }

    public void startReaderThread() {
        if (this.readerThread != null) {
            throw new IllegalStateException("Method called twice.");
        }
        (this.readerThread = new TimeoutReader(this)).start();
    }

    public TimeoutReader getReaderThread() {
        return this.readerThread;
    }

    public void sendMsg(final String msg) throws IOException {
        this.agentInput.write(msg);
        this.agentInput.flush();
    }

    public String recvMsg() throws IOException {
        final StringBuilder message = new StringBuilder();
        int newCharacter;
        do {
            newCharacter = this.agentOutput.read();
            if (newCharacter == -1) {
                throw new EOFException("Agent input ended unexpectedly.");
            }
            message.append((char) newCharacter);
        } while ((char) newCharacter != '\n');
        return message.toString();
    }
}
