package netsil;

import netsil.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // alapértelmezett L&F-fel folytatjuk
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
