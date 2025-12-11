package gui;

import domain.MatchSimulator;
import io.SaveManager;
import domain.GameSession;
import domain.Equipo;
import domain.LeagueData;
import domain.Jugador;
import domain.LeagueCalendar;
import domain.TeamStats;

import javax.swing.*;

import db.ClasificacionDAO;
import db.DataManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.Comparator;
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

    // Tarjeta resumen
    private JTextArea txtOverview;

    // Tarjeta siguiente partido
    private JLabel lblNextMatchTeams;
    private JLabel lblNextMatchDate;
    private JLabel lblNextMatchInfo;

    // Tarjeta noticias
    private JTextArea txtNews;

    // Tarjeta insights (forma, jugadores clave, prob partido)
    private JLabel lblForm;
    private JLabel lblBestPlayer;
    private JLabel lblYoungPlayer;
    private JLabel lblWinProb;

    // Cache del siguiente partido
    private LeagueCalendar.Match nextMatch;
    private int nextMatchJornada;

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

        // ============================
        //  FONDO PRINCIPAL GRADIENTE
        // ============================
        JPanel root = new GradientPanel();
        root.setLayout(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        // ============================
        //  LATERAL IZQUIERDO (MENÚ)
        // ============================
        JPanel leftMenu = new JPanel();
        leftMenu.setLayout(new BoxLayout(leftMenu, BoxLayout.Y_AXIS));
        leftMenu.setPreferredSize(new Dimension(230, 0));
        leftMenu.setBackground(new Color(8, 12, 36));
        leftMenu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 55, 120)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        // CABECERA EQUIPO EN LATERAL
        lblTeamName = new JLabel(equipo.getNombre(), SwingConstants.CENTER);
        lblTeamName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTeamName.setForeground(Color.WHITE);
        lblTeamName.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftMenu.add(lblTeamName);
        leftMenu.add(Box.createVerticalStrut(10));

        lblFormation = new JLabel("Formación: " + equipo.getFormacion());
        lblFormation.setForeground(new Color(200, 210, 240));
        lblFormation.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblRating = new JLabel("Media equipo: " + calcularMediaEquipo(equipo) + " / 100");
        lblRating.setForeground(new Color(200, 210, 240));
        lblRating.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblAvgRating = new JLabel("Media once: " + calcularMediaOnce(equipo) + " / 99");
        lblAvgRating.setForeground(new Color(200, 210, 240));
        lblAvgRating.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblBudget = new JLabel("Presupuesto: " + LeagueData.formatMoney(equipo.getBudget()));
        lblBudget.setForeground(new Color(140, 230, 140));
        lblBudget.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftMenu.add(lblFormation);
        leftMenu.add(Box.createVerticalStrut(4));
        leftMenu.add(lblRating);
        leftMenu.add(Box.createVerticalStrut(4));
        leftMenu.add(lblAvgRating);
        leftMenu.add(Box.createVerticalStrut(4));
        leftMenu.add(lblBudget);

        leftMenu.add(Box.createVerticalStrut(18));
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 80, 150));
        leftMenu.add(sep);
        leftMenu.add(Box.createVerticalStrut(12));

        // BOTONES MENÚ
        JButton btnClasificacion      = createMenuButton("Clasificación");
        JButton btnMercado            = createMenuButton("Mercado de fichajes");
        JButton btnPlantilla          = createMenuButton("Plantilla / Tácticas");
        JButton btnCalendario         = createMenuButton("Calendario");
        JButton btnCalendarioMensual  = createMenuButton("Calendario mensual");
        JButton btnSimularPartido     = createMenuButton("Simular siguiente partido");
        JButton btnSimularTemporada   = createMenuButton("Simular temporada completa");
        JButton btnGuardar            = createMenuButton("Guardar partida");
        JButton btnAtras              = createMenuButton("Salir al menú principal");

        Dimension btnSize = new Dimension(200, 34);
        for (JButton b : new JButton[]{btnClasificacion, btnMercado, btnPlantilla,
                btnCalendario, btnCalendarioMensual, btnSimularPartido,
                btnSimularTemporada, btnGuardar, btnAtras}) {

            b.setMaximumSize(btnSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftMenu.add(b);
            leftMenu.add(Box.createVerticalStrut(8));
        }

        root.add(leftMenu, BorderLayout.WEST);

        // ============================
        // PANEL DERECHO (MANAGER CARD)
        // ============================
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(220, 0));

        RoundedPanel managerCard = new RoundedPanel(new Color(18, 24, 60, 230));
        managerCard.setLayout(new BoxLayout(managerCard, BoxLayout.Y_AXIS));
        managerCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblManagerTitle = new JLabel("Manager", SwingConstants.CENTER);
        lblManagerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblManagerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblManagerTitle.setForeground(new Color(190, 205, 255));

        // Avatar
        ImageIcon av = new ImageIcon("resources/images/avatars/" + session.getManagerAvatar());
        Image scaledAv = av.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        JLabel lblAvatar = new JLabel(new ImageIcon(scaledAv));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel(session.getManagerName(), SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(Color.WHITE);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSince = new JLabel("Desde: " + session.getInicio().toLocalDate(), SwingConstants.CENTER);
        lblSince.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSince.setForeground(new Color(200, 210, 240));
        lblSince.setAlignmentX(Component.CENTER_ALIGNMENT);

        managerCard.add(lblManagerTitle);
        managerCard.add(Box.createVerticalStrut(6));
        managerCard.add(lblAvatar);
        managerCard.add(Box.createVerticalStrut(6));
        managerCard.add(lblName);
        managerCard.add(Box.createVerticalStrut(4));
        managerCard.add(lblSince);

        rightPanel.add(managerCard);
        rightPanel.add(Box.createVerticalGlue());

        root.add(rightPanel, BorderLayout.EAST);

        // ============================
        //  PANEL CENTRAL (CARDS)
        // ============================
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        root.add(center, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;

        // ---- CARD 1: OVERVIEW DEL EQUIPO ----
        RoundedPanel overviewCard = new RoundedPanel(new Color(18, 30, 80, 230));
        overviewCard.setLayout(new BorderLayout(10, 10));
        overviewCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblOverviewTitle = new JLabel("Resumen del club");
        lblOverviewTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblOverviewTitle.setForeground(new Color(220, 230, 255));

        txtOverview = new JTextArea();
        txtOverview.setEditable(false);
        txtOverview.setOpaque(false);
        txtOverview.setForeground(new Color(220, 230, 255));
        txtOverview.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtOverview.setLineWrap(true);
        txtOverview.setWrapStyleWord(true);
        txtOverview.setBorder(null);

        updateOverviewText(txtOverview);

        overviewCard.add(lblOverviewTitle, BorderLayout.NORTH);
        overviewCard.add(new JScrollPane(txtOverview) {
            private static final long serialVersionUID = 1L;
            {
                setBorder(null);
                getViewport().setOpaque(false);
                setOpaque(false);
            }
        }, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.gridwidth = 2;
        center.add(overviewCard, gbc);

        // ---- CARD 2: SIGUIENTE PARTIDO ----
        RoundedPanel nextMatchCard = new RoundedPanel(new Color(26, 40, 100, 230));
        nextMatchCard.setLayout(new BoxLayout(nextMatchCard, BoxLayout.Y_AXIS));
        nextMatchCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblNextMatchTitle = new JLabel("Siguiente partido");
        lblNextMatchTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNextMatchTitle.setForeground(new Color(230, 240, 255));
        lblNextMatchTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNextMatchTeams = new JLabel("—", SwingConstants.CENTER);
        lblNextMatchTeams.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblNextMatchTeams.setForeground(Color.WHITE);
        lblNextMatchTeams.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNextMatchDate = new JLabel("", SwingConstants.CENTER);
        lblNextMatchDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNextMatchDate.setForeground(new Color(210, 220, 245));
        lblNextMatchDate.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNextMatchInfo = new JLabel("", SwingConstants.CENTER);
        lblNextMatchInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNextMatchInfo.setForeground(new Color(180, 205, 255));
        lblNextMatchInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        nextMatchCard.add(lblNextMatchTitle);
        nextMatchCard.add(Box.createVerticalStrut(8));
        nextMatchCard.add(lblNextMatchTeams);
        nextMatchCard.add(Box.createVerticalStrut(4));
        nextMatchCard.add(lblNextMatchDate);
        nextMatchCard.add(Box.createVerticalStrut(4));
        nextMatchCard.add(lblNextMatchInfo);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.55;
        gbc.weighty = 0.25;
        center.add(nextMatchCard, gbc);

        // ---- CARD 3: STATS RÁPIDAS ----
        RoundedPanel statsCard = new RoundedPanel(new Color(22, 26, 70, 230));
        statsCard.setLayout(new GridLayout(3, 2, 6, 6));
        statsCard.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        statsCard.add(createStatLabel("Media equipo:"));
        statsCard.add(createStatValueLabel(() -> lblRating.getText().replace("Media equipo: ", "")));

        statsCard.add(createStatLabel("Media once:"));
        statsCard.add(createStatValueLabel(() -> lblAvgRating.getText().replace("Media once: ", "")));

        statsCard.add(createStatLabel("Presupuesto:"));
        statsCard.add(createStatValueLabel(() -> LeagueData.formatMoney(equipo.getBudget())));

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.45;
        gbc.weighty = 0.25;
        center.add(statsCard, gbc);

        // ---- CARD 4: NOTICIAS DE MERCADO ----
        RoundedPanel newsCard = new RoundedPanel(new Color(20, 28, 72, 230));
        newsCard.setLayout(new BorderLayout(8, 8));
        newsCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblNewsTitle = new JLabel("Noticias del mercado");
        lblNewsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNewsTitle.setForeground(new Color(230, 240, 255));

        txtNews = new JTextArea();
        txtNews.setEditable(false);
        txtNews.setOpaque(false);
        txtNews.setLineWrap(true);
        txtNews.setWrapStyleWord(true);
        txtNews.setForeground(new Color(220, 230, 255));
        txtNews.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNews.setBorder(null);

        newsCard.add(lblNewsTitle, BorderLayout.NORTH);
        newsCard.add(new JScrollPane(txtNews) {
            private static final long serialVersionUID = 1L;
            {
                setBorder(null);
                getViewport().setOpaque(false);
                setOpaque(false);
            }
        }, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.55;
        gbc.weighty = 0.25;
        center.add(newsCard, gbc);

        // ---- CARD 5: FORMA + JUGADORES CLAVE + PREDICCIÓN ----
        RoundedPanel insightCard = new RoundedPanel(new Color(18, 24, 68, 230));
        insightCard.setLayout(new BoxLayout(insightCard, BoxLayout.Y_AXIS));
        insightCard.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblInsightTitle = new JLabel("Informe rápido del equipo");
        lblInsightTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInsightTitle.setForeground(new Color(230, 240, 255));
        lblInsightTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblForm = new JLabel("", SwingConstants.LEFT);
        lblForm.setForeground(new Color(210, 220, 255));
        lblForm.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblBestPlayer = new JLabel("", SwingConstants.LEFT);
        lblBestPlayer.setForeground(new Color(210, 220, 255));
        lblBestPlayer.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblYoungPlayer = new JLabel("", SwingConstants.LEFT);
        lblYoungPlayer.setForeground(new Color(210, 220, 255));
        lblYoungPlayer.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblWinProb = new JLabel("", SwingConstants.LEFT);
        lblWinProb.setForeground(new Color(170, 235, 210));
        lblWinProb.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        insightCard.add(lblInsightTitle);
        insightCard.add(Box.createVerticalStrut(8));
        insightCard.add(lblForm);
        insightCard.add(Box.createVerticalStrut(6));
        insightCard.add(lblBestPlayer);
        insightCard.add(Box.createVerticalStrut(4));
        insightCard.add(lblYoungPlayer);
        insightCard.add(Box.createVerticalStrut(8));
        insightCard.add(lblWinProb);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.45;
        gbc.weighty = 0.25;
        center.add(insightCard, gbc);

        // Actualizar tarjetas dinámicas
        updateNextMatchCard();
        updateNewsCard();
        updateInsightCard();

        // ============================
        //  LISTENERS DE BOTONES
        // ============================

        // CLASIFICACIÓN
        btnClasificacion.addActionListener(e -> {
            List<Equipo> liga = DataManager.cargarLigaDeBD();
            ClasificacionDAO cdao = new ClasificacionDAO(DataManager.getGestor());
            cdao.cargarClasificacion(liga);
            new ClassificationWindow(this, equipo, liga);
        });

        // MERCADO
        btnMercado.addActionListener(e -> {
            MarketWindow mw = new MarketWindow(this, equipo);
            mw.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    lblBudget.setText("Presupuesto: " + LeagueData.formatMoney(equipo.getBudget()));
                    lblAvgRating.setText("Media once: " + calcularMediaOnce(equipo) + " / 99");
                    lblRating.setText("Media equipo: " + calcularMediaEquipo(equipo) + " / 100");
                    updateOverviewText(txtOverview);
                    updateNewsCard();
                    updateInsightCard();
                }
            });
        });

        // PLANTILLA / TÁCTICAS
        btnPlantilla.addActionListener(e -> new SquadTacticsWindow(this, equipo));

        // CALENDARIO
        btnCalendario.addActionListener(e ->
                new CalendarWindow(this, equipo, session.getLiga(), session)
        );

        // CALENDARIO MENSUAL
        btnCalendarioMensual.addActionListener(e ->
                new MonthlyCalendarWindow(this, equipo, session.getLiga(), session)
        );

        // GUARDAR
        btnGuardar.addActionListener(e -> {
        	SaveManager.guardarPartida(session, session.getSlot());
            JOptionPane.showMessageDialog(this, "Partida guardada correctamente.");
        });

        // ATRÁS (VOLVER A WELCOME)
        btnAtras.addActionListener(e -> {
            dispose();
            new WelcomeWindow().setVisible(true);
        });

        // SIMULAR TEMPORADA ENTERA
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
                    updateOverviewText(txtOverview);
                    updateNextMatchCard();
                    updateNewsCard();
                    updateInsightCard();
                });
            }, "SimulacionLigaThread");

            t.start();
        });

        // SIMULAR SIGUIENTE PARTIDO (JORNADA)
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

            List<LeagueCalendar.Match> jornadaPartidos = calendario.get(jornada - 1);

            LeagueCalendar.Match miPartido = null;
            for (LeagueCalendar.Match m : jornadaPartidos) {
                if (m.local().equals(equipo.getNombre()) ||
                        m.visitante().equals(equipo.getNombre())) {
                    miPartido = m;
                    break;
                }
            }

            if (miPartido == null) {
                JOptionPane.showMessageDialog(this,
                        "Tu equipo no juega esta jornada (descansa).");
                session.avanzarJornada();
                updateNextMatchCard();
                updateNewsCard();
                updateInsightCard();
                return;
            }

            btnSimularPartido.setEnabled(false);
            final int jornadaFinal = jornada;

            Thread t = new Thread(() -> {

                MatchSimulator.simularJornadaCompleta(jornadaPartidos, session.getLiga());

                SwingUtilities.invokeLater(() -> {
                    btnSimularPartido.setEnabled(true);
                    JOptionPane.showMessageDialog(this,
                            "Jornada " + jornadaFinal + " simulada.\n" +
                                    "Consulta la Clasificación para ver los resultados.");

                    session.avanzarJornada();
                    updateOverviewText(txtOverview);
                    updateNextMatchCard();
                    updateNewsCard();
                    updateInsightCard();
                });
            }, "SimulacionJornadaThread");

            t.start();
        });

        // ESC → volver
        JRootPane rootPane = getRootPane();
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "volver");
        am.put("volver", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAtras.doClick();
            }
        });
    }

    // ============================
    //  UI HELPERS
    // ============================

    private JButton createMenuButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setForeground(new Color(230, 235, 255));
        b.setBackground(new Color(30, 45, 110));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(55, 80, 160));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(30, 45, 110));
            }
        });

        return b;
    }

    private JLabel createStatLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(200, 210, 235));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private JLabel createStatValueLabel(java.util.function.Supplier<String> supplier) {
        JLabel l = new JLabel(supplier.get());
        l.setForeground(new Color(140, 230, 200));
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }

    // ============================
    //  LÓGICA VISUAL
    // ============================

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
        sb.append("Usa el menú de la izquierda para acceder a:\n");
        sb.append(" • Clasificación\n");
        sb.append(" • Mercado de fichajes\n");
        sb.append(" • Plantilla y tácticas\n");
        sb.append(" • Calendario y simulación de partidos\n");

        area.setText(sb.toString());
        area.setCaretPosition(0);
    }

    private void updateNextMatchCard() {
        List<List<LeagueCalendar.Match>> calendario = session.getCalendario();
        nextMatch = null;
        nextMatchJornada = -1;

        if (calendario == null || calendario.isEmpty()) {
            lblNextMatchTeams.setText("Sin calendario generado");
            lblNextMatchDate.setText("Abre la ventana de Calendario");
            lblNextMatchInfo.setText("");
            return;
        }

        int jActual = session.getJornadaActual();
        if (jActual < 1) jActual = 1;

        LeagueCalendar.Match siguiente = null;
        int jornadaIndex = -1;

        // Buscar el próximo partido del equipo a partir de la jornada actual
        for (int j = jActual - 1; j < calendario.size(); j++) {
            List<LeagueCalendar.Match> jornada = calendario.get(j);
            for (LeagueCalendar.Match m : jornada) {
                if (m.local().equals(equipo.getNombre()) ||
                        m.visitante().equals(equipo.getNombre())) {
                    siguiente = m;
                    jornadaIndex = j + 1;
                    break;
                }
            }
            if (siguiente != null) break;
        }

        if (siguiente == null) {
            lblNextMatchTeams.setText("No hay más partidos.");
            lblNextMatchDate.setText("Temporada finalizada.");
            lblNextMatchInfo.setText("");
            nextMatch = null;
            nextMatchJornada = -1;
            return;
        }

        nextMatch = siguiente;
        nextMatchJornada = jornadaIndex;

        String vsText = siguiente.local() + "  vs  " + siguiente.visitante();
        lblNextMatchTeams.setText(vsText);

        LocalDate fecha = siguiente.fecha();
        lblNextMatchDate.setText("Jornada " + jornadaIndex + "  •  " + fecha + "  •  " + siguiente.hora());

        boolean local = siguiente.local().equals(equipo.getNombre());
        String donde = local ? "Juegas como LOCAL" : "Juegas como VISITANTE";
        lblNextMatchInfo.setText("LaLiga EA Sports  •  " + donde);
    }

    private void updateNewsCard() {
        StringBuilder sb = new StringBuilder();

        List<Jugador> plantilla = equipo.getPlantilla();

        if (!plantilla.isEmpty()) {
            Jugador mejor = plantilla.stream()
                    .max(Comparator.comparingDouble(Jugador::getValoracion))
                    .orElse(null);

            Jugador joven = plantilla.stream()
                    .min(Comparator.comparingInt(Jugador::getEdad))
                    .orElse(null);

            if (mejor != null) {
                sb.append("• Rumores: varios clubes siguen de cerca a ")
                        .append(mejor.getNombre())
                        .append(" tras su rendimiento reciente.\n");
            }

            if (joven != null) {
                sb.append("• Cantera: ")
                        .append(joven.getNombre())
                        .append(" podría tener más minutos esta temporada.\n");
            }
        }

        sb.append("• Dirección deportiva de ")
                .append(equipo.getNombre())
                .append(" rastrea el mercado en busca de oportunidades.\n");

        sb.append("• Presupuesto disponible: ")
                .append(LeagueData.formatMoney(equipo.getBudget()))
                .append(" para incorporaciones.\n");

        txtNews.setText(sb.toString());
        txtNews.setCaretPosition(0);
    }

    private void updateInsightCard() {
        // Forma
        TeamStats stats = equipo.getStats();
        String forma = generarTextoForma(stats);
        lblForm.setText("Forma reciente: " + forma);

        // Jugadores clave
        List<Jugador> plantilla = equipo.getPlantilla();
        if (!plantilla.isEmpty()) {
            Jugador mejor = plantilla.stream()
                    .max(Comparator.comparingDouble(Jugador::getValoracion))
                    .orElse(null);

            Jugador joven = plantilla.stream()
                    .min(Comparator.comparingInt(Jugador::getEdad))
                    .orElse(null);

            if (mejor != null) {
                lblBestPlayer.setText("Mejor jugador: " + mejor.getNombre() +
                        " (media " + (int) mejor.getValoracion() + ")");
            } else {
                lblBestPlayer.setText("Mejor jugador: -");
            }

            if (joven != null) {
                lblYoungPlayer.setText("Joven promesa: " + joven.getNombre() +
                        " (" + joven.getEdad() + " años)");
            } else {
                lblYoungPlayer.setText("Joven promesa: -");
            }
        } else {
            lblBestPlayer.setText("Mejor jugador: -");
            lblYoungPlayer.setText("Joven promesa: -");
        }

        // Probabilidad partido
        if (nextMatch != null) {
            double[] probs = calcularProbabilidades(nextMatch);

            String local = nextMatch.local();
            String visit = nextMatch.visitante();

            double pVictoria, pEmpate, pDerrota;
            if (equipo.getNombre().equals(local)) {
                pVictoria = probs[0];
                pEmpate = probs[1];
                pDerrota = probs[2];
            } else {
                pVictoria = probs[2];
                pEmpate = probs[1];
                pDerrota = probs[0];
            }

            int v = (int) Math.round(pVictoria * 100);
            int e = (int) Math.round(pEmpate * 100);
            int d = (int) Math.round(pDerrota * 100);

            lblWinProb.setText("<html><b>Probabilidad estimada próximo partido:</b><br>" +
                    "Victoria: " + v + "% &nbsp;&nbsp; Empate: " + e + "% &nbsp;&nbsp; Derrota: " + d + "%</html>");
        } else {
            lblWinProb.setText("Probabilidad estimada: sin partido programado.");
        }
    }

    private String generarTextoForma(TeamStats stats) {
        int v = stats.getVictorias();
        int e = stats.getEmpates();
        int d = stats.getDerrotas();
        int pj = v + e + d;

        if (pj == 0) return "— Sin partidos jugados —";

        double ppg = (double) stats.getPuntos() / pj;

        if (ppg >= 2.3) return "Excelente";
        if (ppg >= 1.7) return "Buena";
        if (ppg >= 1.0) return "Irregular";
        return "Preocupante";
    }

    // devuelve [pLocal, pEmpate, pVisitante]
    private double[] calcularProbabilidades(LeagueCalendar.Match match) {
        Equipo localEq = null;
        Equipo visitEq = null;

        for (Equipo e : session.getLiga()) {
            if (e.getNombre().equals(match.local())) localEq = e;
            if (e.getNombre().equals(match.visitante())) visitEq = e;
        }

        if (localEq == null || visitEq == null) {
            return new double[]{0.33, 0.34, 0.33};
        }

        int mediaLocal = calcularMediaOnce(localEq);
        int mediaVisit = calcularMediaOnce(visitEq);

        // pequeña ventaja de campo
        mediaLocal += 3;

        double diff = (mediaLocal - mediaVisit) / 20.0; // normalizamos
        double pLocal = 0.4 + diff;  // base
        double pEmpate = 0.25;
        double pVisit = 1.0 - pLocal - pEmpate;

        // Ajustes y límites
        if (pLocal < 0.15) pLocal = 0.15;
        if (pLocal > 0.7) pLocal = 0.7;

        if (pVisit < 0.15) pVisit = 0.15;
        if (pVisit > 0.7) pVisit = 0.7;

        double sum = pLocal + pEmpate + pVisit;
        pLocal /= sum;
        pEmpate /= sum;
        pVisit /= sum;

        return new double[]{pLocal, pEmpate, pVisit};
    }

    // ============================
    //  MÉTODOS ESTÁTICOS EXISTENTES
    // ============================

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
            sum += j.getValoracion();
        }

        return (int) Math.round(sum / e.getPlantilla().size());
    }

    // ============================
    //  CLASES DE ESTILO INTERNA
    // ============================

    // Panel con fondo en gradiente
    private static class GradientPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            int h = getHeight();

            Color c1 = new Color(5, 10, 30);
            Color c2 = new Color(15, 35, 90);

            GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }

    // Panel con esquinas redondeadas
    private static class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Color backgroundColor;
        private final int cornerRadius = 18;

        public RoundedPanel(Color bg) {
            super();
            this.backgroundColor = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

            g2.setColor(new Color(40, 60, 130, 180));
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}