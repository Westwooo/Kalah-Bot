// 
// Decompiled by Procyon v0.5.36
// 

package ManKalahVerbose;

public class Timer {
    private long milliseconds;
    private long startTime;

    public Timer() {
        this.milliseconds = 0L;
        this.startTime = 0L;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void stop() {
        final long stopTime = System.currentTimeMillis();
        this.milliseconds += stopTime - this.startTime;
    }

    public void reset() {
        this.milliseconds = 0L;
    }

    public long time() {
        return this.milliseconds;
    }
}
