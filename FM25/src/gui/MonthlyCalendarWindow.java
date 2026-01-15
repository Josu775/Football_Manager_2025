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

    private final List<List<LeagueCalendar.Match>> calendario;

    private LocalDate fechaActual = LocalDate.now();

    private JPanel gridPanel;
    private JLabel lblMes;

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
        this.calendario = session.getCalendario();

        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        generarVista();

        setVisible(true);
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(10, 16, 36));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(main);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(new Color(10, 16, 36));
        top.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");

        styleNavButton(btnPrev);
        styleNavButton(btnNext);

        lblMes = new JLabel("", SwingConstants.CENTER);
        lblMes.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblMes.setForeground(new Color(230, 240, 255));

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

        main.add(top, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(7, 7, 6, 6));
        gridPanel.setBackground(new Color(10, 16, 36));
        main.add(gridPanel, BorderLayout.CENTER);
    }

    private void generarVista() {
        gridPanel.removeAll();

        String nombreMes = fechaActual.getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("es", "ES"))
                .toUpperCase();
        lblMes.setText(nombreMes + " " + fechaActual.getYear());

        String[] dias = {"L", "M", "X", "J", "V", "S", "D"};
        for (String d : dias) {
            JLabel l = new JLabel(d, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 16));
            l.setForeground(Color.WHITE);

            JPanel headerCell = new JPanel(new BorderLayout());
            headerCell.setBackground(new Color(70, 30, 120));
            headerCell.setBorder(BorderFactory.createLineBorder(new Color(90, 110, 200)));
            headerCell.add(l, BorderLayout.CENTER);

            gridPanel.add(headerCell);
        }

        LocalDate primero = fechaActual.withDayOfMonth(1);
        int diaSemana = primero.getDayOfWeek().getValue();
        int offset = diaSemana - 1;
        int diasMes = fechaActual.lengthOfMonth();

        for (int i = 0; i < 42; i++) {
            JPanel diaPanel = new JPanel();
            diaPanel.setBorder(BorderFactory.createLineBorder(new Color(90, 110, 200)));
            diaPanel.setLayout(new BoxLayout(diaPanel, BoxLayout.Y_AXIS));
            diaPanel.setBackground(new Color(20, 28, 60));

            int diaMes = i - offset + 1;

            if (diaMes >= 1 && diaMes <= diasMes) {
                LocalDate fechaDia = fechaActual.withDayOfMonth(diaMes);

                JLabel lbl = new JLabel(String.valueOf(diaMes));
                lbl.setForeground(new Color(230, 240, 255));
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

                diaPanel.add(Box.createVerticalStrut(4));
                diaPanel.add(lbl);

                pintarPartidosEnCelda(diaPanel, fechaDia);
            } else {
                diaPanel.setBackground(new Color(14, 20, 44));
            }

            gridPanel.add(diaPanel);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void pintarPartidosEnCelda(JPanel panel, LocalDate fecha) {
        if (calendario == null) return;

        for (List<LeagueCalendar.Match> jornada : calendario) {
            for (LeagueCalendar.Match m : jornada) {

                if (!m.fecha().equals(fecha)) continue;

                boolean esMiPartido =
                        m.local().equals(equipoJugador.getNombre()) ||
                        m.visitante().equals(equipoJugador.getNombre());

                if (!esMiPartido) continue;

                JPanel partidoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
                partidoPanel.setOpaque(true);
                partidoPanel.setBackground(new Color(90, 140, 230));
                partidoPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 240, 255)));
                partidoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                ImageIcon iconLocal = getEscudoIcon(m.local());
                ImageIcon iconVisit = getEscudoIcon(m.visitante());

                JLabel lblLocal = new JLabel(iconLocal);
                JLabel lblVs = new JLabel("vs");
                lblVs.setForeground(Color.WHITE);
                lblVs.setFont(new Font("Segoe UI", Font.BOLD, 12));
                JLabel lblVisit = new JLabel(iconVisit);

                partidoPanel.add(lblLocal);
                partidoPanel.add(lblVs);
                partidoPanel.add(lblVisit);

                panel.add(Box.createVerticalStrut(6));
                panel.add(partidoPanel);
                return;
            }
        }
    }

    private ImageIcon getEscudoIcon(String equipo) {
        String file = ESCUDOS.get(equipo);
        if (file == null) return new ImageIcon();

        String path = "resources/images/escudos/" + file;
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() <= 0) return new ImageIcon();

        Image img = icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void styleNavButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(new Color(90, 140, 230));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setPreferredSize(new Dimension(60, 36));
    }
}
