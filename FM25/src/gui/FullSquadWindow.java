package gui;

import domain.Equipo;
import domain.Jugador;

import javax.swing.*;
import java.awt.*;

public class FullSquadWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    public FullSquadWindow(JFrame parent, Equipo equipo) {
        super("Plantilla completa - " + equipo.getNombre());
        setSize(520, 560);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        init(equipo);
        setVisible(true);
    }

    private void init(Equipo equipo) {

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(10, 16, 36));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(main);

        JLabel title = new JLabel("Plantilla completa", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(230, 240, 255));
        title.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));
        main.add(title, BorderLayout.NORTH);

        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.setBackground(new Color(20, 28, 60));
        txt.setForeground(Color.WHITE);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);

        StringBuilder sb = new StringBuilder();
        int i = 1;

        for (Jugador j : equipo.getPlantilla()) {
            int media = (int) Math.round(j.getValoracion());
            sb.append(String.format("%02d. %s  —  %s  —  %d años  —  Media: %d%n",
                    i++, j.getNombre(), j.getPosicion(), j.getEdad(), media));
        }

        txt.setText(sb.toString());
        txt.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(
                txt,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.getViewport().setBackground(new Color(20, 28, 60));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(90, 110, 200)));
        main.add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("Cerrar");
        btnClose.setPreferredSize(new Dimension(110, 36));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setBackground(new Color(90, 140, 230));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());

        JPanel south = new JPanel();
        south.setBackground(new Color(10, 16, 36));
        south.add(btnClose);
        main.add(south, BorderLayout.SOUTH);
    }
}
