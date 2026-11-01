package netsil.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Dimension;

/** Bal oldali LCARS navigációs sáv, színes "pirula" gombokkal. */
public class Sidebar extends JPanel {

    public final PillButton connectionsBtn = new PillButton("KAPCSOLATOK", LcarsTheme.CREAM);
    public final PillButton interfacesBtn = new PillButton("INTERFESZEK", LcarsTheme.ORANGE);
    public final PillButton exportBtn = new PillButton("EXPORT CSV", LcarsTheme.PURPLE);
    public final PillButton clearLogBtn = new PillButton("NAPLO TORLES", LcarsTheme.PINK);
    public final PillButton clearListBtn = new PillButton("LISTA TORLES", LcarsTheme.CYAN);

    private final PillLabel legendTcp = new PillLabel("TCP", LcarsTheme.ORANGE);
    private final PillLabel legendUdp = new PillLabel("UDP", LcarsTheme.PINK);

    public Sidebar() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        for (PillButton b : new PillButton[]{connectionsBtn, interfacesBtn, exportBtn, clearLogBtn, clearListBtn}) {
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(220, 38));
            b.setPreferredSize(new Dimension(200, 38));
            add(b);
            add(Box.createVerticalStrut(10));
        }

        add(Box.createVerticalGlue());

        legendTcp.setAlignmentX(LEFT_ALIGNMENT);
        legendTcp.setMaximumSize(new Dimension(200, 26));
        legendTcp.setPreferredSize(new Dimension(200, 26));
        legendUdp.setAlignmentX(LEFT_ALIGNMENT);
        legendUdp.setMaximumSize(new Dimension(200, 26));
        legendUdp.setPreferredSize(new Dimension(200, 26));
        add(legendTcp);
        add(Box.createVerticalStrut(8));
        add(legendUdp);

        connectionsBtn.setActive(true);
    }

    public void selectConnections() {
        connectionsBtn.setActive(true);
        interfacesBtn.setActive(false);
    }

    public void selectInterfaces() {
        connectionsBtn.setActive(false);
        interfacesBtn.setActive(true);
    }
}
