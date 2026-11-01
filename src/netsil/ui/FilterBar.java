package netsil.ui;

import netsil.filter.FilterCriteria;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

/** Szűrősor: protokoll, irány, port-szűrő és szabadszavas keresés. */
public class FilterBar extends RoundRectPanel {

    private final JComboBox<String> protocolBox = new JComboBox<>(new String[]{"ALL", "TCP", "UDP"});
    private final JComboBox<String> directionBox = new JComboBox<>(
            new String[]{"ALL", "LISTEN", "IN", "OUT", "UDP"});
    private final JTextField portField = new JTextField(14);
    private final JTextField searchField = new JTextField(18);

    private Consumer<FilterCriteria> onChange;
    private final Timer debounce;

    public FilterBar() {
        super(14, LcarsTheme.PANEL_BG);
        setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));
        setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        debounce = new Timer(250, e -> fireChange());
        debounce.setRepeats(false);

        add(styledLabel("PROTOKOLL"));
        styleCombo(protocolBox);
        add(protocolBox);

        add(styledLabel("IRANY"));
        styleCombo(directionBox);
        add(directionBox);

        add(styledLabel("PORT (pl. 443, 8000-9000)"));
        styleField(portField);
        add(portField);

        add(styledLabel("KERESES"));
        styleField(searchField);
        add(searchField);

        protocolBox.addActionListener(this::debounced);
        directionBox.addActionListener(this::debounced);
        portField.getDocument().addDocumentListener(docListener());
        searchField.getDocument().addDocumentListener(docListener());
    }

    public void setOnChange(Consumer<FilterCriteria> onChange) {
        this.onChange = onChange;
    }

    /** Minden szűrőt alapértelmezettre állít (teljes, szűretlen lista). */
    public void reset() {
        protocolBox.setSelectedIndex(0);
        directionBox.setSelectedIndex(0);
        portField.setText("");
        searchField.setText("");
        fireChange();
    }

    private void debounced(ActionEvent e) {
        debounce.restart();
    }

    private DocumentListener docListener() {
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { debounce.restart(); }
            @Override public void removeUpdate(DocumentEvent e) { debounce.restart(); }
            @Override public void changedUpdate(DocumentEvent e) { debounce.restart(); }
        };
    }

    private void fireChange() {
        FilterCriteria fc = new FilterCriteria();
        fc.protocol = (String) protocolBox.getSelectedItem();
        fc.direction = (String) directionBox.getSelectedItem();
        fc.portText = portField.getText();
        fc.searchText = searchField.getText();
        if (onChange != null) {
            onChange.accept(fc);
        }
    }

    private static JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(LcarsTheme.TEXT_DIM);
        l.setFont(LcarsTheme.label(11f));
        return l;
    }

    private static void styleCombo(JComboBox<String> box) {
        box.setBackground(LcarsTheme.PANEL_BG_ALT);
        box.setForeground(LcarsTheme.TEXT_LIGHT);
        box.setFont(LcarsTheme.mono(12f));
        box.setFocusable(false);
    }

    private static void styleField(JTextField field) {
        field.setBackground(LcarsTheme.PANEL_BG_ALT);
        field.setForeground(LcarsTheme.TEXT_LIGHT);
        field.setCaretColor(LcarsTheme.CYAN);
        field.setFont(LcarsTheme.mono(12f));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LcarsTheme.GRID_LINE, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }
}
