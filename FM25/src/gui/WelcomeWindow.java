package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    public WelcomeWindow() {
        setTitle("Football Manager");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        // ========== PANEL DE FONDO ==========
        BackgroundPanel p = new BackgroundPanel("resources/images/logo.jpg");
        p.setLayout(new BorderLayout(10,10));
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // ========== TÍTULO ==========
        JLabel title = new JLabel("Football Manager", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        p.add(title, BorderLayout.NORTH);

        // ========== SUBTÍTULO ==========
        JLabel subtitle = new JLabel("Entrena un equipo. Alcanza la gloria", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 20));
        subtitle.setForeground(Color.WHITE);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.add(subtitle);
        p.add(center, BorderLayout.CENTER);

        // ========== BOTONES ==========
        JPanel botones = new JPanel();
        botones.setOpaque(false);

        JButton btnNueva = new JButton("Nueva partida");
        JButton btnSalir = new JButton("Salir");

        botones.add(btnNueva);
        botones.add(btnSalir);

        p.add(botones, BorderLayout.SOUTH);

        add(p);

        // ========== TECLAS RÁPIDAS ==========
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "nueva");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "salir");

        am.put("nueva", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                btnNueva.doClick();
            }
        });
        am.put("salir", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSalir.doClick();
            }
        });

        // === ACCIONES ===
        btnNueva.addActionListener(e -> {
            // Flujo nuevo: nombre -> avatar -> selector de equipo
            new CreateManagerWindow(this, (nombre, avatar) -> {
                new TeamSelectionWindow(this, nombre, avatar);
                setVisible(false);
            });
        });

        btnSalir.addActionListener(e -> System.exit(0));
    }

    // ========== PANEL QUE PINTA EL FONDO ==========
    class BackgroundPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private Image background;

        public BackgroundPanel(String imagePath) {
            background = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Escala la imagen al tamaño de la ventana
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
