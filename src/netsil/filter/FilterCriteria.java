package netsil.filter;

import netsil.net.ConnectionInfo;
import netsil.net.Direction;

import java.util.Locale;
import java.util.function.IntPredicate;

/** Az aktuális szűrőbeállítások + illesztési logika egy kapcsolatra. */
public class FilterCriteria {

    public String protocol = "ALL";   // ALL, TCP, UDP
    public String direction = "ALL";  // ALL, LISTEN, IN, OUT, UDP
    public String portText = "";
    public String searchText = "";

    public boolean matches(ConnectionInfo c) {
        if (!"ALL".equals(protocol)) {
            String base = c.protocol.replace("v6", "");
            if (!base.equalsIgnoreCase(protocol)) {
                return false;
            }
        }
        if (!"ALL".equals(direction)) {
            Direction want = Direction.valueOf(direction);
            if (c.direction != want) {
                return false;
            }
        }
        IntPredicate portPredicate = PortFilter.parse(portText);
        boolean portOk = portPredicate.test(c.localPort)
                || (c.remotePort >= 0 && portPredicate.test(c.remotePort));
        if (!portOk) {
            return false;
        }
        if (searchText != null && !searchText.trim().isEmpty()) {
            String needle = searchText.trim().toLowerCase(Locale.ROOT);
            String haystack = (c.localEndpoint() + " " + c.remoteEndpoint() + " "
                    + c.processName + " " + c.pid + " " + c.state).toLowerCase(Locale.ROOT);
            if (!haystack.contains(needle)) {
                return false;
            }
        }
        return true;
    }
}
