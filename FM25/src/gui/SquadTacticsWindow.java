package gui;

import domain.Equipo;
import domain.Jugador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SquadTacticsWindow extends JFrame {

    public SquadTacticsWindow(JFrame parent, Equipo equipo) {
        super("Plantilla y Tácticas - " + equipo.getNombre());
        setSize(760, 520);
        setLocationRelativeTo(parent);
        init(equipo);
        setVisible(true);
    }

    private void init(Equipo equipo) {
        JPanel top = new JPanel(new BorderLayout(6,6));
        JLabel lbl = new JLabel("Formación actual: " + equipo.getFormacion());
        top.add(lbl, BorderLayout.NORTH);

        String[] formations = {"4-4-2","4-3-3","4-2-3-1","3-5-2","5-3-2","4-4-1-1"};
        JComboBox<String> cb = new JComboBox<>(formations);
        cb.setSelectedItem(equipo.getFormacion());
        top.add(cb, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        String[] cols = {"#", "Nombre", "Posición", "Edad", "Valoración"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        for (int i=0; i<equipo.getOnceTitular().size(); i++) {
            Jugador j = equipo.getOnceTitular().get(i);
            model.addRow(new Object[]{i+1, j.getNombre(), j.getPosicion(), j.getEdad(), j.getValoracion()});
        }
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnApply = new JButton("Aplicar formación");
        JButton btnClose = new JButton("Cerrar");
        south.add(btnApply);
        south.add(btnClose);
        add(south, BorderLayout.SOUTH);

        btnApply.addActionListener(e -> {
            String sel = (String) cb.getSelectedItem();
            equipo.setFormacion(sel);
            JOptionPane.showMessageDialog(this, "Formación aplicada: " + sel);
            lbl.setText("Formación actual: " + equipo.getFormacion());
        });

        btnClose.addActionListener(e -> dispose());
    }
}
