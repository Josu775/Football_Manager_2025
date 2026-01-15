package gui;

import db.DataManager;
import domain.Equipo;
import domain.Jugador;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarketWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Random RNG = new Random();
    private final List<Offer> offers = new ArrayList<>();

    public MarketWindow(JFrame parent, Equipo targetTeam) {
        super("Mercado - " + targetTeam.getNombre());
        setSize(950, 580);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        init(targetTeam);
        setVisible(true);
    }

    private void init(Equipo targetTeam) {

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(10, 16, 36));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(main);

        JLabel title = new JLabel("Mercado de fichajes", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(230, 240, 255));
        title.setBorder(BorderFactory.createEmptyBorder(6, 0, 8, 0));
        main.add(title, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(10, 16, 36));

        JLabel lblBudget = new JLabel("Presupuesto: " + formatMoney(targetTeam.getBudget()));
        lblBudget.setForeground(new Color(140, 230, 140));
        lblBudget.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBudget.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(240, 30));
        txtSearch.setToolTipText("Buscar jugador, posición o club...");
        txtSearch.setBackground(new Color(20, 28, 60));
        txtSearch.setForeground(Color.WHITE);
        txtSearch.setCaretColor(Color.WHITE);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 110, 200)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        topPanel.add(lblBudget, BorderLayout.WEST);
        topPanel.add(txtSearch, BorderLayout.EAST);

        main.add(topPanel, BorderLayout.SOUTH);

        String[] cols = {"Nombre", "Club origen", "Posición", "Edad", "Media", "Precio", "Acción"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return c == 6; }
        };

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(32);
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

        table.setDefaultRenderer(Object.class, new MarketRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(20, 28, 60));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(90, 110, 200)));
        main.add(scroll, BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            private void filtrar() {
                String texto = txtSearch.getText().trim().toLowerCase();
                if (texto.isEmpty()) {
                    sorter.setRowFilter(null);
                    return;
                }

                sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        for (int i = 0; i < entry.getValueCount(); i++) {
                            Object val = entry.getValue(i);
                            if (val != null && val.toString().toLowerCase().contains(texto)) return true;
                        }
                        return false;
                    }
                });
            }

            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        List<Object[]> desdeBD = DataManager.getMercadoDAO().cargarMercado();

        if (desdeBD.isEmpty()) {
            generateManyOffers(75, 120);

            for (Offer of : offers) {
                DataManager.getMercadoDAO().insertarJugadorMercado(
                        of.jugador, of.clubOrigen, of.precio
                );
            }

        } else {
            for (Object[] o : desdeBD) {
                Jugador j = new Jugador(
                        (String) o[1],
                        (String) o[2],
                        (int) o[3],
                        (double) o[4]
                );
                offers.add(new Offer(j, (String) o[5], (double) o[6]));
            }
        }

        for (Offer of : offers) {
            model.addRow(new Object[]{
                    of.jugador.getNombre(),
                    of.clubOrigen,
                    of.jugador.getPosicion(),
                    of.jugador.getEdad(),
                    (int) Math.round(of.jugador.getValoracion()),
                    formatMoney(of.precio),
                    "Fichar"
            });
        }

        TableColumn actionCol = table.getColumnModel().getColumn(6);
        actionCol.setCellRenderer(new ButtonRenderer());
        actionCol.setCellEditor(new ButtonEditor(table, model, offers, targetTeam, lblBudget));
        actionCol.setMaxWidth(120);

        JButton btnClose = new JButton("Cerrar");
        btnClose.setPreferredSize(new Dimension(110, 36));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setBackground(new Color(90, 140, 230));
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());

        JPanel south = new JPanel();
        south.setBackground(new Color(10, 16, 36));
        south.add(btnClose);
        main.add(south, BorderLayout.PAGE_END);
    }

    private static String formatMoney(double amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB€", amount / 1_000_000_000.0).replace('.', ',');
        if (amount >= 1_000_000) return String.format("%.1fM€", amount / 1_000_000.0).replace('.', ',');
        if (amount >= 1000) return String.format("%.0fK€", amount / 1000.0);
        return String.format("%.0f€", amount);
    }

    private void generateManyOffers(double teamRating, int n) {

        String[] names = {
                "Lucas Pérez","Diego López","Sergio Torres","Álvaro Ruiz","Pablo Martín","Rubén Díaz",
                "Manu García","Hugo Moreno","Iván Ramos","Javier Ortega","Adrián Campos","Marco Silva",
                "Nico Fernández","Bruno Costa","Felipe Alves","Theo Müller","Luca Rossi","Jean Dupont",
                "Sven Hansen","Mats Johansson","Carlos Silva","André Gomes","Mikhail Petrov","Omar Haddad",
                "Ethan Carter","Luis Navarro","Álex Blanco","Tomás Álvarez","Rui Pereira","Mateo Iglesias"
        };

        String[] pos = {"POR","DEF","MID","ATT"};

        String[] clubs = {
                "Real Madrid","FC Barcelona","Atlético de Madrid","Sevilla FC","Villarreal CF","Real Sociedad",
                "Getafe CF","Rayo Vallecano","Real Betis","Valencia CF","RCD Espanyol","Granada CF",
                "Borussia Dortmund","PSG","Bayern","Chelsea","Juventus","Inter","Ajax","Benfica"
        };

        for (int i = 0; i < n; i++) {

            String name = names[RNG.nextInt(names.length)];
            String origen = clubs[RNG.nextInt(clubs.length)];
            String position = pos[RNG.nextInt(pos.length)];
            int age = 18 + RNG.nextInt(16);

            double val = 60 + RNG.nextInt(31);

            double base;

            if (val < 65)       base = 1_500_000;
            else if (val < 70)  base = 4_000_000;
            else if (val < 75)  base = 8_000_000;
            else if (val < 80)  base = 15_000_000;
            else if (val < 85)  base = 30_000_000;
            else                base = 60_000_000;

            double ageFactor;
            if (age <= 21)      ageFactor = 1.6;
            else if (age <= 24) ageFactor = 1.3;
            else if (age <= 27) ageFactor = 1.1;
            else if (age <= 30) ageFactor = 0.9;
            else                ageFactor = 0.7;

            double posFactor = position.equals("POR") ? 0.75 : 1.0;

            double randomFactor = 0.85 + RNG.nextDouble() * 0.3;

            double precio = base * ageFactor * posFactor * randomFactor;

            precio = Math.round(precio / 100_000.0) * 100_000.0;


            offers.add(new Offer(new Jugador(name, position, age, val), origen, precio));
        }
    }

    private static class Offer {
        Jugador jugador;
        String clubOrigen;
        double precio;
        Offer(Jugador j, String origen, double p) {
            this.jugador = j;
            this.clubOrigen = origen;
            this.precio = p;
        }
    }

    private static class MarketRenderer extends DefaultTableCellRenderer {
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

            if (column >= 3 && column <= 5) setHorizontalAlignment(CENTER);
            else setHorizontalAlignment(LEFT);

            return c;
        }
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(90, 140, 230));
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {

            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    private static class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private static final long serialVersionUID = 1L;

        private final JButton button = new JButton();
        private final JTable table;
        private final DefaultTableModel model;
        private final List<Offer> offers;
        private final Equipo targetTeam;
        private final JLabel lblBudget;
        private int editingRow = -1;

        ButtonEditor(JTable table, DefaultTableModel model, List<Offer> offers,
                     Equipo targetTeam, JLabel lblBudget) {

            this.table = table;
            this.model = model;
            this.offers = offers;
            this.targetTeam = targetTeam;
            this.lblBudget = lblBudget;

            button.setBackground(new Color(90, 140, 230));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 13));
            button.setFocusPainted(false);
            button.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            editingRow = table.convertRowIndexToModel(row);
            button.setText(value == null ? "" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() { return button.getText(); }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (editingRow < 0 || editingRow >= offers.size()) { fireEditingStopped(); return; }

            Offer of = offers.get(editingRow);
            double precio = of.precio;

            if (precio > targetTeam.getBudget()) {
                JOptionPane.showMessageDialog(table, "No hay presupuesto suficiente.");
                fireEditingStopped();
                return;
            }

            targetTeam.setBudget(targetTeam.getBudget() - precio);
            of.jugador.setEquipo(targetTeam.getNombre());
            targetTeam.getPlantilla().add(of.jugador);

            try {
                DataManager.getJugadorDAO().insertarJugador(of.jugador);
                DataManager.getEquipoDAO().actualizarPresupuesto(targetTeam.getNombre(), targetTeam.getBudget());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(table,
                    "Fichado: " + of.jugador.getNombre() + "\nPrecio: " + formatMoney(precio));

            lblBudget.setText("Presupuesto: " + formatMoney(targetTeam.getBudget()));

            model.removeRow(editingRow);
            offers.remove(editingRow);

            fireEditingStopped();
        }
    }
}
