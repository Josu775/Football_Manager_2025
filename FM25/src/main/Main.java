package main;

import javax.swing.SwingUtilities;
import gui.WelcomeWindow;
import db.DataManager;

public class Main {
    public static void main(String[] args) {

        // Inicializamos el sistema de datos (conecta y crea tablas)
        DataManager.init();

        SwingUtilities.invokeLater(() -> new WelcomeWindow());
    }
}
