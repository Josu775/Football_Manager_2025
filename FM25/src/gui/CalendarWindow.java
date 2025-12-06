package gui;

import domain.Equipo;
import domain.GameSession;
import domain.LeagueCalendar;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Equipo equipo;
    private final GameSession session;
    private final List<Equipo> liga;

    private List<List<LeagueCalendar.Match>> calendario;
    private int jornadaActual = 1;

    private JPanel panelPartido;
    private JLabel lblTitulo;
    private JLabel lblEstadio;
    private JLabel lblFecha;

    private static final Color FONDO = new Color(10, 16, 36);
    private static final Color TEXTO = Color.WHITE;

    // ================================
    // ESCUDOS
    // ================================
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
        ESCUDOS.put("Real Oviedo", "realoviedo.png");
        ESCUDOS.put("Levante UD", "levante.png");
    }

    private ImageIcon getEscudo(String equipo) {
        String file = ESCUDOS.get(equipo);
        if (file == null) return null;

        ImageIcon icon = new ImageIcon("resources/images/escudos/" + file);
        if (icon.getIconWidth() <= 0) return null;

        Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // ================================
    // CONSTRUCTOR
    // ================================
    public CalendarWindow(JFrame parent, Equipo equipo, List<Equipo> liga, GameSession session) {
        super("Calendario - " + equipo.getNombre());
        this.equipo = equipo;
        this.liga = liga;
        this.session = session;

        calendario = session.getCalendario();
        if (calendario == null || calendario.isEmpty()) {
            calendario = LeagueCalendar.generarCalendario(liga);
            session.setCalendario(calendario);
        }

        setSize(675, 400);
        setLocationRelativeTo(parent);
        initUI();
        setVisible(true);
    }

    private void initUI() {

        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(FONDO);
        add(fondo);

        lblTitulo = new JLabel("Jornada 1", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(TEXTO);
        fondo.add(lblTitulo, BorderLayout.NORTH);

        // PANEL CENTRAL REAL – ya no se expande hacia abajo
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(FONDO);
        fondo.add(center, BorderLayout.CENTER);

        // --- Pegamos la parte de arriba ---
        center.add(Box.createVerticalGlue());
        center.add(Box.createVerticalStrut(15));

        panelPartido = new JPanel();
        panelPartido.setBackground(FONDO);
        panelPartido.setLayout(new BoxLayout(panelPartido, BoxLayout.X_AXIS));
        panelPartido.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(panelPartido);

        // Estadio más arriba
        center.add(Box.createVerticalStrut(6));

        lblEstadio = new JLabel("", SwingConstants.CENTER);
        lblEstadio.setFont(new Font("Segoe UI", Font.BOLD, 21));
        lblEstadio.setForeground(TEXTO);
        lblEstadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(lblEstadio);

        center.add(Box.createVerticalStrut(4));

        lblFecha = new JLabel("", SwingConstants.CENTER);
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblFecha.setForeground(TEXTO);
        lblFecha.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(lblFecha);

        center.add(Box.createVerticalGlue()); // >>> ESTO evita que baje <<<

        // --- BOTONES ---
        JPanel bottom = new JPanel();
        bottom.setBackground(FONDO);
        JButton btnPrev = new JButton("← Jornada anterior");
        JButton btnNext = new JButton("Siguiente jornada →");
        bottom.add(btnPrev);
        bottom.add(btnNext);
        fondo.add(bottom, BorderLayout.SOUTH);

        btnPrev.addActionListener(e -> {
            jornadaActual = (jornadaActual == 1) ? calendario.size() : jornadaActual - 1;
            actualizarVista();
        });

        btnNext.addActionListener(e -> {
            jornadaActual = (jornadaActual == calendario.size()) ? 1 : jornadaActual + 1;
            actualizarVista();
        });

        actualizarVista();
    }

    private void actualizarVista() {

        lblTitulo.setText("Jornada " + jornadaActual);

        panelPartido.removeAll();

        List<LeagueCalendar.Match> jornada = calendario.get(jornadaActual - 1);

        LeagueCalendar.Match miPartido = null;
        for (LeagueCalendar.Match m : jornada) {
            if (m.local().equals(equipo.getNombre()) || m.visitante().equals(equipo.getNombre())) {
                miPartido = m;
                break;
            }
        }

        if (miPartido == null) {
            JLabel rest = new JLabel("Tu equipo descansa esta jornada.");
            rest.setFont(new Font("Segoe UI", Font.BOLD, 22));
            rest.setForeground(TEXTO);
            panelPartido.add(rest);
            lblEstadio.setText("");
            lblFecha.setText("");
            panelPartido.revalidate();
            panelPartido.repaint();
            return;
        }

        ImageIcon escLocal = getEscudo(miPartido.local());
        ImageIcon escVisit = getEscudo(miPartido.visitante());

        JLabel lblLocalEsc = new JLabel(escLocal);
        JLabel lblVisitEsc = new JLabel(escVisit);

        JLabel lblLocal = new JLabel(miPartido.local());
        lblLocal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLocal.setForeground(TEXTO);

        JLabel lblVs = new JLabel("  VS  ");
        lblVs.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblVs.setForeground(TEXTO);

        JLabel lblVisit = new JLabel(miPartido.visitante());
        lblVisit.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVisit.setForeground(TEXTO);

        panelPartido.add(lblLocalEsc);
        panelPartido.add(Box.createHorizontalStrut(18));
        panelPartido.add(lblLocal);
        panelPartido.add(Box.createHorizontalStrut(30));
        panelPartido.add(lblVs);
        panelPartido.add(Box.createHorizontalStrut(30));
        panelPartido.add(lblVisit);
        panelPartido.add(Box.createHorizontalStrut(18));
        panelPartido.add(lblVisitEsc);

        lblEstadio.setText("Estadio: " + buscarEstadio(miPartido.local()));
        lblFecha.setText("Fecha: " + miPartido.fecha() + "    Hora: " + miPartido.hora());

        panelPartido.revalidate();
        panelPartido.repaint();
    }

    private String buscarEstadio(String nombreEquipo) {
        for (Equipo e : liga) {
            if (e.getNombre().equals(nombreEquipo)) {
                return e.getEstadio();
            }
        }
        return "Estadio desconocido";
    }
}
