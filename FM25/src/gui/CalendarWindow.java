package gui;

import domain.Equipo;
import domain.LeagueData;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CalendarWindow interactivo: muestra jornada actual con rival, estadio, hora, local/visitante y botones siguiente/anterior.
 */
public class CalendarWindow extends JFrame {

    private final Equipo equipo;
    private final List<MatchInfo> jornadas;
    private int jornadaActual = 1;
    private JLabel lblInfo;

    public CalendarWindow(JFrame parent, Equipo equipo) {
        super("Calendario - " + equipo.getNombre());
        this.equipo = equipo;
        this.jornadas = generarJornadas();
        setSize(650, 400);
        setLocationRelativeTo(parent);
        init();
        setVisible(true);
    }

    private void init() {
        setLayout(new BorderLayout());
        lblInfo = new JLabel("", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 16));
        lblInfo.setVerticalAlignment(SwingConstants.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.add(lblInfo, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton btnPrev = new JButton("← Jornada anterior");
        JButton btnNext = new JButton("Siguiente jornada →");
        botones.add(btnPrev);
        botones.add(btnNext);
        add(botones, BorderLayout.SOUTH);

        btnPrev.addActionListener(e -> {
            jornadaActual = (jornadaActual == 1) ? 38 : jornadaActual - 1;
            actualizarVista();
        });

        btnNext.addActionListener(e -> {
            jornadaActual = (jornadaActual == 38) ? 1 : jornadaActual + 1;
            actualizarVista();
        });

        actualizarVista();
    }

    private void actualizarVista() {
        MatchInfo m = jornadas.get(jornadaActual - 1);
        String html = "<html><div style='text-align:center;'>"
                + "<h2>Jornada " + jornadaActual + "</h2>"
                + "<b>Rival:</b> " + m.rival + "<br>"
                + "<b>Estadio:</b> " + m.estadio + "<br>"
                + "<b>Hora:</b> " + m.hora + "<br>"
                + "<b>Posición rival:</b> " + m.posicion + "<br>"
                + "<b>Condición:</b> " + (m.local ? "Local" : "Visitante")
                + "</div></html>";
        lblInfo.setText(html);
    }

    private List<MatchInfo> generarJornadas() {
        List<MatchInfo> lista = new ArrayList<>();
        List<Equipo> equipos = LeagueData.getLaLiga20();
        equipos.removeIf(e -> e.getNombre().equals(equipo.getNombre()));
        Random rnd = new Random();

        for (int i = 1; i <= 38; i++) {
            Equipo rival = equipos.get(rnd.nextInt(equipos.size()));
            boolean local = rnd.nextBoolean();
            String estadio = local ? equipo.getEstadio() : rival.getEstadio();
            String hora = LocalTime.of(13 + rnd.nextInt(6), 0).toString() + "h";
            int pos = 1 + rnd.nextInt(20);
            lista.add(new MatchInfo(i, rival.getNombre(), estadio, hora, pos, local));
        }
        return lista;
    }

    private static class MatchInfo {
        int jornada;
        String rival;
        String estadio;
        String hora;
        int posicion;
        boolean local;
        MatchInfo(int j, String r, String e, String h, int p, boolean l) {
            jornada = j; rival = r; estadio = e; hora = h; posicion = p; local = l;
        }
    }
}
