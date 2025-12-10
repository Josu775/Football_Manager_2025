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
        setSize(900, 520);
        setLocationRelativeTo(parent);
        init(targetTeam);
        setVisible(true);
    }

    private void init(Equipo targetTeam) {

        // TABLA Y MODELO
        String[] cols = {"Nombre", "Club origen", "Posición", "Edad", "Media", "Precio", "Acción"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return c == 6;
            }
        };

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // PANEL SUPERIOR: PRESUPUESTO + BUSQUEDA
        JLabel lblBudget = new JLabel("Presupuesto: " + formatMoney(targetTeam.getBudget()));
        lblBudget.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JTextField txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(200, 28));
        txtSearch.setToolTipText("Buscar jugador, posición o club...");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(lblBudget, BorderLayout.WEST);
        topPanel.add(txtSearch, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // SORTER PARA ORDENAR + FILTRAR
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
                            if (val != null && val.toString().toLowerCase().contains(texto)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }

            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        // 1) CARGAR MERCADO DESDE BD
        List<Object[]> desdeBD = DataManager.getMercadoDAO().cargarMercado();

        if (desdeBD.isEmpty()) {
            // BD vacía → generar mercado nuevo
            generateManyOffers(75, 120);

            for (Offer of : offers) {
                DataManager.getMercadoDAO().insertarJugadorMercado(
                        of.jugador, of.clubOrigen, of.precio
                );
            }

        } else {
            for (Object[] o : desdeBD) {
                Jugador j = new Jugador(
                        (String) o[1],    // nombre
                        (String) o[2],    // posicion
                        (int) o[3],       // edad
                        (double) o[4]     // valoracion (60–90)
                );

                offers.add(new Offer(j, (String) o[5], (double) o[6]));
            }
        }

        // 2) MOSTRAR OFERTAS EN LA TABLA
        for (Offer of : offers) {
            model.addRow(new Object[]{
                    of.jugador.getNombre(),
                    of.clubOrigen,
                    of.jugador.getPosicion(),
                    of.jugador.getEdad(),
                    (int) Math.round(of.jugador.getValoracion()),  // media correcta
                    formatMoney(of.precio),
                    "Fichar"
            });
        }

        // BOTÓN "FICHAR"
        TableColumn actionCol = table.getColumnModel().getColumn(6);
        actionCol.setCellRenderer(new ButtonRenderer());
        actionCol.setCellEditor(new ButtonEditor(table, model, offers, targetTeam, lblBudget));

        // BOTÓN CERRAR ABAJO
        JPanel south = new JPanel();
        JButton btnClose = new JButton("Cerrar");
        south.add(btnClose);
        add(south, BorderLayout.SOUTH);

        btnClose.addActionListener(e -> dispose());
    }

    // FORMATEO DE DINERO
    private static String formatMoney(double amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB€", amount / 1_000_000_000.0).replace('.', ',');
        if (amount >= 1_000_000) return String.format("%.1fM€", amount / 1_000_000.0).replace('.', ',');
        if (amount >= 1000) return String.format("%.0fK€", amount / 1000.0);
        return String.format("%.0f€", amount);
    }

    // GENERAR JUGADORES ALEATORIOS REALISTAS
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

            // MEDIA REALISTA ENTRE 60 Y 90
            double val = 60 + RNG.nextInt(31);

            // PRECIO
            double base = val * 2_000_000;
            double factor = (age < 22 ? 1.6 : 1.0) * (position.equals("POR") ? 0.9 : 1.0);
            double precio = Math.round((base * factor + RNG.nextInt(15_000_000)) / 1000.0) * 1000.0;

            offers.add(new Offer(new Jugador(name, position, age, val), origen, precio));
        }
    }

    // CLASE OFFER
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

    // RENDER BOTÓN
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); setBackground(new Color(230, 240, 255)); }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            setText(value == null ? "" : value.toString());
            setForeground(Color.BLACK);
            return this;
        }
    }

    // EDITOR BOTÓN "FICHAR"
    private static class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

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

            button.setBackground(new Color(230, 240, 255));
            button.addActionListener(this);
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
            editingRow = table.convertRowIndexToModel(row);
            button.setText(value == null ? "" : value.toString());
            return button;
        }

        @Override public Object getCellEditorValue() { return button.getText(); }

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

            // FICHAR
            targetTeam.setBudget(targetTeam.getBudget() - precio);
            of.jugador.setEquipo(targetTeam.getNombre());
            targetTeam.getPlantilla().add(of.jugador);

            // BD
            try {
                DataManager.getJugadorDAO().insertarJugador(of.jugador);
                DataManager.getEquipoDAO().actualizarPresupuesto(targetTeam.getNombre(), targetTeam.getBudget());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // UI
            JOptionPane.showMessageDialog(table,
                    "Fichado: " + of.jugador.getNombre() + "\nPrecio: " + formatMoney(precio));

            lblBudget.setText("Presupuesto: " + formatMoney(targetTeam.getBudget()));

            model.removeRow(editingRow);
            offers.remove(editingRow);

            fireEditingStopped();
        }
    }
}
