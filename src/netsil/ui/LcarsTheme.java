package netsil.ui;

import java.awt.Color;
import java.awt.Font;

/** Közös LCARS-stílusú színpaletta és betűtípusok. */
public final class LcarsTheme {

    private LcarsTheme() {
    }

    public static final Color BG = new Color(0x07, 0x08, 0x0D);
    public static final Color PANEL_BG = new Color(0x0E, 0x11, 0x19);
    public static final Color PANEL_BG_ALT = new Color(0x13, 0x17, 0x21);

    public static final Color ORANGE = new Color(0xFF, 0x9F, 0x5A);
    public static final Color ORANGE_DARK = new Color(0xE0, 0x82, 0x44);
    public static final Color PURPLE = new Color(0xB9, 0xA4, 0xFF);
    public static final Color PINK = new Color(0xFF, 0x9F, 0xC6);
    public static final Color CYAN = new Color(0x7F, 0xE0, 0xDE);
    public static final Color CREAM = new Color(0xFF, 0xDD, 0xA8);
    public static final Color RED = new Color(0xFF, 0x7A, 0x7A);

    public static final Color TEXT_LIGHT = new Color(0xDC, 0xEC, 0xF4);
    public static final Color TEXT_DIM = new Color(0x7A, 0x8A, 0x9A);
    public static final Color GRID_LINE = new Color(0x1E, 0x24, 0x30);

    public static Font title(float size) {
        return new Font("Arial", Font.BOLD, Math.round(size));
    }

    public static Font label(float size) {
        return new Font("Arial", Font.BOLD, Math.round(size));
    }

    public static Font mono(float size) {
        return new Font("Consolas", Font.PLAIN, Math.round(size));
    }

    public static Font monoBold(float size) {
        return new Font("Consolas", Font.BOLD, Math.round(size));
    }
}
