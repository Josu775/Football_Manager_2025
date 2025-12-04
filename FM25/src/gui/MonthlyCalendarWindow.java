package gui;

import domain.Equipo;
import domain.GameSession;
import domain.LeagueCalendar;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MonthlyCalendarWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private final List<Equipo> liga;
    private final GameSession session;
    private final Equipo equipoJugador;

    private List<List<LeagueCalendar.Match>> calendario;

    private LocalDate fechaActual = LocalDate.now();

    private JPanel gridPanel;
    private JLabel lblMes;
    
    private final Map<String, ImageIcon> cacheEscudos = new HashMap<>();

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
        ESCUDOS.put("RC Celta", "celta.png");
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
        gridPanel = new JPanel(new GridLayout(7, 7)); // fila de días + 6 semanas
        add(gridPanel, BorderLayout.CENTER);
    }


    private void pintarPartidosEnCelda(JPanel panel, LocalDate fecha) {

        int jornada = obtenerJornadaPorDia(fecha);

        if (jornada == -1) return;

        List<LeagueCalendar.Match> partidos = calendario.get(jornada - 1);

        for (LeagueCalendar.Match m : partidos) {

            // Solo mostrar SI JUEGA TU EQUIPO
            boolean esMiPartido = m.local().equals(equipoJugador.getNombre()) ||
                                  m.visitante().equals(equipoJugador.getNombre());

            if (!esMiPartido) continue;   // IGNORAR EL RESTO

            JPanel partidoPanel = new JPanel();
            partidoPanel.setOpaque(false);

            if (esMiPartido) {
                partidoPanel.setBackground(new Color(40, 90, 180));
                partidoPanel.setOpaque(true);
            }

            ImageIcon iconLocal = getEscudoIcon(m.local());
            ImageIcon iconVisit = getEscudoIcon(m.visitante());

            JLabel lblLocal = new JLabel(iconLocal != null ? iconLocal : new ImageIcon());
            JLabel lblVisit = new JLabel(iconVisit != null ? iconVisit : new ImageIcon());

            partidoPanel.add(lblLocal);
            partidoPanel.add(new JLabel("vs"));
            partidoPanel.add(lblVisit);

            panel.add(partidoPanel);
        }
    }


    private void generarVista() {
        gridPanel.removeAll();

        // Título del mes
        lblMes.setText(fechaActual.getMonth().toString() + " " + fechaActual.getYear());

        // Cabecera de días
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

        // === Celdas ===
        for (int i = 0; i < 42; i++) {
            JPanel diaPanel = new JPanel();
            diaPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            diaPanel.setLayout(new BoxLayout(diaPanel, BoxLayout.Y_AXIS));
            diaPanel.setBackground(new Color(22, 30, 60));

            int diaMes = i - offset + 1;

            if (diaMes >= 1 && diaMes <= diasMes) {
                JLabel lbl = new JLabel(String.valueOf(diaMes));
                lbl.setForeground(Color.WHITE);
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                diaPanel.add(lbl);

                // Fecha completa del día
                LocalDate fechaDia = fechaActual.withDayOfMonth(diaMes);

                // Pintar partidos para esa fecha
                pintarPartidosEnCelda(diaPanel, fechaDia);
            }

            gridPanel.add(diaPanel);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private ImageIcon getEscudoIcon(String equipo) {
        // Cache: si ya está cargado, usarlo
        if (cacheEscudos.containsKey(equipo)) {
            return cacheEscudos.get(equipo);
        }

        String file = ESCUDOS.get(equipo);

        if (file == null) {
            cacheEscudos.put(equipo, null);
            return null;
        }

        String path = "resources/images/escudos/" + file;
        ImageIcon icon = new ImageIcon(path);

        // Imagen no encontrada → guardamos null para no intentar cargar otra vez
        if (icon.getIconWidth() <= 0) {
            cacheEscudos.put(equipo, null);
            return null;
        }

        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaled = new ImageIcon(img);

        cacheEscudos.put(equipo, scaled);
        return scaled;
    }



    private int obtenerJornadaPorDia(LocalDate fecha) {
        Map<Integer, LocalDate> fechas = session.getFechasJornadas();

        for (int j = 1; j <= fechas.size(); j++) {
            if (fechas.get(j).equals(fecha)) return j;
        }

        return -1;
    }

}
