package netsil.net;

import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Időzítve lekérdezi a rendszer kapcsolat-tábláját (netstat -ano) és az
 * interfész bájtszámlálókat (netstat -e), majd a feldolgozott adatot az UI
 * szálon továbbítja a feliratkozott figyelőnek.
 *
 * Megjegyzés: ez a rendszer kapcsolat-állapotát figyeli, nem nyers hálózati
 * csomagokat fog el (ahhoz rendszergazdai jogosultságú csomagrögzítő
 * illesztőprogram, pl. Npcap kellene) - ez a megoldás admin jog nélkül fut.
 */
public class NetworkMonitorService {

    public static final long POLL_INTERVAL_MS = 2000;

    public interface Listener {
        void onUpdate(List<ConnectionInfo> connections, List<String> newLogLines, InterfaceStats stats);

        void onError(String message);
    }

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "netsil-monitor");
        t.setDaemon(true);
        return t;
    });

    private final ProcessNameResolver processNameResolver = new ProcessNameResolver();
    private Map<String, ConnectionInfo> known = new HashMap<>();
    private InterfaceStats lastStats = InterfaceStats.EMPTY;
    private long lastPollTime = 0;

    private volatile Listener listener;
    private ScheduledFuture<?> future;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public synchronized void start() {
        if (future != null && !future.isDone()) {
            return;
        }
        future = executor.scheduleWithFixedDelay(this::pollSafely, 0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    public synchronized void stop() {
        if (future != null) {
            future.cancel(false);
            future = null;
        }
    }

    public boolean isRunning() {
        return future != null && !future.isDone();
    }

    public void shutdown() {
        stop();
        executor.shutdownNow();
    }

    private void pollSafely() {
        try {
            poll();
        } catch (Exception e) {
            fireError("Lekerdezesi hiba: " + e.getMessage());
        }
    }

    private void poll() throws Exception {
        String netstatOut = runCommand(5, "netstat", "-ano");
        List<ConnectionInfo> fresh = NetstatParser.parse(netstatOut);

        Set<String> listeningPorts = new HashSet<>();
        for (ConnectionInfo c : fresh) {
            if (c.isTcp() && "LISTENING".equalsIgnoreCase(c.state)) {
                listeningPorts.add(c.protocol + ":" + c.localPort);
            }
        }
        for (ConnectionInfo c : fresh) {
            if (!c.isTcp()) {
                c.direction = Direction.UDP;
            } else if ("LISTENING".equalsIgnoreCase(c.state)) {
                c.direction = Direction.LISTEN;
            } else if (listeningPorts.contains(c.protocol + ":" + c.localPort)) {
                c.direction = Direction.IN;
            } else {
                c.direction = Direction.OUT;
            }
        }

        Map<Integer, String> names = processNameResolver.resolveAll();
        for (ConnectionInfo c : fresh) {
            c.processName = names.getOrDefault(c.pid, c.pid == 0 ? "SYSTEM" : "—");
        }

        Map<String, ConnectionInfo> freshMap = new HashMap<>();
        for (ConnectionInfo c : fresh) {
            freshMap.put(c.key(), c);
        }

        List<String> logLines = new ArrayList<>();
        for (String key : freshMap.keySet()) {
            if (!known.containsKey(key)) {
                ConnectionInfo c = freshMap.get(key);
                logLines.add("UJ " + c.direction.label() + " :: " + c.protocol + " "
                        + c.localEndpoint() + " -> " + c.remoteEndpoint() + " [" + c.processName + "]");
            } else {
                ConnectionInfo old = known.get(key);
                freshMap.get(key).firstSeen = old.firstSeen;
            }
        }
        for (String key : known.keySet()) {
            if (!freshMap.containsKey(key)) {
                ConnectionInfo c = known.get(key);
                logLines.add("LEZARVA :: " + c.protocol + " " + c.localEndpoint() + " -> " + c.remoteEndpoint());
            }
        }
        known = freshMap;

        InterfaceStats stats = parseInterfaceStats(runCommand(5, "netstat", "-e"));
        long now = System.currentTimeMillis();
        if (lastPollTime > 0 && lastStats != InterfaceStats.EMPTY) {
            double seconds = Math.max(0.5, (now - lastPollTime) / 1000.0);
            stats.receivedPerSec = (long) Math.max(0, (stats.bytesReceived - lastStats.bytesReceived) / seconds);
            stats.sentPerSec = (long) Math.max(0, (stats.bytesSent - lastStats.bytesSent) / seconds);
        }
        lastStats = stats;
        lastPollTime = now;

        List<ConnectionInfo> snapshot = new ArrayList<>(freshMap.values());
        Listener l = listener;
        if (l != null) {
            SwingUtilities.invokeLater(() -> l.onUpdate(snapshot, logLines, stats));
        }
    }

    private static InterfaceStats parseInterfaceStats(String output) {
        if (output != null) {
            for (String line : output.split("\\r?\\n")) {
                String[] tok = line.trim().split("\\s+");
                if (tok.length == 3) {
                    try {
                        long received = Long.parseLong(tok[1]);
                        long sent = Long.parseLong(tok[2]);
                        return new InterfaceStats(received, sent);
                    } catch (NumberFormatException ignored) {
                        // nem a "Bytes" sor - folytatjuk
                    }
                }
            }
        }
        return InterfaceStats.EMPTY;
    }

    private static String runCommand(int timeoutSeconds, String... command) throws Exception {
        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
        }
        return sb.toString();
    }

    private void fireError(String message) {
        Listener l = listener;
        if (l != null) {
            SwingUtilities.invokeLater(() -> l.onError(message));
        }
    }
}
