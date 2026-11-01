package netsil.ui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;

/** Sötét témájú JTable a forgalmi listához, színkódolt IRANY oszloppal. */
public class TrafficTable extends JTable {

    public TrafficTable(TrafficTableModel model) {
        super(model);
        setBackground(LcarsTheme.PANEL_BG);
        setForeground(LcarsTheme.TEXT_LIGHT);
        setGridColor(LcarsTheme.GRID_LINE);
        setSelectionBackground(LcarsTheme.PURPLE.darker().darker());
        setSelectionForeground(LcarsTheme.TEXT_LIGHT);
        setRowHeight(24);
        setFont(LcarsTheme.mono(12f));
        setFillsViewportHeight(true);
        setShowGrid(true);
        setIntercellSpacing(new java.awt.Dimension(1, 1));

        JTableHeader header = getTableHeader();
        header.setBackground(LcarsTheme.PANEL_BG_ALT);
        header.setForeground(LcarsTheme.ORANGE);
        header.setFont(LcarsTheme.label(12f));
        header.setReorderingAllowed(false);

        getColumnModel().getColumn(2).setCellRenderer(new DirectionRenderer());

        getColumnModel().getColumn(0).setPreferredWidth(70);
        getColumnModel().getColumn(1).setPreferredWidth(55);
        getColumnModel().getColumn(2).setPreferredWidth(70);
        getColumnModel().getColumn(3).setPreferredWidth(170);
        getColumnModel().getColumn(4).setPreferredWidth(170);
        getColumnModel().getColumn(5).setPreferredWidth(100);
        getColumnModel().getColumn(6).setPreferredWidth(60);
        getColumnModel().getColumn(7).setPreferredWidth(150);

        DefaultTableCellRenderer plain = new DefaultTableCellRenderer();
        plain.setBackground(LcarsTheme.PANEL_BG);
        plain.setForeground(LcarsTheme.TEXT_LIGHT);
        setDefaultRenderer(Object.class, plain);
        setDefaultRenderer(Integer.class, plain);
    }

    private static class DirectionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text = String.valueOf(value);
            Color color;
            switch (text) {
                case "BEJOVO": color = LcarsTheme.CYAN; break;
                case "KIMENO": color = LcarsTheme.ORANGE; break;
                case "FIGYEL": color = LcarsTheme.PURPLE; break;
                case "UDP": color = LcarsTheme.PINK; break;
                default: color = LcarsTheme.TEXT_DIM;
            }
            c.setForeground(color);
            if (!isSelected) {
                c.setBackground(LcarsTheme.PANEL_BG);
            }
            return c;
        }
    }
}
