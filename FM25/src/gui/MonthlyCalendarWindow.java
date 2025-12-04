package gui;

import domain.Equipo;
import domain.GameSession;
import domain.LeagueCalendar;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MonthlyCalendarWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private final List<Equipo> liga;
    private final GameSession session;
    private final Equipo equipoJugador;

    // calendario[jornada] -> lista de partidos
    private final List<List<LeagueCalendar.Match>> calendario;

    private LocalDate fechaActual = LocalDate.now();

    private JPanel gridPanel;
    private JLabel lblMes;

    // === Escudos ===
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
        ESCUDOS.put("Real Club Celta de Vigo", "celta.png");
        ESCUDOS.put("Getafe CF", "getafe.png");
        ESCUDOS.put("Rayo Vallecano", "rayovallecano.png");
        ESCUDOS.put("Deportivo Alavés", "alaves.png");
        ESCUDOS.put("Elche CF", "elche.png");
        ESCUDOS.put("RCD Espanyol", "espanyol.png");
        ESCUDOS.put("Girona FC", "girona.png");
    }

    public MonthlyCalendarWindow(JFrame parent, Equipo equipoJugador, List<Equipo> liga, GameSession session) {
        super("Calendario mensual");
        this.liga = liga;
        this.session = session;
        this.equipoJugador = equipoJugador;

        this.calendario = session.getCalendario();   // ya generado al iniciar partida

        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        generarVista();

        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ======= Barra superior =======
        JPanel top = new JPanel(new BorderLayout());
        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");

        lblMes = new JLabel("", SwingConstants.CENTER);
        lblMes.setFont(new Font("Segoe UI", Font.BOLD, 24));

        btnPrev.addActionListener(e -> {
            fechaActual = fechaActual.minusMonths(1);
            generarVista();
        });

        btnNext.addActionListener(e -> {
            fechaActual = fechaActual.plusMonths(1);
            generarVista();
        });

        top.add(btnPrev, BorderLayout.WEST);
        top.add(lblMes, BorderLayout.CENTER);
        top.add(btnNext, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // ======= Panel de cuadrícula =======
        gridPanel = new JPanel(new GridLayout(7, 7)); // cabecera + 6 semanas
        add(gridPanel, BorderLayout.CENTER);
    }

    private void generarVista() {
        gridPanel.removeAll();

        // Título del mes en bonito
        String nombreMes = fechaActual.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
                .toUpperCase();
        lblMes.setText(nombreMes + " " + fechaActual.getYear());

        // Cabecera días semana
        String[] dias = {"L", "M", "X", "J", "V", "S", "D"};
        for (String d : dias) {
            JLabel l = new JLabel(d, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 16));
            gridPanel.add(l);
        }

        // Primer día del mes
        LocalDate primero = fechaActual.withDayOfMonth(1);
        int diaSemana = primero.getDayOfWeek().getValue(); // 1 = lunes
        int offset = diaSemana - 1;
        int diasMes = fechaActual.lengthOfMonth();

        // Celdas del calendario
        for (int i = 0; i < 42; i++) {
            JPanel diaPanel = new JPanel();
            diaPanel.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 90)));
            diaPanel.setLayout(new BoxLayout(diaPanel, BoxLayout.Y_AXIS));
            diaPanel.setBackground(new Color(10, 16, 36));

            int diaMes = i - offset + 1;

            if (diaMes >= 1 && diaMes <= diasMes) {
                LocalDate fechaDia = fechaActual.withDayOfMonth(diaMes);

                JLabel lbl = new JLabel(String.valueOf(diaMes));
                lbl.setForeground(Color.WHITE);
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                diaPanel.add(lbl);

                // Partidos de TU equipo este día
                pintarPartidosEnCelda(diaPanel, fechaDia);
            }

            gridPanel.add(diaPanel);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // Solo pinta el partido de TU equipo en esa fecha (si lo hay)
    private void pintarPartidosEnCelda(JPanel panel, LocalDate fecha) {
        if (calendario == null) return;

        for (List<LeagueCalendar.Match> jornada : calendario) {
            for (LeagueCalendar.Match m : jornada) {

                // Fecha distinta → siguiente
                if (!m.fecha().equals(fecha)) continue;

                boolean esMiPartido =
                        m.local().equals(equipoJugador.getNombre()) ||
                        m.visitante().equals(equipoJugador.getNombre());

                // Solo mostramos partidos de tu equipo
                if (!esMiPartido) continue;

                JPanel partidoPanel = new JPanel();
                partidoPanel.setOpaque(true);
                partidoPanel.setBackground(new Color(40, 90, 180));
                partidoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                ImageIcon iconLocal  = getEscudoIcon(m.local());
                ImageIcon iconVisit  = getEscudoIcon(m.visitante());

                JLabel lblLocal = new JLabel(iconLocal);
                JLabel lblVs    = new JLabel("vs");
                lblVs.setForeground(Color.WHITE);
                JLabel lblVisit = new JLabel(iconVisit);

                partidoPanel.add(lblLocal);
                partidoPanel.add(lblVs);
                partidoPanel.add(lblVisit);

                panel.add(Box.createVerticalStrut(4));
                panel.add(partidoPanel);

                // Solo habrá 1 partido de tu equipo por día → podemos salir
                return;
            }
        }
    }

    private ImageIcon getEscudoIcon(String equipo) {
        String file = ESCUDOS.get(equipo);
        if (file == null) return new ImageIcon(); // vacío

        String path = "resources/images/escudos/" + file;
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() <= 0) return new ImageIcon();

        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
