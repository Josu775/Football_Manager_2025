package gui;

import java.awt.*;
import javax.swing.*;
import domain.GameSession;
import io.SaveManager;

public class LoadSlotsWindow extends JFrame {

    public LoadSlotsWindow(JFrame parent) {

        super("Cargar partida");
        setSize(460, 420);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel fondo = new JPanel(new BorderLayout(10,10));
        fondo.setBackground(new Color(8,16,40));
        fondo.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        add(fondo);

        JLabel title = new JLabel("Selecciona un Slot", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        fondo.add(title, BorderLayout.NORTH);

        JPanel slotsPanel = new JPanel(new GridLayout(3,1,12,12));
        slotsPanel.setBackground(new Color(8,16,40));

        for (int i = 1; i <= 3; i++) {

            int slot = i;
            GameSession s = SaveManager.cargarPartida(slot);

            String texto = (s == null)
                    ? "Slot " + slot + " — VACÍO"
                    : "Slot " + slot + " — " + s.getManagerName() +
                      " (" + s.getJugadorEquipo().getNombre() + ")";

            JPanel fila = new JPanel(new BorderLayout(8,8));
            fila.setBackground(new Color(8,16,40));

            JButton btnLoad = new JButton(texto);
            btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btnLoad.setBackground(new Color(30,45,90));
            btnLoad.setForeground(Color.WHITE);
            btnLoad.setFocusPainted(false);
            btnLoad.setBorder(BorderFactory.createLineBorder(new Color(90,110,200)));

            btnLoad.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    btnLoad.setBackground(new Color(50,70,140));
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    btnLoad.setBackground(new Color(30,45,90));
                }
            });

            btnLoad.addActionListener(e -> {
                GameSession loaded = SaveManager.cargarPartida(slot);
                if (loaded == null) {
                    JOptionPane.showMessageDialog(this, "Este slot está vacío.");
                    return;
                }
                parent.setVisible(false);
                dispose();
                new MainGameWindow(parent, loaded);
            });

            JButton btnDelete = new JButton("🗑");
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnDelete.setBackground(new Color(120,30,30));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setPreferredSize(new Dimension(50, 40));

            btnDelete.addActionListener(e -> {

                if (SaveManager.cargarPartida(slot) == null) {
                    JOptionPane.showMessageDialog(this, "El slot ya está vacío.");
                    return;
                }

                int res = JOptionPane.showConfirmDialog(
                        this,
                        "¿Seguro que quieres eliminar la partida del slot " + slot + "?\nEsta acción no se puede deshacer.",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (res == JOptionPane.YES_OPTION) {
                    SaveManager.borrarSlot(slot);
                    dispose();
                    new LoadSlotsWindow(parent);
                }
            });

            fila.add(btnLoad, BorderLayout.CENTER);
            fila.add(btnDelete, BorderLayout.EAST);

            slotsPanel.add(fila);
        }

        fondo.add(slotsPanel, BorderLayout.CENTER);

        JButton btnClose = new JButton("Cerrar");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setBackground(new Color(100,30,30));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        JPanel south = new JPanel();
        south.setBackground(new Color(8,16,40));
        south.add(btnClose);

        fondo.add(south, BorderLayout.SOUTH);

        setVisible(true);
    }
}
