package gui;

import domain.MatchSimulator;
import io.SaveManager;
import domain.GameSession;
import domain.Equipo;
import domain.LeagueData;
import domain.Jugador;
import domain.LeagueCalendar;

import javax.swing.*;

import db.ClasificacionDAO;
import db.DataManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

        lblRating = new JLabel("Media equipo: " + (int)equipo.getValoracion() + " / 100");
        lblRating.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftMenu.add(lblRating);
        leftMenu.add(Box.createVerticalStrut(5));

        lblAvgRating = new JLabel("Media once: " + calcularMediaOnce(equipo) + " / 99");
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
        JButton btnCalendarioMensual  = new JButton("Calendario mensual");
        JButton btnSimularPartido     = new JButton("Simular siguiente partido");
        JButton btnSimularTemporada   = new JButton("Simular temporada");
        JButton btnGuardar            = new JButton("Guardar partida");

        Dimension btnSize = new Dimension(200, 36);
        for (JButton b : new JButton[]{btnClasificacion, btnMercado, btnPlantilla,
                btnCalendario, btnCalendarioMensual, btnSimularPartido, btnSimularTemporada, btnGuardar}) 
        {
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

        // === BOTÓN CLASIFICACIÓN ===
        btnClasificacion.addActionListener(e -> {
            List<Equipo> liga = DataManager.cargarLigaDeBD();
            ClasificacionDAO cdao = new ClasificacionDAO(DataManager.getGestor());
            cdao.cargarClasificacion(liga);
            new ClassificationWindow(this, equipo, liga);
        });

        // === BOTÓN MERCADO ===
        btnMercado.addActionListener(e -> {
            MarketWindow mw = new MarketWindow(this, equipo);
            mw.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    lblBudget.setText("Presupuesto: " + LeagueData.formatMoney(equipo.getBudget()));
                    lblAvgRating.setText("Media once: " + calcularMediaOnce(equipo) + " / 99");
                }
            });
        });

        btnPlantilla.addActionListener(e -> new SquadTacticsWindow(this, equipo));

        btnCalendario.addActionListener(e ->
                new CalendarWindow(this, equipo, session.getLiga(), session)
        );
        
        btnCalendarioMensual.addActionListener(e -> 
        new MonthlyCalendarWindow(this, equipo, session.getLiga(), session)
        );


        btnGuardar.addActionListener(e -> {
            SaveManager.guardarPartida(session);
            JOptionPane.showMessageDialog(this, "Partida guardada.");
        });

        btnAtras.addActionListener(e -> {
            dispose();
            new WelcomeWindow().setVisible(true);
        });

        // === SIMULAR TEMPORADA COMPLETA ===
        btnSimularTemporada.addActionListener(e -> {
            btnSimularTemporada.setEnabled(false);

            Thread t = new Thread(() -> {
                MatchSimulator.simularTemporada(session.getLiga());

                SwingUtilities.invokeLater(() -> {
                    btnSimularTemporada.setEnabled(true);
                    session.reiniciarJornada();
                    JOptionPane.showMessageDialog(this,
                            "La temporada completa ha sido simulada.\n" +
                                    "Abre la Clasificación para ver los resultados.");
                });
            }, "SimulacionLigaThread");

            t.start();
        });

        // === SIMULAR SIGUIENTE PARTIDO ===
        btnSimularPartido.addActionListener(e -> {

            List<List<LeagueCalendar.Match>> calendario = session.getCalendario();

            if (calendario == null || calendario.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Primero abre el Calendario para generar los partidos.");
                return;
            }

            int jornada = session.getJornadaActual();
            if (jornada < 1) jornada = 1;
            if (jornada > calendario.size()) {
                JOptionPane.showMessageDialog(this,
                        "La temporada ya ha terminado.");
                return;
            }

            // Partidos de esa jornada
            List<LeagueCalendar.Match> jornadaPartidos = calendario.get(jornada - 1);

            // Buscar tu partido
            LeagueCalendar.Match miPartido = null;

            for (LeagueCalendar.Match m : jornadaPartidos) {
                if (m.local().equals(equipo.getNombre()) ||
                    m.visitante().equals(equipo.getNombre())) {
                    miPartido = m;
                    break;
                }
            }

            if (miPartido == null) {
                JOptionPane.showMessageDialog(this, "Tu equipo no juega esta jornada.");
                session.avanzarJornada();
                return;
            }

            btnSimularPartido.setEnabled(false);

            final int jornadaFinal = jornada;

            Thread t = new Thread(() -> {

                // 1️º Simular toda la jornada
                MatchSimulator.simularJornadaCompleta(jornadaPartidos, session.getLiga());

                SwingUtilities.invokeLater(() -> {
                    btnSimularPartido.setEnabled(true);
                    JOptionPane.showMessageDialog(this,
                            "Jornada " + jornadaFinal + " simulada.\n" +
                            "Consulta la Clasificación para ver los resultados.");

                    session.avanzarJornada();
                });
            });

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
        int mediaEquipo = calcularMediaEquipo(equipo);
        lblRating.setText("Media equipo: " + mediaEquipo + " / 100");
        lblAvgRating.setText("Media once: " + calcularMediaOnce(equipo) + " / 99");
        sb.append("Media del once: ").append(calcularMediaOnce(equipo)).append(" / 99\n");
        sb.append("Presupuesto: ").append(LeagueData.formatMoney(equipo.getBudget())).append("\n\n");
        sb.append("Usa el menú para ver clasificación, mercado, plantilla/tácticas y calendario.");
        area.setText(sb.toString());
    }

    public static int calcularMediaOnce(Equipo e) {
        List<Jugador> once = e.getOnceTitularReal();
        if (once.isEmpty()) return 0;

        double sum = 0;
        for (Jugador j : once) sum += j.getValoracion();

        return (int) Math.round(sum / once.size());
    }
    
    public static int calcularMediaEquipo(Equipo e) {
        if (e.getPlantilla().isEmpty()) return 0;
        
        double sum = 0;
        for (Jugador j : e.getPlantilla()) {
            sum += j.getValoracion();   // AHORA directa, porque ya es 60-90
        }

        return (int) Math.round(sum / e.getPlantilla().size());
    }

}

