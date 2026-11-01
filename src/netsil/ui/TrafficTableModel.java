package netsil.ui;

import netsil.filter.FilterCriteria;
import netsil.net.ConnectionInfo;
import netsil.net.Direction;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/** Táblamodell a (szűrt) kapcsolat-listához. */
public class TrafficTableModel extends AbstractTableModel {

    public static final String[] COLUMNS = {
            "IDO", "PROTO", "IRANY", "HELYI CIM", "TAVOLI CIM", "ALLAPOT", "PID", "FOLYAMAT"
    };

    private List<ConnectionInfo> all = new ArrayList<>();
    private List<ConnectionInfo> view = new ArrayList<>();
    private FilterCriteria filter = new FilterCriteria();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public void setData(List<ConnectionInfo> data) {
        this.all = data;
        reapplyFilter();
    }

    public void setFilter(FilterCriteria filter) {
        this.filter = filter;
        reapplyFilter();
    }

    private void reapplyFilter() {
        List<ConnectionInfo> filtered = new ArrayList<>();
        for (ConnectionInfo c : all) {
            if (filter.matches(c)) {
                filtered.add(c);
            }
        }
        filtered.sort(Comparator
                .comparingInt((ConnectionInfo c) -> directionOrder(c.direction))
                .thenComparing(c -> c.protocol)
                .thenComparingInt(c -> c.localPort));
        this.view = filtered;
        fireTableDataChanged();
    }

    private static int directionOrder(Direction d) {
        switch (d) {
            case LISTEN: return 0;
            case IN: return 1;
            case OUT: return 2;
            case UDP: return 3;
            default: return 4;
        }
    }

    public ConnectionInfo getConnectionAt(int row) {
        return view.get(row);
    }

    public int getFilteredCount() {
        return view.size();
    }

    public int getTotalCount() {
        return all.size();
    }

    @Override
    public int getRowCount() {
        return view.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ConnectionInfo c = view.get(rowIndex);
        switch (columnIndex) {
            case 0: return timeFormat.format(new Date(c.firstSeen));
            case 1: return c.protocol;
            case 2: return c.direction.label();
            case 3: return c.localEndpoint();
            case 4: return c.remoteEndpoint();
            case 5: return c.state;
            case 6: return c.pid;
            case 7: return c.processName;
            default: return "";
        }
    }
}
