package gui;

import domain.Equipo;
import domain.TeamStats;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ClassificationWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel model;
    private List<Equipo> equipos;
    private String nombreEquipoJugador;

    // Mapa de escudos (igual que en TeamSelectionWindow)
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
        ESCUDOS.put("Real Club Celta de Vigo", "celta.png");
        ESCUDOS.put("Getafe CF", "getafe.png");
        ESCUDOS.put("Rayo Vallecano", "rayovallecano.png");
        ESCUDOS.put("Deportivo Alavés", "alaves.png");
        ESCUDOS.put("Elche CF", "elche.png");
        ESCUDOS.put("RCD Espanyol", "espanyol.png");
        ESCUDOS.put("Girona FC", "girona.png");
        ESCUDOS.put("Real Oviedo", "realoviedo.png");
        ESCUDOS.put("Levante UD", "levante.png");
    }

    // Construye la clasificación con : 
    // la lista de equipos
    // el equipo que entrena el jugador ( para resaltarlo)
    
    public ClassificationWindow(JFrame parent, Equipo equipoJugador, List<Equipo> equiposLiga) {
        super("Clasificación");
        this.nombreEquipoJugador = equipoJugador.getNombre();
        this.equipos = new ArrayList<>(equiposLiga);

        setSize(950, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        cargarDatos();
        setVisible(true);
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(10, 16, 36));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(main);

        JLabel title = new JLabel("Clasificación de la Liga", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(230, 240, 255));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        main.add(title, BorderLayout.NORTH);

        String[] cols = {"Pos", "", "Equipo", "Pts", "V", "E", "D", "GF", "GC", "DG"};

        model = new DefaultTableModel(cols, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex >= 3) return Integer.class;
                return String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(20, 28, 60));
        table.setSelectionBackground(new Color(100, 120, 210));
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);


        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(70, 30, 120));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.setDefaultRenderer(Object.class,
                new ClassificationRenderer(nombreEquipoJugador));

        // Columna del escudo con renderer especial
        table.getColumnModel().getColumn(1).setCellRenderer(
                new BadgeRenderer()
        );
        table.getColumnModel().getColumn(1).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(20, 28, 60));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(90, 110, 200)));
        main.add(scroll, BorderLayout.CENTER);

        JButton back = new JButton("Volver");
        back.setPreferredSize(new Dimension(110, 36));
        back.setFont(new Font("Segoe UI", Font.BOLD, 14));
        back.setBackground(new Color(90, 140, 230));
        back.setForeground(Color.WHITE);
        back.addActionListener(e -> dispose());

        JPanel south = new JPanel();
        south.setBackground(new Color(10, 16, 36));
        south.add(back);
        main.add(south, BorderLayout.SOUTH);
    }
    
    //Rellana la tabla en base a el Team Stats de cada equipo.   

    
    private void cargarDatos() {
        model.setRowCount(0);

        equipos.sort((a, b) -> {
            TeamStats sa = a.getStats();
            TeamStats sb = b.getStats();

            // Orden: puntos desc, DG desc, GF desc, nombre
            int cmp = Integer.compare(sb.getPuntos(), sa.getPuntos());
            if (cmp != 0) return cmp;
            cmp = Integer.compare(sb.getDg(), sa.getDg());
            if (cmp != 0) return cmp;
            cmp = Integer.compare(sb.getGf(), sa.getGf());
            if (cmp != 0) return cmp;
            return a.getNombre().compareToIgnoreCase(b.getNombre());
        });

        int pos = 1;
        for (Equipo e : equipos) {
            TeamStats s = e.getStats();
            Object[] row = {
            	    pos++,
            	    e.getNombre(),
            	    e.getNombre(),
            	    s.getPuntos(),
            	    s.getVictorias(),
            	    s.getEmpates(),
            	    s.getDerrotas(),
            	    s.getGf(),
            	    s.getGc(),
            	    s.getDg()
            	};

            model.addRow(row);
        }
    }
    
    //Renderer de filas : alterna colores y resalta el equipo seleccionado por el jugador en azul clarito.

    
    private static class ClassificationRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final String equipoJugador;

        public ClassificationRenderer(String equipoJugador) {
            this.equipoJugador = equipoJugador;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String nombreEquipo = (String) table.getValueAt(row, 2);

            if (isSelected) {
                c.setBackground(new Color(110, 140, 230));
                c.setForeground(Color.WHITE);
            } else if (nombreEquipo.equals(equipoJugador)) {
                c.setBackground(new Color(60, 190, 230)); // azul clarito para tu equipo
                c.setForeground(Color.BLACK);
            } else {
                if (row % 2 == 0) {
                    c.setBackground(new Color(24, 32, 68));
                } else {
                    c.setBackground(new Color(30, 40, 80));
                }
                c.setForeground(Color.WHITE);
            }

            if (column == 0 || (column >= 3 && column <= 6)) {
                setHorizontalAlignment(CENTER);
            } else {
                setHorizontalAlignment(LEFT);
            }

            return c;
        }
    }

    //Renderer para la columna del escudo + el nombre ( columna 1) 
    
    private static class BadgeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            String equipo = (String) value;
            JLabel label = new JLabel();
            label.setOpaque(false);
            label.setHorizontalAlignment(CENTER);

            //  Buscar archivo del escudo
            String file = ESCUDOS.get(equipo);
            if (file != null) {

                //  Tamaño dinámico según el alto de la fila
                int size = table.getRowHeight(row) - 6;
                if (size < 16) size = 16;   // tamaño mínimo

                ImageIcon icon = new ImageIcon("resources/images/escudos/" + file);

                //  Evitar que falle si el archivo no esta
                if (icon.getIconWidth() > 0) {
                    Image scaled = icon.getImage().getScaledInstance(
                            size, size, Image.SCALE_SMOOTH
                    );
                    label.setIcon(new ImageIcon(scaled));
                }
            }

            return label;
        }
    }

}
