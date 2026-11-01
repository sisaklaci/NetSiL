package netsil.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

/**
 * Port-szűrő kifejezés értelmezése. Támogatott formátum, vesszővel elválasztva:
 * egyszerű port ("443"), vagy tartomány ("8000-9000"). Üres szöveg = minden port.
 */
public final class PortFilter {

    private PortFilter() {
    }

    public static IntPredicate parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            return p -> true;
        }
        List<IntPredicate> predicates = new ArrayList<>();
        for (String token : text.split(",")) {
            String t = token.trim();
            if (t.isEmpty()) {
                continue;
            }
            try {
                if (t.contains("-")) {
                    String[] parts = t.split("-", 2);
                    int lo = Integer.parseInt(parts[0].trim());
                    int hi = Integer.parseInt(parts[1].trim());
                    int min = Math.min(lo, hi);
                    int max = Math.max(lo, hi);
                    predicates.add(p -> p >= min && p <= max);
                } else {
                    int exact = Integer.parseInt(t);
                    predicates.add(p -> p == exact);
                }
            } catch (NumberFormatException ignored) {
                // érvénytelen token kihagyása - megengedő viselkedés gépelés közben
            }
        }
        if (predicates.isEmpty()) {
            return p -> true;
        }
        return p -> predicates.stream().anyMatch(pred -> pred.test(p));
    }
}
