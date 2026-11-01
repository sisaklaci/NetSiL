package netsil.ui;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.LinkedHashMap;
import java.util.Map;

/** Egyszerű, gyűrű alakú arány-diagram (protokoll-megoszlás), LCARS stílusban. */
public class DonutChart extends JComponent {

    private final Map<String, Integer> values = new LinkedHashMap<>();
    private final Map<String, Color> colors = new LinkedHashMap<>();
    private String centerTitle = "NET";
    private String centerSubtitle = "FIGYELES";

    public void setSegment(String key, int value, Color color) {
        values.put(key, value);
        colors.put(key, color);
        repaint();
    }

    public void setCenterText(String title, String subtitle) {
        this.centerTitle = title;
        this.centerSubtitle = subtitle;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            float stroke = Math.max(10f, size * 0.12f);

            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            g2.setColor(LcarsTheme.GRID_LINE);
            g2.draw(new Arc2D.Float(x, y, size, size, 0, 360, Arc2D.OPEN));

            int total = values.values().stream().mapToInt(Integer::intValue).sum();
            if (total > 0) {
                double startAngle = 90;
                for (Map.Entry<String, Integer> e : values.entrySet()) {
                    if (e.getValue() <= 0) {
                        continue;
                    }
                    double extent = 360.0 * e.getValue() / total;
                    g2.setColor(colors.getOrDefault(e.getKey(), LcarsTheme.CYAN));
                    g2.draw(new Arc2D.Double(x, y, size, size, startAngle, -extent, Arc2D.OPEN));
                    startAngle -= extent;
                }
            }

            g2.setColor(LcarsTheme.TEXT_LIGHT);
            g2.setFont(LcarsTheme.title(16f));
            FontMetrics fm = g2.getFontMetrics();
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            g2.drawString(centerTitle, cx - fm.stringWidth(centerTitle) / 2, cy - 2);

            g2.setColor(LcarsTheme.TEXT_DIM);
            g2.setFont(LcarsTheme.label(9f));
            FontMetrics fm2 = g2.getFontMetrics();
            g2.drawString(centerSubtitle, cx - fm2.stringWidth(centerSubtitle) / 2, cy + fm2.getHeight());
        } finally {
            g2.dispose();
        }
    }
}
