package gui;

import domain.Equipo;
import domain.Jugador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MarketWindow: quita los números de los nombres, formato de dinero correcto y botones funcionales.
 */
public class MarketWindow extends JFrame {

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
        String[] cols = {"Nombre", "Club origen", "Posición", "Edad", "Valoración", "Precio", "Acción"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 6; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel lblBudget = new JLabel("Presupuesto: " + formatMoney(targetTeam.getBudget()));
        lblBudget.setBorder(BorderFactory.createEmptyBorder(4,6,4,6));
        add(lblBudget, BorderLayout.NORTH);

        generateManyOffers(targetTeam.getValoracion(), 120);

        for (Offer of : offers) {
            model.addRow(new Object[]{
                    of.jugador.getNombre(),
                    of.clubOrigen,
                    of.jugador.getPosicion(),
                    of.jugador.getEdad(),
                    String.format("%.1f", of.jugador.getValoracion()),
                    formatMoney(of.precio),
                    "Fichar"
            });
        }

        TableColumn actionCol = table.getColumnModel().getColumn(6);
        actionCol.setCellRenderer(new ButtonRenderer());
        actionCol.setCellEditor(new ButtonEditor(table, model, offers, targetTeam, lblBudget));

        JPanel south = new JPanel();
        JButton btnClose = new JButton("Cerrar");
        south.add(btnClose);
        add(south, BorderLayout.SOUTH);

        btnClose.addActionListener(e -> dispose());
    }

    private static String formatMoney(double amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB€", amount / 1_000_000_000.0).replace('.', ',');
        if (amount >= 1_000_000) {
            double v = amount / 1_000_000.0;
            return (Math.abs(v - Math.round(v)) < 0.05)
                    ? String.format("%.0fM€", v)
                    : String.format("%.1fM€", v).replace('.', ',');
        }
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
        String[] foreignClubs = {
                "Manchester Utd", "Juventus", "Bayern", "PSG", "Chelsea", "Ajax",
                "Porto", "Benfica", "Inter", "AC Milan", "Dortmund", "RB Leipzig",
                "Atalanta", "Leeds", "Monaco", "Sporting CP"
        };
        String[] pos = {"POR","DEF","MID","ATT"};
        String[] laLiga = {
                "Real Madrid","FC Barcelona","Atlético de Madrid","Sevilla FC","Real Sociedad","Villarreal CF",
                "Real Betis","Athletic Club","Valencia CF","RCD Mallorca","CA Osasuna","RC Celta","Getafe CF",
                "Granada CF","Rayo Vallecano","Deportivo Alavés","Cádiz CF","Elche CF","RCD Espanyol","Girona FC"
        };

        for (int i = 0; i < n; i++) {
            String name = names[RNG.nextInt(names.length)]; 
            String origen = (RNG.nextDouble() < 0.6)
                    ? laLiga[RNG.nextInt(laLiga.length)]
                    : foreignClubs[RNG.nextInt(foreignClubs.length)];
            String position = pos[RNG.nextInt(pos.length)];
            int age = 18 + RNG.nextInt(16);
            double val = Math.max(1.5, Math.min(5.0, teamRating + (RNG.nextDouble() * 1.4 - 0.7)));
            val = Math.round(val * 10.0) / 10.0;
            double base = val * 2_000_000;
            double factor = (age < 22 ? 1.6 : 1.0) * (position.equals("POR") ? 0.9 : 1.0);
            double precio = Math.round((base * factor + RNG.nextInt(15_000_000)) / 1000.0) * 1000.0;
            offers.add(new Offer(new Jugador(name, position, age, val), origen, precio));
        }
    }

    private static class Offer {
        Jugador jugador;
        String clubOrigen;
        double precio;
        Offer(Jugador j, String origen, double p) { this.jugador = j; this.clubOrigen = origen; this.precio = p; }
    }

    private static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); setBackground(new Color(230, 240, 255)); }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setForeground(Color.BLACK);
            return this;
        }
    }

    private static class ButtonEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor, ActionListener {
        private final JButton button = new JButton();
        private final JTable table;
        private final DefaultTableModel model;
        private final List<Offer> offers;
        private final Equipo targetTeam;
        private final JLabel lblBudget;
        private int editingRow = -1;

        ButtonEditor(JTable table, DefaultTableModel model, List<Offer> offers, Equipo targetTeam, JLabel lblBudget) {
            this.table = table;
            this.model = model;
            this.offers = offers;
            this.targetTeam = targetTeam;
            this.lblBudget = lblBudget;
            button.addActionListener(this);
            button.setBackground(new Color(230, 240, 255));
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editingRow = table.convertRowIndexToModel(row);
            button.setText((value == null) ? "" : value.toString());
            return button;
        }

        @Override public Object getCellEditorValue() { return button.getText(); }

        @Override public void actionPerformed(ActionEvent e) {
            if (editingRow < 0 || editingRow >= offers.size()) { fireEditingStopped(); return; }
            Offer of = offers.get(editingRow);
            double precio = of.precio;
            if (precio > targetTeam.getBudget()) {
                JOptionPane.showMessageDialog(table, "No hay presupuesto suficiente para fichar a " + of.jugador.getNombre());
                fireEditingStopped();
                return;
            }
            targetTeam.setBudget(targetTeam.getBudget() - precio);
            targetTeam.getOnceTitular().add(of.jugador);
            JOptionPane.showMessageDialog(table, "Fichado: " + of.jugador.getNombre() + "\nPrecio: " + formatMoney(precio));
            if (lblBudget != null) lblBudget.setText("Presupuesto: " + formatMoney(targetTeam.getBudget()));
            offers.remove(editingRow);
            model.removeRow(editingRow);
            fireEditingStopped();
        }
    }
}
