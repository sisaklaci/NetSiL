package netsil.ui;

import netsil.net.InterfaceStats;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.StringJoiner;

/** Interfész-áttekintő nézet: összesített be/kimenő forgalom és hálózati adapterek. */
public class InterfacePanel extends RoundRectPanel {

    private final StatTile bytesInTile = new StatTile("OSSZES FOGADOTT BAJT", LcarsTheme.CYAN);
    private final StatTile bytesOutTile = new StatTile("OSSZES KULDOTT BAJT", LcarsTheme.ORANGE);
    private final StatTile rateInTile = new StatTile("FOGADAS SEBESSEG", LcarsTheme.CYAN);
    private final StatTile rateOutTile = new StatTile("KULDES SEBESSEG", LcarsTheme.ORANGE);

    private final DefaultTableModel ifaceModel = new DefaultTableModel(
            new Object[]{"NEV", "LEIRAS", "MTU", "AKTIV", "LOOPBACK", "IP CIMEK"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public InterfacePanel() {
        super(14, LcarsTheme.PANEL_BG);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        javax.swing.JPanel tiles = new javax.swing.JPanel(new GridLayout(1, 4, 14, 0));
        tiles.setOpaque(false);
        tiles.add(bytesInTile);
        tiles.add(bytesOutTile);
        tiles.add(rateInTile);
        tiles.add(rateOutTile);
        tiles.setPreferredSize(new java.awt.Dimension(10, 90));
        add(tiles, BorderLayout.NORTH);

        JTable table = new JTable(ifaceModel);
        table.setBackground(LcarsTheme.PANEL_BG);
        table.setForeground(LcarsTheme.TEXT_LIGHT);
        table.setGridColor(LcarsTheme.GRID_LINE);
        table.setRowHeight(24);
        table.setFont(LcarsTheme.mono(12f));
        table.getTableHeader().setBackground(LcarsTheme.PANEL_BG_ALT);
        table.getTableHeader().setForeground(LcarsTheme.ORANGE);
        table.getTableHeader().setFont(LcarsTheme.label(12f));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(LcarsTheme.PANEL_BG);
        scroll.setBorder(BorderFactory.createLineBorder(LcarsTheme.GRID_LINE));
        add(scroll, BorderLayout.CENTER);

        loadInterfaces();
    }

    public void updateStats(InterfaceStats stats) {
        bytesInTile.setValue(formatBytes(stats.bytesReceived));
        bytesOutTile.setValue(formatBytes(stats.bytesSent));
        rateInTile.setValue(formatBytes(stats.receivedPerSec) + "/s");
        rateOutTile.setValue(formatBytes(stats.sentPerSec) + "/s");
    }

    private void loadInterfaces() {
        ifaceModel.setRowCount(0);
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            if (ifaces == null) {
                return;
            }
            for (NetworkInterface ni : Collections.list(ifaces)) {
                StringJoiner ips = new StringJoiner(", ");
                for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                    ips.add(addr.getHostAddress());
                }
                int mtu;
                boolean up;
                boolean loopback;
                try {
                    mtu = ni.getMTU();
                    up = ni.isUp();
                    loopback = ni.isLoopback();
                } catch (SocketException e) {
                    mtu = -1;
                    up = false;
                    loopback = false;
                }
                ifaceModel.addRow(new Object[]{
                        ni.getName(), ni.getDisplayName(), mtu,
                        up ? "IGEN" : "NEM", loopback ? "IGEN" : "NEM",
                        ips.length() == 0 ? "-" : ips.toString()
                });
            }
        } catch (SocketException ignored) {
            // nincs elérhető interfész-lista
        }
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024L * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
