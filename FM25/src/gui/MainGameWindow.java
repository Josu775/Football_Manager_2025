package gui;

import domain.MatchSimulator;
import io.SaveManager;
import domain.GameSession;
import domain.Equipo;
import domain.LeagueData;
import domain.Jugador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class MainGameWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private GameSession session;
    private Equipo equipo;
    private JLabel lblTeamName;
    private JLabel lblFormation;
    private JLabel lblRating;
    private JLabel lblBudget;
    private JLabel lblAvgRating;

    public MainGameWindow(Window parent, GameSession session) {
        super("Partida - " + session.getJugadorEquipo().getNombre());
        this.session = session;
        this.equipo = session.getJugadorEquipo();
        setSize(1100, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel leftMenu = new JPanel();
        leftMenu.setLayout(new BoxLayout(leftMenu, BoxLayout.Y_AXIS));
        leftMenu.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        leftMenu.setPreferredSize(new Dimension(220, 0));

        lblTeamName = new JLabel(equipo.getNombre(), SwingConstants.CENTER);
        lblTeamName.setFont(new Font("Arial", Font.BOLD, 16));
        lblTeamName.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftMenu.add(lblTeamName);
        leftMenu.add(Box.createVerticalStrut(10));

        lblFormation = new JLabel("Formación: " + equipo.getFormacion());
        lblFormation.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftMenu.add(lblFormation);
        leftMenu.add(Box.createVerticalStrut(5));

        lblRating = new JLabel("Valoración equipo: " + String.format("%.1f / 5", equipo.getValoracion()));
        lblRating.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftMenu.add(lblRating);
        leftMenu.add(Box.createVerticalStrut(5));

        lblAvgRating = new JLabel("Media once: " + calcularMediaOnce(equipo.getOnceTitular()) + " / 99");
        lblAvgRating.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftMenu.add(lblAvgRating);
        leftMenu.add(Box.createVerticalStrut(5));

        lblBudget = new JLabel("Presupuesto: " + LeagueData.formatMoney(equipo.getBudget()));
        lblBudget.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftMenu.add(lblBudget);
        leftMenu.add(Box.createVerticalStrut(15));

        JButton btnClasificacion      = new JButton("Clasificación");
        JButton btnMercado            = new JButton("Mercado");
        JButton btnPlantilla          = new JButton("Plantilla / Tácticas");
        JButton btnCalendario         = new JButton("Calendario");
        JButton btnSimularPartido     = new JButton("Simular siguiente partido");
        JButton btnSimularTemporada   = new JButton("Simular temporada");
        JButton btnGuardar            = new JButton("Guardar partida");

        Dimension btnSize = new Dimension(200, 36);
        for (JButton b : new JButton[]{btnClasificacion, btnMercado, btnPlantilla,
                btnCalendario, btnSimularPartido, btnSimularTemporada, btnGuardar}) {
            b.setMaximumSize(btnSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftMenu.add(b);
            leftMenu.add(Box.createVerticalStrut(8));
        }

        JPanel center = new JPanel(new BorderLayout(10,10));
        center.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JTextArea txtOverview = new JTextArea();
        txtOverview.setEditable(false);
        updateOverviewText(txtOverview);
        center.add(new JScrollPane(txtOverview), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAtras = new JButton("Atrás");
        bottom.add(btnAtras);

        // PANEL MANAGER (arriba a la derecha)      
        JPanel managerPanel = new JPanel();
        managerPanel.setLayout(new BoxLayout(managerPanel, BoxLayout.Y_AXIS));
        managerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel lblName = new JLabel(session.getManagerName(), SwingConstants.CENTER);
        lblName.setFont(new Font("Arial", Font.BOLD, 18));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon av = new ImageIcon("resources/images/avatars/" + session.getManagerAvatar());
        Image scaledAv = av.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel lblAvatar = new JLabel(new ImageIcon(scaledAv), SwingConstants.CENTER);
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        managerPanel.add(lblAvatar);
        managerPanel.add(Box.createVerticalStrut(5));
        managerPanel.add(lblName);

        add(managerPanel, BorderLayout.EAST);
        add(leftMenu, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // === BOTONES ===

        // Clasificación ( lee TeamStats de cada equipo)
        btnClasificacion.addActionListener(e -> {
            List<Equipo> liga = session.getLiga();
            new ClassificationWindow(this, equipo, liga);
        });

        // Mercado
        btnMercado.addActionListener(e -> {
            MarketWindow mw = new MarketWindow(this, equipo);
            mw.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    lblBudget.setText("Presupuesto: " + LeagueData.formatMoney(equipo.getBudget()));
                    lblAvgRating.setText("Media once: " + calcularMediaOnce(equipo.getOnceTitular()) + " / 99");
                }
            });
        });

        // Plantilla / Tácticas
        btnPlantilla.addActionListener(e -> new SquadTacticsWindow(this, equipo));

        // Calendario ( pasa también la sesión para guardar / reutilizar calendario)
        btnCalendario.addActionListener(e ->
                new CalendarWindow(this, equipo, session.getLiga(), session)
        );

        // Guardar partida
        btnGuardar.addActionListener(e -> {
            SaveManager.guardarPartida(session);
            JOptionPane.showMessageDialog(this, "Partida guardada.");
        });

        // Volver al menú principal
        btnAtras.addActionListener(e -> {
            dispose();
            new WelcomeWindow().setVisible(true);
        });

        // === SIMULAR TEMPORADA COMPLETA ( HILO) ===
        btnSimularTemporada.addActionListener(e -> {
            btnSimularTemporada.setEnabled(false);

            Thread t = new Thread(() -> {
                MatchSimulator.simularTemporada(session.getLiga());

                SwingUtilities.invokeLater(() -> {
                    btnSimularTemporada.setEnabled(true);
                    session.reiniciarJornada();  // volvemos a 1 por si queremos luego simular jornada a jornada
                    JOptionPane.showMessageDialog(this,
                            "La temporada completa ha sido simulada.\n" +
                            "Abre la Clasificación para ver los resultados.");
                });
            }, "SimulacionLigaThread");

            t.start();
        });

        // ===  SIMULAR SIGUIENTE PARTIDO (HILO) ===
        btnSimularPartido.addActionListener(e -> {

            List<Object[]> calendario = session.getCalendario();
            if (calendario == null || calendario.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Primero abre el Calendario para generar el calendario de partidos.");
                return;
            }

            int jornada = session.getJornadaActual();
            if (jornada < 1) jornada = 1;

            if (jornada > calendario.size()) {
                JOptionPane.showMessageDialog(this,
                        "La temporada ya ha terminado. No hay más partidos por simular.");
                return;
            }

            Object[] datos = calendario.get(jornada - 1);
            String nombreRival = (String) datos[1];
            boolean local = (Boolean) datos[5];

            //  Buscar rival en la lista de equipos de la liga
            Equipo rival = null;
            for (Equipo eq : session.getLiga()) {
                if (eq.getNombre().equals(nombreRival)) {
                    rival = eq;
                    break;
                }
            }

            if (rival == null) {
                JOptionPane.showMessageDialog(this,
                        "No se ha encontrado el rival " + nombreRival + " en la liga.");
                return;
            }

            //  Variables finales para usarlas dentro del hilo
            final Equipo rivalFinal          = rival;
            final int jornadaFinal          = jornada;
            final String nombreRivalFinal   = nombreRival;
            final boolean localFinal        = local;

            btnSimularPartido.setEnabled(false);

            Thread t = new Thread(() -> {
                MatchSimulator.simularPartidoDirecto(equipo, rivalFinal, localFinal);

                SwingUtilities.invokeLater(() -> {
                    btnSimularPartido.setEnabled(true);
                    JOptionPane.showMessageDialog(this,
                            "Jornada " + jornadaFinal + " simulada.\n" +
                            "Rival: " + nombreRivalFinal + "\n" +
                            "Consulta la Clasificación para ver los cambios.");
                    session.avanzarJornada();
                });
            }, "SimularPartidoThread");

            t.start();
        });

        // ESC para volver
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "volver");
        am.put("volver", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAtras.doClick();
            }
        });
    }

    private void updateOverviewText(JTextArea area) {
        StringBuilder sb = new StringBuilder();
        sb.append("Manager: ").append(session.getManagerName()).append("\n\n");
        sb.append("Equipo: ").append(equipo.getNombre()).append("\n");
        sb.append("Ciudad: ").append(equipo.getCiudad()).append("\n");
        sb.append("Estadio: ").append(equipo.getEstadio()).append("\n");
        sb.append("Formación: ").append(equipo.getFormacion()).append("\n");
        sb.append("Valoración global: ").append(String.format("%.1f / 5", equipo.getValoracion())).append("\n");
        sb.append("Media del once: ").append(calcularMediaOnce(equipo.getOnceTitular())).append(" / 99\n");
        sb.append("Presupuesto: ").append(LeagueData.formatMoney(equipo.getBudget())).append("\n\n");
        sb.append("Usa el menú para ver clasificación, mercado, plantilla/tácticas y calendario.");
        area.setText(sb.toString());
    }

    private int calcularMediaOnce(List<Jugador> once) {
        if (once == null || once.isEmpty()) return 0;
        int sum = 0;
        for (Jugador j : once) sum += j.getValoracion();
        return Math.round((float) sum / once.size());
    }
}
