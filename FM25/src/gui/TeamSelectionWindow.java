package gui;

import domain.Equipo;
import domain.LeagueData;
import domain.GameSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TeamSelectionWindow extends JFrame {

    private JList<Equipo> list;

    public TeamSelectionWindow(JFrame parent) {
        setTitle("Elegir equipo - Nueva partida");
        setSize(700, 420);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        List<Equipo> equipos = LeagueData.getLaLiga20();

        DefaultListModel<Equipo> model = new DefaultListModel<>();
        for (Equipo e : equipos) model.addElement(e);

        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel detail = new JPanel(new BorderLayout(5,5));
        JTextArea detalleText = new JTextArea();
        detalleText.setEditable(false);
        detalleText.setLineWrap(true);
        detalleText.setWrapStyleWord(true);
        detail.add(new JScrollPane(detalleText), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(list), detail);
        split.setDividerLocation(250);

        JPanel south = new JPanel();
        JButton btnElegir = new JButton("Elegir equipo");
        JButton btnAtras = new JButton("Atrás");
        south.add(btnElegir);
        south.add(btnAtras);

        add(split, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        list.addListSelectionListener(e -> {
            Equipo sel = list.getSelectedValue();
            if (sel != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Equipo: ").append(sel.getNombre()).append("\n");
                sb.append("Ciudad: ").append(sel.getCiudad()).append("\n");
                sb.append("Estadio: ").append(sel.getEstadio()).append("\n");
                sb.append("Formación: ").append(sel.getFormacion()).append("\n");
                sb.append("Valoración: ").append(String.format("%.1f / 5.0", sel.getValoracion())).append("\n");
                sb.append("Presupuesto: ").append(LeagueData.formatMoney(sel.getBudget())).append("\n\n");
                sb.append("Once titular:\n");
                int i=1;
                for (var j : sel.getOnceTitular()) {
                    sb.append(i++).append(". ").append(j.getNombre()).append(" - ").append(j.getPosicion())
                            .append(" (").append(j.getEdad()).append(" años) ").append(j.getValoracion()).append(" / 99").append("\n");
                }
                detalleText.setText(sb.toString());
            } else detalleText.setText("");
        });

        btnAtras.addActionListener(e -> {
            dispose();
            new WelcomeWindow().setVisible(true);
        });

        btnElegir.addActionListener(e -> {
            Equipo seleccionado = list.getSelectedValue();
            if (seleccionado == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un equipo.");
                return;
            }
            GameSession session = new GameSession(seleccionado);
            new MainGameWindow(this, session);
            dispose();
        });
    }
}
