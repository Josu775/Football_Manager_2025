package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeWindow extends JFrame {

    public WelcomeWindow() {
        setTitle("Football Manager");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel title = new JLabel("Football Manager", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        p.add(title, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("Entrena un equipo. Alcanza la gloria", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel center = new JPanel(new GridBagLayout());
        center.add(subtitle);
        p.add(center, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton btnNueva = new JButton("Nueva partida");
        JButton btnSalir = new JButton("Salir");
        botones.add(btnNueva);
        botones.add(btnSalir);
        p.add(botones, BorderLayout.SOUTH);

        add(p);

        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "nueva");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "salir");

        am.put("nueva", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnNueva.doClick();
            }
        });
        am.put("salir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSalir.doClick();
            }
        });

        btnNueva.addActionListener(e -> {
            new TeamSelectionWindow(this);
            setVisible(false);
        });

        btnSalir.addActionListener(e -> System.exit(0));
    }
}
