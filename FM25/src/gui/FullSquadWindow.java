package gui;

import domain.Equipo;
import domain.Jugador;
import domain.LeagueData;

import javax.swing.*;
import java.awt.*;

public class FullSquadWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    public FullSquadWindow(JFrame parent, Equipo equipo) {
        super("Plantilla completa - " + equipo.getNombre());
        setSize(400, 500);
        setLocationRelativeTo(parent);
        init(equipo);
        setVisible(true);
    }

    private void init(Equipo equipo) {

        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.setBackground(new Color(10,20,50));
        txt.setForeground(Color.WHITE);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        StringBuilder sb = new StringBuilder();

        int i = 1;
        for (Jugador j : equipo.getOnceTitular()) {

            int media = LeagueData.toMedia(j.getValoracion());

            sb.append(i++)
              .append(". ")
              .append(j.getNombre())
              .append(" – ")
              .append(j.getPosicion())
              .append(" – Media: ")
              .append(media)
              .append("\n");
        }

        txt.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(
                txt,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("Cerrar");
        btnClose.addActionListener(e -> dispose());

        JPanel south = new JPanel();
        south.add(btnClose);

        add(south, BorderLayout.SOUTH);
    }
}
