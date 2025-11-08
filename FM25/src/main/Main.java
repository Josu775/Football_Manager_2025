package main;

import javax.swing.SwingUtilities;
import gui.WelcomeWindow;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeWindow());
    }
}
