package gui;

import db.DataManager;
import domain.Equipo;
import domain.Jugador;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class SquadTacticsWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    public SquadTacticsWindow(JFrame parent, Equipo equipo) {
        super("Plantilla y Tácticas - " + equipo.getNombre());
        setSize(820, 560);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        init(equipo);
        setVisible(true);
    }

    private void init(Equipo equipo) {

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(10, 16, 36));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(main);

        JLabel title = new JLabel("Plantilla y Tácticas", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(230, 240, 255));
        title.setBorder(BorderFactory.createEmptyBorder(6, 0, 10, 0));
        main.add(title, BorderLayout.NORTH);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(new Color(10, 16, 36));

        JLabel lbl = new JLabel("Formación actual: " + equipo.getFormacion());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(230, 240, 255));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        top.add(lbl, BorderLayout.WEST);

        String[] formations = {"4-4-2","4-3-3","4-2-3-1","3-5-2","5-3-2","4-4-1-1"};
        JComboBox<String> cb = new JComboBox<>(formations);
        cb.setSelectedItem(equipo.getFormacion());
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(new Color(20, 28, 60));
        cb.setForeground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(new Color(90, 110, 200)));
        top.add(cb, BorderLayout.EAST);

        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setBackground(new Color(10, 16, 36));
        topWrap.add(top, BorderLayout.CENTER);
        main.add(topWrap, BorderLayout.BEFORE_FIRST_LINE);

        String[] cols = {"#", "Nombre", "Posición", "Edad", "Media"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(20, 28, 60));
        table.setSelectionBackground(new Color(100, 120, 210));
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(70, 30, 120));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(110, 140, 230));
                    c.setForeground(Color.WHITE);
                } else {
                    if (row % 2 == 0) c.setBackground(new Color(24, 32, 68));
                    else c.setBackground(new Color(30, 40, 80));
                    c.setForeground(Color.WHITE);
                }

                if (column == 0 || column >= 3) setHorizontalAlignment(CENTER);
                else setHorizontalAlignment(LEFT);

                return c;
            }
        });

        for (int i = 0; i < equipo.getPlantilla().size(); i++) {
            Jugador j = equipo.getPlantilla().get(i);
            int media = (int) Math.round(j.getValoracion());

            model.addRow(new Object[]{
                    i + 1,
                    j.getNombre(),
                    j.getPosicion(),
                    j.getEdad(),
                    media
            });
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(20, 28, 60));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(90, 110, 200)));
        main.add(scroll, BorderLayout.CENTER);

        JButton btnApply = new JButton("Aplicar formación");
        btnApply.setPreferredSize(new Dimension(160, 36));
        btnApply.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnApply.setBackground(new Color(90, 140, 230));
        btnApply.setForeground(Color.WHITE);

        JButton btnClose = new JButton("Cerrar");
        btnClose.setPreferredSize(new Dimension(110, 36));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setBackground(new Color(90, 140, 230));
        btnClose.setForeground(Color.WHITE);

        JPanel south = new JPanel();
        south.setBackground(new Color(10, 16, 36));
        south.add(btnApply);
        south.add(Box.createHorizontalStrut(10));
        south.add(btnClose);
        main.add(south, BorderLayout.SOUTH);

        btnApply.addActionListener(e -> {
            String sel = (String) cb.getSelectedItem();
            equipo.setFormacion(sel);

            try {
                DataManager.getEquipoDAO().actualizarFormacion(equipo.getNombre(), sel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this, "Formación aplicada: " + sel);
            lbl.setText("Formación actual: " + sel);
        });

        btnClose.addActionListener(e -> dispose());

        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "aplicar");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");

        am.put("aplicar", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                btnApply.doClick();
            }
        });

        am.put("cerrar", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                btnClose.doClick();
            }
        });
    }
}
