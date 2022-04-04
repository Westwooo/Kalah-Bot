// 
// Decompiled by Procyon v0.5.36
// 

package ManKalahVerbose;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TimeoutReader extends Thread {
    private final Player player;
    private boolean getNextMessage;
    private boolean terminate;
    private String message;
    private IOException ioException;
    private Thread requestor;

    public TimeoutReader(final Player player) {
        this.getNextMessage = false;
        this.terminate = false;
        this.message = null;
        this.ioException = null;
        this.requestor = null;
        this.player = player;
    }

    public String recvMsg(long milliseconds) throws IOException, TimeoutException {
        if (milliseconds < 0L) {
            milliseconds = 0L;
        }
        this.message = null;
        this.ioException = null;
        synchronized (this) {
            this.requestor = Thread.currentThread();
        }
        this.getNextMessage = true;
        synchronized (this) {
            this.notifyAll();
        }
        try {
            Thread.sleep(milliseconds);
            throw new TimeoutException("Receiving message from agent has timed out.");
        } catch (InterruptedException ex) {
            if (this.ioException != null) {
                throw this.ioException;
            }
            synchronized (this) {
                this.requestor = null;
            }
            return this.message;
        }
    }

    @Override
    public void run() {
        while (true) {
            if (this.getNextMessage) {
                this.getNextMessage = false;
                try {
                    this.message = this.player.recvMsg();
                } catch (IOException e) {
                    this.ioException = e;
                }
                synchronized (this) {
                    if (this.requestor == null) {
                        continue;
                    }
                    this.requestor.interrupt();
                }
            } else {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                    }
                }
                if (this.terminate) {
                    break;
                }
                continue;
            }
        }
    }

    public void finish() {
        this.terminate = true;
        synchronized (this) {
            this.notifyAll();
        }
    }
}
