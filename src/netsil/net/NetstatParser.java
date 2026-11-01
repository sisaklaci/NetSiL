package netsil.net;

import java.util.ArrayList;
import java.util.List;

/** A Windows "netstat -ano" parancs kimenetének feldolgozása. */
public final class NetstatParser {

    private NetstatParser() {
    }

    public static List<ConnectionInfo> parse(String output) {
        List<ConnectionInfo> result = new ArrayList<>();
        if (output == null) {
            return result;
        }
        for (String rawLine : output.split("\\r?\\n")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] tok = line.split("\\s+");
            if (tok.length == 0) {
                continue;
            }
            String proto = tok[0].toUpperCase();
            try {
                if (proto.startsWith("TCP") && tok.length >= 5) {
                    int[] local = splitHostPort(tok[1]);
                    int[] remote = splitHostPort(tok[2]);
                    String state = tok[3];
                    int pid = Integer.parseInt(tok[4]);
                    result.add(new ConnectionInfo(proto, hostOf(tok[1]), local[0],
                            hostOf(tok[2]), remote[0], state, pid));
                } else if (proto.startsWith("UDP") && tok.length >= 4) {
                    int[] local = splitHostPort(tok[1]);
                    int pid = Integer.parseInt(tok[3]);
                    result.add(new ConnectionInfo(proto, hostOf(tok[1]), local[0],
                            "*", -1, "-", pid));
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
                // Fejléc- vagy formázási sorok átugrása (pl. "Proto", "Active Connections").
            }
        }
        return result;
    }

    /** "1.2.3.4:443" vagy "[::1]:443" vagy "0.0.0.0:*" -> {port} (host-ot külön adjuk vissza). */
    private static int[] splitHostPort(String token) {
        String portPart = token.substring(token.lastIndexOf(':') + 1);
        int port = "*".equals(portPart) ? -1 : Integer.parseInt(portPart);
        return new int[]{port};
    }

    private static String hostOf(String token) {
        return token.substring(0, token.lastIndexOf(':'));
    }
}
