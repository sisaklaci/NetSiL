package netsil.ui;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Dimension;

/** Statikus, színes "pirula" felirat (pl. fejléc státuszsor). */
public class PillLabel extends JComponent {

    private String text;
    private Color fill;
    private Color textColor = new Color(0x08, 0x0A, 0x10);
    private int align = SwingConstants.CENTER;

    public PillLabel(String text, Color fill) {
        this.text = text;
        this.fill = fill;
        setFont(LcarsTheme.label(12f));
        setPreferredSize(new Dimension(120, 26));
    }

    public void setPillText(String text) {
        this.text = text;
        repaint();
    }

    public void setFill(Color fill) {
        this.fill = fill;
        repaint();
    }

    public void setTextColor(Color c) {
        this.textColor = c;
        repaint();
    }

    public void setAlign(int align) {
        this.align = align;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, h, h);
            g2.setColor(textColor);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            int tx = align == SwingConstants.LEFT ? 14 : Math.max(10, (w - tw) / 2);
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);
        } finally {
            g2.dispose();
        }
    }
}
