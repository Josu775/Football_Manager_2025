package gui;

import domain.Equipo;
import domain.LeagueData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Comparator;
import java.util.List;

public class ClassificationWindow extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override public boolean isCellEditable(int r, int c) { return false; }
        };
        int pos = 1;
        for (Equipo e : equipos) {
            model.addRow(new Object[]{pos++, e.getNombre(), 0, 0, 0});
        }

        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        Runnable showSelected = () -> {
            int row = table.getSelectedRow();
            if (row >= 0 && row < equipos.size()) {
                Equipo eq = equipos.get(row);
                int posicion = (int) table.getValueAt(row, 0);
                int puntos = (int) table.getValueAt(row, 2);
                int gf = (int) table.getValueAt(row, 3);
                int pj = (int) table.getValueAt(row, 4);
                JOptionPane.showMessageDialog(
                        this,
                        "Equipo: " + eq.getNombre() +
                                "\nPosición: " + posicion +
                                "\nValoración: " + String.format("%.1f / 5.0", eq.getValoracion()) +
                                "\nCiudad: " + eq.getCiudad() +
                                "\nEstadio: " + eq.getEstadio() +
                                "\n\nPuntos: " + puntos +
                                "\nGoles a favor: " + gf +
                                "\nPartidos jugados: " + pj,
                        "Detalle del equipo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        };

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showSelected.run();
                }
            }
        });

        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "detalle");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");

        am.put("detalle", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                showSelected.run();
            }
        });
        am.put("cerrar", new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
