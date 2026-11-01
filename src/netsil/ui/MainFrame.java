package netsil.ui;

import netsil.filter.FilterCriteria;
import netsil.net.ConnectionInfo;
import netsil.net.InterfaceStats;
import netsil.net.NetworkMonitorService;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/** A NetSiL alkalmazás fő ablaka: LCARS-stílusú hálózatiforgalom-monitor. */
public class MainFrame extends JFrame {

    private final NetworkMonitorService monitor = new NetworkMonitorService();
    private final TrafficTableModel tableModel = new TrafficTableModel();
    private final TrafficTable table = new TrafficTable(tableModel);
    private final FilterBar filterBar = new FilterBar();
    private final Sidebar sidebar = new Sidebar();
    private final StatusFooter footer = new StatusFooter();
    private final InterfacePanel interfacePanel = new InterfacePanel();
    private final DonutChart donutChart = new DonutChart();

    private final StatTile activeTile = new StatTile("AKTIV KAPCSOLATOK", LcarsTheme.PURPLE);
    private final StatTile inRateTile = new StatTile("BEJOVO SEBESSEG", LcarsTheme.CYAN);
    private final StatTile outRateTile = new StatTile("KIMENO SEBESSEG", LcarsTheme.ORANGE);

    private final JTextArea logArea = new JTextArea();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final JLabel uptimeValue = new JLabel("00:00");
    private final long startTime = System.currentTimeMillis();

    public MainFrame() {
        super("NetSiL — Halozati Forgalom Monitor");
        getContentPane().setBackground(LcarsTheme.BG);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(14, 14));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);

        add(buildHeader(), BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(buildCenter(), BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        wireActions();

        monitor.setListener(new NetworkMonitorService.Listener() {
            @Override
            public void onUpdate(List<ConnectionInfo> connections, List<String> newLogLines, InterfaceStats stats) {
                tableModel.setData(connections);
                updateStats(connections, stats);
                appendLogLines(newLogLines);
                interfacePanel.updateStats(stats);
                footer.setRunning(monitor.isRunning(), connections.size());
            }

            @Override
            public void onError(String message) {
                appendLogLines(java.util.Collections.singletonList("HIBA :: " + message));
            }
        });

        Timer uptimeTimer = new Timer(1000, e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            uptimeValue.setText(String.format("%02d:%02d:%02d", elapsed / 3600, (elapsed / 60) % 60, elapsed % 60));
        });
        uptimeTimer.start();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                monitor.shutdown();
            }
        });

        monitor.start();
        footer.setRunning(true, 0);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        ElbowCorner elbow = new ElbowCorner(LcarsTheme.ORANGE, 34, 70, 30, 20);
        top.add(elbow, BorderLayout.WEST);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new javax.swing.BoxLayout(titleBlock, javax.swing.BoxLayout.Y_AXIS));
        JLabel title = new JLabel("NetSiL");
        title.setForeground(new Color(0xCF, 0xEF, 0xFF));
        title.setFont(LcarsTheme.title(34f));
        titleBlock.add(title);
        top.add(titleBlock, BorderLayout.CENTER);

        JLabel subtitle = new JLabel("HALOZATI FORGALOM MONITOR  /  01.00", SwingConstants.RIGHT);
        subtitle.setForeground(LcarsTheme.CYAN);
        subtitle.setFont(LcarsTheme.label(13f));
        top.add(subtitle, BorderLayout.EAST);

        header.add(top, BorderLayout.NORTH);

        JPanel statusRow = new JPanel(new GridLayout(1, 4, 8, 0));
        statusRow.setOpaque(false);
        statusRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        statusRow.add(fixedPill("ELO FIGYELES", LcarsTheme.PURPLE));
        statusRow.add(fixedPill("TCP / UDP", LcarsTheme.PINK));
        statusRow.add(fixedPill("KAPCSOLAT-TABLA FIGYELES", LcarsTheme.ORANGE));
        PillLabel onlinePill = new PillLabel("ONLINE", LcarsTheme.CYAN);
        onlinePill.setPreferredSize(new Dimension(160, 26));
        statusRow.add(onlinePill);
        header.add(statusRow, BorderLayout.SOUTH);

        return header;
    }

    private PillLabel fixedPill(String text, Color color) {
        PillLabel p = new PillLabel(text, color);
        p.setPreferredSize(new Dimension(160, 26));
        return p;
    }

    private JPanel buildCenter() {
        JPanel connectionsCard = new JPanel(new BorderLayout(0, 12));
        connectionsCard.setOpaque(false);

        JPanel statsRow = new JPanel(new BorderLayout(14, 0));
        statsRow.setOpaque(false);
        JPanel tiles = new JPanel(new GridLayout(1, 3, 14, 0));
        tiles.setOpaque(false);
        tiles.add(activeTile);
        tiles.add(inRateTile);
        tiles.add(outRateTile);
        statsRow.add(tiles, BorderLayout.CENTER);

        RoundRectPanel donutWrap = new RoundRectPanel(14, LcarsTheme.PANEL_BG);
        donutWrap.setPreferredSize(new Dimension(150, 90));
        donutWrap.setLayout(new BorderLayout());
        donutWrap.add(donutChart, BorderLayout.CENTER);
        donutChart.setCenterText("NET", "MIX");
        statsRow.add(donutWrap, BorderLayout.EAST);
        statsRow.setPreferredSize(new Dimension(10, 90));

        connectionsCard.add(statsRow, BorderLayout.NORTH);

        JPanel middle = new JPanel(new BorderLayout(0, 10));
        middle.setOpaque(false);
        middle.add(filterBar, BorderLayout.NORTH);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.getViewport().setBackground(LcarsTheme.PANEL_BG);
        tableScroll.setBorder(BorderFactory.createLineBorder(LcarsTheme.GRID_LINE));
        middle.add(tableScroll, BorderLayout.CENTER);

        connectionsCard.add(middle, BorderLayout.CENTER);
        connectionsCard.add(buildLogPanel(), BorderLayout.SOUTH);

        cardPanel.setOpaque(false);
        cardPanel.add(connectionsCard, "CONNECTIONS");
        cardPanel.add(interfacePanel, "INTERFACES");
        return cardPanel;
    }

    private RoundRectPanel buildLogPanel() {
        RoundRectPanel panel = new RoundRectPanel(14, LcarsTheme.PANEL_BG);
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(10, 150));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        JLabel caption = new JLabel("RENDSZERNAPLO / TELEMETRIA");
        caption.setForeground(LcarsTheme.PURPLE);
        caption.setFont(LcarsTheme.label(11f));
        panel.add(caption, BorderLayout.NORTH);

        logArea.setEditable(false);
        logArea.setBackground(LcarsTheme.PANEL_BG);
        logArea.setForeground(LcarsTheme.TEXT_LIGHT);
        logArea.setFont(LcarsTheme.mono(11.5f));
        logArea.setText("> NetSiL 1.0 // halozat-monitor rendszer online\n"
                + "> kapcsolat-tabla figyeles elinditva (netstat alapu, admin jog nem szukseges)\n");

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(LcarsTheme.PANEL_BG);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void wireActions() {
        filterBar.setOnChange(fc -> tableModel.setFilter(fc));
        tableModel.setFilter(new FilterCriteria());

        sidebar.connectionsBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "CONNECTIONS");
            sidebar.selectConnections();
        });
        sidebar.interfacesBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "INTERFACES");
            sidebar.selectInterfaces();
        });
        sidebar.clearLogBtn.addActionListener(e -> logArea.setText(""));
        sidebar.clearListBtn.addActionListener(e -> filterBar.reset());
        sidebar.exportBtn.addActionListener(e -> exportCsv());

        footer.toggleBtn.addActionListener(e -> {
            if (monitor.isRunning()) {
                monitor.stop();
            } else {
                monitor.start();
            }
            footer.setRunning(monitor.isRunning(), tableModel.getTotalCount());
        });
    }

    private void updateStats(List<ConnectionInfo> connections, InterfaceStats stats) {
        activeTile.setValue(String.valueOf(connections.size()));
        inRateTile.setValue(formatBytes(stats.receivedPerSec) + "/s");
        outRateTile.setValue(formatBytes(stats.sentPerSec) + "/s");

        int tcp = 0, udp = 0, listen = 0;
        for (ConnectionInfo c : connections) {
            switch (c.direction) {
                case LISTEN: listen++; break;
                case UDP: udp++; break;
                default: tcp++; break;
            }
        }
        donutChart.setSegment("TCP", tcp, LcarsTheme.ORANGE);
        donutChart.setSegment("UDP", udp, LcarsTheme.PINK);
        donutChart.setSegment("LISTEN", listen, LcarsTheme.PURPLE);
    }

    private void appendLogLines(List<String> lines) {
        if (lines.isEmpty()) {
            return;
        }
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append("> [").append(fmt.format(new Date())).append("] ").append(line).append('\n');
        }
        logArea.append(sb.toString());

        int maxLines = 400;
        String[] all = logArea.getText().split("\n");
        if (all.length > maxLines) {
            StringBuilder trimmed = new StringBuilder();
            for (int i = all.length - maxLines; i < all.length; i++) {
                trimmed.append(all[i]).append('\n');
            }
            logArea.setText(trimmed.toString());
        }
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("netsil_export.csv"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        java.io.File file = chooser.getSelectedFile();
        try (Writer fw = new FileWriter(file, StandardCharsets.UTF_8, false);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("ido;protokoll;irany;helyi_cim;helyi_port;tavoli_cim;tavoli_port;allapot;pid;folyamat");
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < tableModel.getFilteredCount(); i++) {
                ConnectionInfo c = tableModel.getConnectionAt(i);
                pw.printf("%s;%s;%s;%s;%d;%s;%d;%s;%d;%s%n",
                        fmt.format(new Date(c.firstSeen)), c.protocol, c.direction.label(),
                        c.localAddr, c.localPort, c.remoteAddr, c.remotePort, c.state, c.pid, c.processName);
            }
            appendLogLines(java.util.Collections.singletonList("EXPORT KESZ :: " + file.getAbsolutePath()));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Nem sikerult menteni: " + ex.getMessage(),
                    "Export hiba", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024L * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
