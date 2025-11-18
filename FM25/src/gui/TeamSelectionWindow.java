package gui;

import domain.Equipo;
import domain.LeagueData;
import domain.GameSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamSelectionWindow extends JFrame {

    private JList<Equipo> list;
    private JLabel lblEscudo;

    // ===== MAPA (AJUSTADO A TUS PNG) =====
    private static final Map<String, String> ESCUDOS = new HashMap<>();
    static {
        ESCUDOS.put("Real Madrid", "realmadrid.png");
        ESCUDOS.put("FC Barcelona", "barcelona.png");
        ESCUDOS.put("Atlético de Madrid", "atlmadrid.png");
        ESCUDOS.put("Sevilla FC", "sevilla.png");
        ESCUDOS.put("Real Sociedad", "realsociedad.png");
        ESCUDOS.put("Villarreal CF", "villarreal.png");
        ESCUDOS.put("Real Betis", "betis.png");
        ESCUDOS.put("Athletic Club", "athletic.png");
        ESCUDOS.put("Valencia CF", "valencia.png");
        ESCUDOS.put("RCD Mallorca", "mallorca.png");
        ESCUDOS.put("CA Osasuna", "osasuna.png");
        ESCUDOS.put("RC Celta", "celta.png");
        ESCUDOS.put("Getafe CF", "getafe.png");
        ESCUDOS.put("Rayo Vallecano", "rayovallecano.png");
        ESCUDOS.put("Deportivo Alavés", "alaves.png");
        ESCUDOS.put("Elche CF", "elche.png");
        ESCUDOS.put("RCD Espanyol", "espanyol.png");
        ESCUDOS.put("Girona FC", "girona.png");

        // NUEVOS EQUIPOS (tienes escudo)
        ESCUDOS.put("Real Oviedo", "realoviedo.png");
        ESCUDOS.put("Levante UD", "levante.png");
    }

    public TeamSelectionWindow(JFrame parent) {
        setTitle("Elegir equipo - Nueva partida");
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        List<Equipo> equipos = LeagueData.getLaLiga20();

        DefaultListModel<Equipo> model = new DefaultListModel<>();
        for (Equipo e : equipos) model.addElement(e);

        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Panel de detalle
        JPanel detail = new JPanel(new BorderLayout(10,10));
        detail.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Escudo
        lblEscudo = new JLabel();
        lblEscudo.setHorizontalAlignment(SwingConstants.CENTER);
        detail.add(lblEscudo, BorderLayout.NORTH);

        // Texto descriptivo
        JTextArea detalleText = new JTextArea();
        detalleText.setEditable(false);
        detalleText.setLineWrap(true);
        detalleText.setWrapStyleWord(true);
        detail.add(new JScrollPane(detalleText), BorderLayout.CENTER);

        // Split
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(list), detail);
        split.setDividerLocation(250);

        // Botones
        JPanel south = new JPanel();
        JButton btnElegir = new JButton("Elegir equipo");
        JButton btnAtras = new JButton("Atrás");
        south.add(btnElegir);
        south.add(btnAtras);

        add(split, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        // Selección
        list.addListSelectionListener(e -> {
            Equipo sel = list.getSelectedValue();
            if (sel != null) {

                // Escudo
                String fileName = ESCUDOS.get(sel.getNombre());
                if (fileName != null) {
                    String path = "resources/images/escudos/" + fileName;
                    ImageIcon icon = new ImageIcon(path);
                    Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    lblEscudo.setIcon(new ImageIcon(img));
                } else {
                    lblEscudo.setIcon(null);
                }

                // Info
                StringBuilder sb = new StringBuilder();
                sb.append("Equipo: ").append(sel.getNombre()).append("\n");
                sb.append("Ciudad: ").append(sel.getCiudad()).append("\n");
                sb.append("Estadio: ").append(sel.getEstadio()).append("\n");
                sb.append("Formación: ").append(sel.getFormacion()).append("\n");
                sb.append("Valoración: ").append(String.format("%.1f / 5.0", sel.getValoracion())).append("\n");
                sb.append("Presupuesto: ").append(LeagueData.formatMoney(sel.getBudget())).append("\n\n");
                sb.append("Once titular:\n");
                int i = 1;
                for (var j : sel.getOnceTitular()) {
                    sb.append(i++).append(". ").append(j.getNombre()).append(" - ").append(j.getPosicion())
                            .append(" (").append(j.getEdad()).append(" años) ").append(j.getValoracion()).append(" / 99").append("\n");
                }
                detalleText.setText(sb.toString());

            } else {
                detalleText.setText("");
                lblEscudo.setIcon(null);
            }
        });

        // Botones
        btnAtras.addActionListener(e -> {
            dispose();
            new WelcomeWindow().setVisible(true);
        });

        btnElegir.addActionListener(e -> {
            Equipo seleccionado = list.getSelectedValue();
            if (seleccionado == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un equipo.");
                return;
            }
            GameSession session = new GameSession(seleccionado);
            new MainGameWindow(this, session);
            dispose();
        });

        // Teclas
        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> btnElegir.doClick();
                    case KeyEvent.VK_ESCAPE -> btnAtras.doClick();
                }
            }
        });
    }
}
