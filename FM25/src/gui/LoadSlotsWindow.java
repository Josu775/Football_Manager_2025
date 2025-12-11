package gui;

import java.awt.*;
import javax.swing.*;
import domain.GameSession;
import io.SaveManager;

public class LoadSlotsWindow extends JFrame {

    public LoadSlotsWindow(JFrame parent) {

        super("Cargar partida");
        setSize(420, 360);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ===== Fondo general =====
        JPanel fondo = new JPanel(new BorderLayout(10,10));
        fondo.setBackground(new Color(8,16,40));
        fondo.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        add(fondo);

        // ===== Título =====
        JLabel title = new JLabel("Selecciona un Slot para Cargar", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        fondo.add(title, BorderLayout.NORTH);

        // ===== Panel de slots =====
        JPanel slotsPanel = new JPanel(new GridLayout(3,1,10,10));
        slotsPanel.setBackground(new Color(8,16,40));

        for (int i=1; i<=3; i++) {

            int slot = i;
            GameSession s = SaveManager.cargarPartida(slot);

            String texto = (s == null)
                    ? "Slot " + slot + " — VACÍO"
                    : "Slot " + slot + " — " + s.getManagerName() +
                      " (" + s.getJugadorEquipo().getNombre() + ")";

            JButton btn = new JButton(texto);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setBackground(new Color(30,45,90));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(90,110,200)));

            // Hover
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(new Color(50,70,140));
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(new Color(30,45,90));
                }
            });

            btn.addActionListener(e -> {
                GameSession loaded = SaveManager.cargarPartida(slot);
                if (loaded == null) {
                    JOptionPane.showMessageDialog(this, "Este slot está vacío.");
                    return;
                }

                parent.setVisible(false);
                dispose();
                new MainGameWindow(parent, loaded);
            });

            slotsPanel.add(btn);
        }

        fondo.add(slotsPanel, BorderLayout.CENTER);

        // ===== Botón cerrar =====
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
