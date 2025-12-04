package gui;

import domain.Equipo;
import domain.GameSession;
import domain.LeagueCalendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CalendarWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Equipo equipo;
    private final GameSession session;
    private final List<Equipo> liga;

    private List<List<LeagueCalendar.Match>> calendario;
    private int jornadaActual = 1;
    private JLabel lblInfo;

    public CalendarWindow(JFrame parent, Equipo equipo, List<Equipo> liga, GameSession session) {
        super("Calendario - " + equipo.getNombre());
        this.equipo = equipo;
        this.liga = liga;
        this.session = session;

        // Reutilizar o generar calendario
        calendario = session.getCalendario();
        if (calendario == null || calendario.isEmpty()) {
            calendario = LeagueCalendar.generarCalendario(liga);
            session.setCalendario(calendario);
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
        add(lblInfo, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton btnPrev = new JButton("← Jornada anterior");
        JButton btnNext = new JButton("Siguiente jornada →");
        botones.add(btnPrev);
        botones.add(btnNext);
        add(botones, BorderLayout.SOUTH);

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
        if (calendario == null || calendario.isEmpty()) return;

        List<LeagueCalendar.Match> jornada = calendario.get(jornadaActual - 1);

        LeagueCalendar.Match miPartido = null;
        for (LeagueCalendar.Match m : jornada) {
            if (m.local().equals(equipo.getNombre()) || m.visitante().equals(equipo.getNombre())) {
                miPartido = m;
                break;
            }
        }

        if (miPartido == null) {
            lblInfo.setText(
                    "<html><div style='text-align:center;'>" +
                    "<h2>Jornada " + jornadaActual + "</h2>" +
                    "Tu equipo descansa esta jornada." +
                    "</div></html>"
            );
            return;
        }

        boolean esLocal = miPartido.local().equals(equipo.getNombre());
        String rival = esLocal ? miPartido.visitante() : miPartido.local();

        lblInfo.setText(
                "<html><div style='text-align:center;'>" +
                "<h2>Jornada " + jornadaActual + "</h2>" +
                "<b>" + miPartido.local() + "</b> vs <b>" + miPartido.visitante() + "</b><br><br>" +
                "<b>Tu rival:</b> " + rival + "<br>" +
                "<b>Condición:</b> " + (esLocal ? "Local" : "Visitante") +
                "</div></html>"
        );
    }
}
