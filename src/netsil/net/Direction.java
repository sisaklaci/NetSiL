package netsil.net;

/**
 * A kapcsolat iránya. Mivel a program a rendszer kapcsolat-tábláját (netstat)
 * figyeli és nem nyers csomagokat fog el, az irányt heurisztikával állapítja meg:
 * ha a helyi port egy LISTENING porttal egyezik, a kapcsolatot BEJOVO-nek tekinti,
 * egyébként a mi gépünk által kezdeményezett KIMENO kapcsolatnak.
 */
public enum Direction {
    LISTEN("FIGYEL"),
    IN("BEJOVO"),
    OUT("KIMENO"),
    UDP("UDP"),
    UNKNOWN("ISMERETLEN");

    private final String label;

    Direction(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
