package gui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import domain.GameSession;
import io.SaveManager;

public class NewGameSlotWindow extends JFrame {

    public NewGameSlotWindow(JFrame parent, String nombre, String avatar) {

        super("Seleccionar slot");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(3, 1, 10, 10));

        for (int i = 1; i <= 3; i++) {

            int slot = i;
            GameSession s = SaveManager.cargarPartida(slot);

            String texto = (s == null)
                    ? "Slot " + slot + " — VACÍO"
                    : "Slot " + slot + " — Sobrescribir partida existente";

            JButton b = new JButton(texto);

            b.addActionListener(e -> {

                if (s != null) {
                    int conf = JOptionPane.showConfirmDialog(
                            this,
                            "Este slot ya tiene una partida.\n¿Deseas sobrescribirlo?",
                            "Confirmar",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (conf != JOptionPane.YES_OPTION) return;
                }

                // Ahora vas a elegir el equipo
                new TeamSelectionWindow(this, nombre, avatar, slot);
                dispose();
            });

            add(b);
        }

        setVisible(true);
    }
}
