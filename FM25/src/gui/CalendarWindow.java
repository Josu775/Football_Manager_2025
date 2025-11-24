package gui;

import domain.Equipo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CalendarWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private final Equipo equipo;
    private final List<MatchInfo> jornadas;
    private int jornadaActual = 1;
    private JLabel lblInfo;

    // AHORA recibe también la lista de equipos de la liga
    public CalendarWindow(JFrame parent, Equipo equipo, List<Equipo> liga) {
        super("Calendario - " + equipo.getNombre());
        this.equipo = equipo;
        this.jornadas = generarJornadas(liga);
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

        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "prev");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "next");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");

        am.put("prev", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                btnPrev.doClick();
            }
        });
        am.put("next", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                btnNext.doClick();
            }
        });
        am.put("cerrar", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
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

    // AHORA usa la lista de la sesión, no vuelve a llamar a LeagueData
    private List<MatchInfo> generarJornadas(List<Equipo> liga) {
        List<MatchInfo> lista = new ArrayList<>();

        // Copia modificable de la liga y quitamos nuestro propio equipo
        List<Equipo> equipos = new ArrayList<>(liga);
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
        private int jornada;
        String rival;
        String estadio;
        String hora;
        int posicion;
        boolean local;
        MatchInfo(int j, String r, String e, String h, int p, boolean l) {
            setJornada(j);
            rival = r;
            estadio = e;
            hora = h;
            posicion = p;
            local = l;
        }
        @SuppressWarnings("unused")
        public int getJornada() {
            return jornada;
        }
        public void setJornada(int jornada) {
            this.jornada = jornada;
        }
    }
}
