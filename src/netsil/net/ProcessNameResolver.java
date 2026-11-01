package netsil.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** A futó folyamatok PID -> név leképezését adja a "tasklist" paranccsal. */
public final class ProcessNameResolver {

    public Map<Integer, String> resolveAll() {
        Map<Integer, String> map = new HashMap<>();
        Process process = null;
        try {
            process = new ProcessBuilder("tasklist", "/fo", "csv", "/nh")
                    .redirectErrorStream(true)
                    .start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = parseCsvLine(line);
                    if (fields.length >= 2) {
                        try {
                            int pid = Integer.parseInt(fields[1].trim());
                            map.put(pid, fields[0].trim());
                        } catch (NumberFormatException ignored) {
                            // fejléc vagy hibás sor
                        }
                    }
                }
            }
            process.waitFor(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // tasklist nem elérhető - üres map-et adunk vissza, a hívó kezeli
        } finally {
            if (process != null && process.isAlive()) {
                process.destroyForcibly();
            }
        }
        return map;
    }

    private static String[] parseCsvLine(String line) {
        String stripped = line.trim();
        if (stripped.startsWith("\"")) {
            stripped = stripped.substring(1);
        }
        if (stripped.endsWith("\"")) {
            stripped = stripped.substring(0, stripped.length() - 1);
        }
        return stripped.split("\",\"");
    }
}
