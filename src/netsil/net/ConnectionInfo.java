package netsil.net;

import java.util.Objects;

/** Egy hálózati kapcsolat (vagy figyelő foglalat) pillanatnyi állapota. */
public class ConnectionInfo {

    public final String protocol;    // TCP, TCPv6, UDP, UDPv6
    public final String localAddr;
    public final int localPort;
    public final String remoteAddr;
    public final int remotePort;     // -1 ha nincs (pl. UDP "*")
    public final String state;       // ESTABLISHED, LISTENING, ... vagy "-" UDP-nél
    public final int pid;

    public String processName = "—";
    public Direction direction = Direction.UNKNOWN;
    public long firstSeen = System.currentTimeMillis();
    public long lastSeen = System.currentTimeMillis();

    public ConnectionInfo(String protocol, String localAddr, int localPort,
                           String remoteAddr, int remotePort, String state, int pid) {
        this.protocol = protocol;
        this.localAddr = localAddr;
        this.localPort = localPort;
        this.remoteAddr = remoteAddr;
        this.remotePort = remotePort;
        this.state = state;
        this.pid = pid;
    }

    public boolean isTcp() {
        return protocol.startsWith("TCP");
    }

    public String key() {
        return protocol + "|" + localAddr + ":" + localPort + "|" + remoteAddr + ":" + remotePort + "|" + pid;
    }

    public String localEndpoint() {
        return localAddr + ":" + localPort;
    }

    public String remoteEndpoint() {
        return remotePort < 0 ? (remoteAddr + ":*") : (remoteAddr + ":" + remotePort);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectionInfo)) return false;
        return key().equals(((ConnectionInfo) o).key());
    }

    @Override
    public int hashCode() {
        return Objects.hash(key());
    }
}
