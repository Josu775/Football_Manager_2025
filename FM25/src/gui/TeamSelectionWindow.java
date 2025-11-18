package gui;

import domain.Equipo;
import domain.LeagueData;
import domain.GameSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamSelectionWindow extends JFrame {

    private JList<Equipo> list;
    private JLabel lblEscudo;
    private JLabel lblTituloEquipo;
    private JTextArea detalleText;

    private static final Map<String, String> ESCUDOS = new HashMap<>();
    static {
        ESCUDOS.put("Real Madrid", "realmadrid.png");
        ESCUDOS.put("FC Barcelona", "barcelona.png");
        ESCUDOS.put("Atlético de Madrid", "atlmadrid.png");
        ESCUDOS.put("Sevilla FC", "sevilla.png");
        ESCUDOS.put("Real Sociedad", "realsociedad.png");
        ESCUDOS.put("Villarreal CF", "villarreal.png");
        ESCUDOS.put("Real Betis", "betis.png");
        ESCUDOS.put("Athletic Club", "athletic.png");
        ESCUDOS.put("Valencia CF", "valencia.png");
        ESCUDOS.put("RCD Mallorca", "mallorca.png");
        ESCUDOS.put("CA Osasuna", "osasuna.png");
        ESCUDOS.put("RC Celta", "celta.png");
        ESCUDOS.put("Getafe CF", "getafe.png");
        ESCUDOS.put("Rayo Vallecano", "rayovallecano.png");
        ESCUDOS.put("Deportivo Alavés", "alaves.png");
        ESCUDOS.put("Elche CF", "elche.png");
        ESCUDOS.put("RCD Espanyol", "espanyol.png");
        ESCUDOS.put("Girona FC", "girona.png");
        ESCUDOS.put("Real Oviedo", "realoviedo.png");
        ESCUDOS.put("Levante UD", "levante.png");
    }

    public TeamSelectionWindow(JFrame parent) {
        setTitle("Elegir equipo - Nueva partida");
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        // ======= PANEL FONDO =======
        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(new Color(5, 10, 30)); 
        fondo.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(fondo);

        // ======= LISTA IZQUIERDA (AZUL OSCURO ) =======
        DefaultListModel<Equipo> model = new DefaultListModel<>();
        for (Equipo e : LeagueData.getLaLiga20()) model.addElement(e);

        list = new JList<>(model);
        list.setFont(new Font("Arial", Font.BOLD, 16));
        list.setBackground(new Color(10, 20, 50));
        list.setForeground(Color.WHITE);
        list.setSelectionBackground(new Color(50,70,140));
        list.setSelectionForeground(Color.WHITE);

        // AUMENTAR ALTURA DE CADA FILA
        list.setCellRenderer(new HighRowRenderer());

        JScrollPane scrollList = new JScrollPane(list);
        scrollList.setPreferredSize(new Dimension(260, 500));
        scrollList.setBorder(BorderFactory.createLineBorder(new Color(70, 80, 120), 2));
        fondo.add(scrollList, BorderLayout.WEST);

        // ======= PANEL DERECHO OSCURO =======
        JPanel right = new JPanel(new BorderLayout(10,10));
        right.setBackground(new Color(8, 16, 45));
        right.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 255), 2),
                BorderFactory.createEmptyBorder(15,15,15,15)
        ));
        fondo.add(right, BorderLayout.CENTER);

        // TÍTULO
        lblTituloEquipo = new JLabel("", SwingConstants.CENTER);
        lblTituloEquipo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTituloEquipo.setForeground(Color.WHITE);
        right.add(lblTituloEquipo, BorderLayout.NORTH);

        // ESCUDO
        lblEscudo = new JLabel("", SwingConstants.CENTER);
        right.add(lblEscudo, BorderLayout.CENTER);

        // TEXTO
        detalleText = new JTextArea();
        detalleText.setEditable(false);
        detalleText.setBackground(new Color(8, 16, 45));
        detalleText.setForeground(Color.WHITE);
        detalleText.setFont(new Font("Arial", Font.PLAIN, 15));
        detalleText.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JScrollPane scrollDetalle = new JScrollPane(detalleText);
        scrollDetalle.setBorder(null);
        scrollDetalle.setPreferredSize(new Dimension(500, 250));
        right.add(scrollDetalle, BorderLayout.SOUTH);

        // ======= BOTONES =======
        JPanel south = new JPanel();
        JButton btnElegir = new JButton("Elegir equipo");
        JButton btnAtras = new JButton("Atrás");
        south.add(btnElegir);
        south.add(btnAtras);
        fondo.add(south, BorderLayout.SOUTH);

        // ======= EVENTOS =======
        list.addListSelectionListener(e -> updateDetails());

        btnElegir.addActionListener(e -> {
            Equipo sel = list.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un equipo.");
                return;
            }
            new MainGameWindow(this, new GameSession(sel));
            dispose();
        });

        btnAtras.addActionListener(e -> {
            dispose();
            new WelcomeWindow().setVisible(true);
        });
    }

    private void updateDetails() {
        Equipo sel = list.getSelectedValue();
        if (sel == null) return;

        lblTituloEquipo.setText(sel.getNombre());

        // ESCUDO
        String file = ESCUDOS.get(sel.getNombre());
        if (file != null) {
            ImageIcon img = new ImageIcon("resources/images/escudos/" + file);
            Image scaled = img.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            lblEscudo.setIcon(new ImageIcon(scaled));
        }

        // TEXTO DETALLE
        StringBuilder sb = new StringBuilder();
        sb.append("Ciudad: ").append(sel.getCiudad()).append("\n");
        sb.append("Estadio: ").append(sel.getEstadio()).append("\n");
        sb.append("Formación: ").append(sel.getFormacion()).append("\n");
        sb.append("Valoración: ").append(sel.getValoracion()).append(" / 5.0\n");
        sb.append("Presupuesto: ").append(LeagueData.formatMoney(sel.getBudget())).append("\n\n");

        sb.append("Once titular:\n");
        int i = 1;
        for (var j : sel.getOnceTitular()) {
            sb.append(i++).append(". ").append(j.getNombre())
                    .append(" - ").append(j.getPosicion())
                    .append(" (").append(j.getEdad()).append(" años) ")
                    .append(j.getValoracion()).append("/99\n");
        }

        detalleText.setText(sb.toString());
    }

    // ===== RENDERER PARA AUMENTAR ALTURA =====
    class HighRowRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            label.setPreferredSize(new Dimension(label.getWidth(), 30)); // ← ALTURA DE FILA
            label.setOpaque(true);

            if (isSelected) {
                label.setBackground(new Color(50,70,140));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(new Color(10,20,50));
                label.setForeground(Color.WHITE);
            }

            return label;
        }
    }
}
