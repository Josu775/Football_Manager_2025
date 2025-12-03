package gui;

import javax.swing.*;
import java.awt.*;

public class AppearanceWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private String managerName;
    private String selectedAvatar = null;
    private java.util.function.BiConsumer<String, String> callback;

    public AppearanceWindow(JFrame parent, String managerName, java.util.function.BiConsumer<String, String> callback) {
        super("Elige tu apariencia");

        this.managerName = managerName;
        this.callback = callback;

        setSize(640, 340);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
        setVisible(true);
    }

    private void initUI() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(5,10,30));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(panel);

        JLabel title = new JLabel("Elige tu apariencia", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        // 🔥 FlowLayout evita que las imágenes se estiren
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        avatarPanel.setBackground(new Color(5,10,30));
        panel.add(avatarPanel, BorderLayout.CENTER);

        String[] avatars = {
                "manager3.png",
                "manager1.png",
                "manager1.png"
        };

        for (String av : avatars) {

            ImageIcon icon = new ImageIcon("resources/images/avatars/" + av);
            Image scaled = icon.getImage().getScaledInstance(130,130,Image.SCALE_SMOOTH);

            JLabel lbl = new JLabel(new ImageIcon(scaled));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);

            // 🔥 Borde pegado
            lbl.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

            lbl.setPreferredSize(new Dimension(130,130));
            lbl.setMaximumSize(new Dimension(130,130));
            lbl.setMinimumSize(new Dimension(130,130));

            lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            lbl.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedAvatar = av;
                    highlight(avatarPanel, lbl);
                }
            });

            avatarPanel.add(lbl);
        }

        // ⭐ BOTÓN NORMAL DE TODA LA VIDA (sin colores raros)
        JButton btn = new JButton("Confirmar");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btn.addActionListener(e -> {
            if (selectedAvatar == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un avatar.");
                return;
            }
            callback.accept(managerName, selectedAvatar);
            dispose();
        });

        JPanel south = new JPanel();
        south.setBackground(new Color(5,10,30));
        south.add(btn);
        panel.add(south, BorderLayout.SOUTH);
    }

    //  Borde azul al seleccionar
    private void highlight(JPanel panel, JLabel selected) {

        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel lbl) {
                lbl.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }
        }

        selected.setBorder(BorderFactory.createLineBorder(new Color(70,120,230), 2));
    }
}
