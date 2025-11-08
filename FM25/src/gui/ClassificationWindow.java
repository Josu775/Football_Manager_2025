package gui;

import domain.Equipo;
import domain.LeagueData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class ClassificationWindow extends JFrame {

    public ClassificationWindow(JFrame parent) {
        super("Clasificación - Liga (prototipo)");
        setSize(600, 500);
        setLocationRelativeTo(parent);
        init();
        setVisible(true);
    }

    private void init() {
        List<Equipo> equipos = LeagueData.getLaLiga20();

        
        equipos.sort(Comparator.comparingDouble(Equipo::getValoracion).reversed());

        String[] cols = {"Pos", "Equipo", "Puntos", "GF", "PJ"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        int pos = 1;
        for (Equipo e : equipos) {
            model.addRow(new Object[]{pos++, e.getNombre(), 0, 0, 0}); 
        }

        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
