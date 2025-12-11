package gui;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import domain.GameSession;
import io.SaveManager;

public class LoadSlotsWindow extends JFrame {

    public LoadSlotsWindow(JFrame parent) {
        super("Cargar partida");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(3, 1, 10, 10));

        for (int i = 1; i <= 3; i++) {
            int slot = i;
            GameSession s = SaveManager.cargarPartida(slot);

            String texto = (s == null)
                    ? "Slot " + slot + " — VACÍO"
                    : "Slot " + slot + " — " + s.getManagerName() +
                      " (" + s.getJugadorEquipo().getNombre() + ")";

            JButton b = new JButton(texto);

            b.addActionListener(e -> {
                GameSession loaded = SaveManager.cargarPartida(slot);

                if (loaded == null) {
                    JOptionPane.showMessageDialog(this, "Este slot está vacío.");
                    return;
                }

                // Ocultar menú principal
                parent.setVisible(false);

                // Cerrar ventana selector
                dispose();

                // Abrir partida
                new MainGameWindow(parent, loaded);
            });

            add(b);
        }

        setVisible(true);
    }
}
