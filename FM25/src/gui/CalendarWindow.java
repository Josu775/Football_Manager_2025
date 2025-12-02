package gui;

import domain.Equipo;
import domain.GameSession;

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
    private List<MatchInfo> jornadas;
    private int jornadaActual = 1;
    private JLabel lblInfo;

    private final GameSession session;

    // AHORA recibe también la sesión, para guardar / reutilizar el calendario
    public CalendarWindow(JFrame parent, Equipo equipo, List<Equipo> liga, GameSession session) {
        super("Calendario - " + equipo.getNombre());
        this.equipo = equipo;
        this.session = session;

        // Si ya hay calendario guardado en la sesión, lo reutilizamos
        List<Object[]> calGuardado = session.getCalendario();
        if (calGuardado == null || calGuardado.isEmpty()) {
            this.jornadas = generarJornadas(liga);
            this.session.setCalendario(convertirAListaSimple(this.jornadas));
        } else {
            this.jornadas = reconstruirDesdeListaSimple(calGuardado);
        }

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
            jornadaActual = (jornadaActual == 1) ? jornadas.size() : jornadaActual - 1;
            actualizarVista();
        });

        btnNext.addActionListener(e -> {
            jornadaActual = (jornadaActual == jornadas.size()) ? 1 : jornadaActual + 1;
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
            public void actionPerformed(ActionEvent e) { btnPrev.doClick(); }
        });

        am.put("next", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) { btnNext.doClick(); }
        });

        am.put("cerrar", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) { dispose(); }
        });

        actualizarVista();
    }

    private void actualizarVista() {
        if (jornadas == null || jornadas.isEmpty()) return;
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

    // Genera jornadas aleatorias y coherentes con el equipo elegido
    private List<MatchInfo> generarJornadas(List<Equipo> liga) {
        List<MatchInfo> lista = new ArrayList<>();

        // Copia modificable de la liga y quitamos nuestro propio equipo
        List<Equipo> equipos = new ArrayList<>(liga);
        equipos.removeIf(e -> e.getNombre().equals(equipo.getNombre()));

        Random rnd = new Random();

        // 38 jornadas
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

    // Convierte la lista de MatchInfo a una lista de Object[] para guardar en GameSession
    private List<Object[]> convertirAListaSimple(List<MatchInfo> jornadas) {
        List<Object[]> lista = new ArrayList<>();
        for (MatchInfo m : jornadas) {
            lista.add(new Object[]{
                    m.jornada,
                    m.rival,
                    m.estadio,
                    m.hora,
                    m.posicion,
                    m.local
            });
        }
        return lista;
    }

    // Reconstruye MatchInfo desde la lista guardada en GameSession
    private List<MatchInfo> reconstruirDesdeListaSimple(List<Object[]> datos) {
        List<MatchInfo> lista = new ArrayList<>();
        for (Object[] o : datos) {
            int j = (Integer) o[0];
            String rival = (String) o[1];
            String estadio = (String) o[2];
            String hora = (String) o[3];
            int pos = (Integer) o[4];
            boolean local = (Boolean) o[5];
            lista.add(new MatchInfo(j, rival, estadio, hora, pos, local));
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
            this.jornada = j;
            this.rival = r;
            this.estadio = e;
            this.hora = h;
            this.posicion = p;
            this.local = l;
        }
    }
}
