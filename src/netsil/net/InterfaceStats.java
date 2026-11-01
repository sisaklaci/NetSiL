package netsil.net;

/** A "netstat -e" kimenetéből kinyert interfész-szintű bájtszámlálók. */
public class InterfaceStats {

    public final long bytesReceived;
    public final long bytesSent;
    public long receivedPerSec;
    public long sentPerSec;

    public InterfaceStats(long bytesReceived, long bytesSent) {
        this.bytesReceived = bytesReceived;
        this.bytesSent = bytesSent;
    }

    public static final InterfaceStats EMPTY = new InterfaceStats(0, 0);
}
