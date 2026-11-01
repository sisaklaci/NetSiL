package netsil.ui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Teljesen lekerekített "pirula" gomb, az LCARS navigációs elemek stílusában. */
public class PillButton extends JButton {

    private final Color baseColor;
    private Color currentColor;
    private boolean active;

    public PillButton(String text, Color baseColor) {
        super(text);
        this.baseColor = baseColor;
        this.currentColor = baseColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(new Color(0x08, 0x0A, 0x10));
        setFont(LcarsTheme.label(13f));
        setHorizontalAlignment(LEFT);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 18, 8, 14));
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = brighten(baseColor);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = baseColor;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                currentColor = darken(baseColor);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentColor = baseColor;
                repaint();
            }
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    private static Color brighten(Color c) {
        return new Color(
                Math.min(255, c.getRed() + 30),
                Math.min(255, c.getGreen() + 30),
                Math.min(255, c.getBlue() + 30));
    }

    private static Color darken(Color c) {
        return new Color(
                Math.max(0, c.getRed() - 40),
                Math.max(0, c.getGreen() - 40),
                Math.max(0, c.getBlue() - 40));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = getHeight();
            int w = getWidth();
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, w, h, h, h);
            if (active) {
                g2.setColor(LcarsTheme.TEXT_LIGHT);
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, h, h);
            }
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}
