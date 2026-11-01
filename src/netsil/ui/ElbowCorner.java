package netsil.ui;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/** Az LCARS felületekre jellemző, lekerekített "könyök" sarokdísz. */
public class ElbowCorner extends JComponent {

    private final Color color;
    private final int barH;
    private final int barW;
    private final int outerR;
    private final int innerR;

    public ElbowCorner(Color color, int barH, int barW, int outerR, int innerR) {
        this.color = color;
        this.barH = barH;
        this.barW = barW;
        this.outerR = outerR;
        this.innerR = innerR;
        setPreferredSize(new Dimension(barW + 40, barH + 70));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Area shape = new Area(new RoundRectangle2D.Float(0, 0, w, h, outerR * 2, outerR * 2));
            Area cut = new Area(new java.awt.geom.Rectangle2D.Float(barW, barH, w - barW, h - barH));
            shape.subtract(cut);

            Area fillet = new Area(new Ellipse2D.Float(barW, barH, innerR * 2, innerR * 2));
            shape.subtract(fillet);

            g2.setColor(color);
            g2.fill(shape);
        } finally {
            g2.dispose();
        }
    }
}
