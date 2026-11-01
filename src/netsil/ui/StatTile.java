package netsil.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;

/** Kis statisztika-doboz, felirattal és nagy értékkel (pl. "AKTIV KAPCSOLATOK / 42"). */
public class StatTile extends RoundRectPanel {

    private final JLabel valueLabel;

    public StatTile(String caption, Color captionColor) {
        super(14, LcarsTheme.PANEL_BG);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setForeground(captionColor);
        captionLabel.setFont(LcarsTheme.label(11f));

        valueLabel = new JLabel("--");
        valueLabel.setForeground(LcarsTheme.TEXT_LIGHT);
        valueLabel.setFont(LcarsTheme.title(26f));
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        add(captionLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
