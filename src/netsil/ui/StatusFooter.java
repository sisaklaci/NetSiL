package netsil.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/** Alsó státuszsor: élő figyelés állapota és indítás/leállítás gomb. */
public class StatusFooter extends JPanel {

    public final PillLabel statusLabel = new PillLabel("FIGYELES INDUL...", LcarsTheme.PURPLE);
    public final PillLabel countLabel = new PillLabel("0 KAPCSOLAT", LcarsTheme.CYAN);
    public final PillButton toggleBtn = new PillButton("LEALLITAS", LcarsTheme.PINK);

    public StatusFooter() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        statusLabel.setPreferredSize(new java.awt.Dimension(220, 28));
        countLabel.setPreferredSize(new java.awt.Dimension(150, 28));
        left.add(statusLabel);
        left.add(countLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        toggleBtn.setPreferredSize(new java.awt.Dimension(150, 32));
        right.add(toggleBtn);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    public void setRunning(boolean running, int connectionCount) {
        statusLabel.setPillText(running ? "FIGYELES AKTIV" : "FIGYELES LEALLITVA");
        statusLabel.setFill(running ? LcarsTheme.CYAN : LcarsTheme.RED);
        countLabel.setPillText(connectionCount + " KAPCSOLAT");
        toggleBtn.setText(running ? "LEALLITAS" : "INDITAS");
    }
}
