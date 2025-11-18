package gui;

import domain.Equipo;
import domain.LeagueData;
import domain.GameSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class TeamSelectionWindow extends JFrame {

    private JList<Equipo> list;
    private JLabel lblEscudo;
    private JLabel lblTituloEquipo;
    private JTextArea detalleTextLeft;   // Información general
    private JTextArea detalleTextRight;  // Once titular

    private static final Map<String, String> ESCUDOS = new HashMap<>();

    static {
        ESCUDOS.put("Real Madrid", "realmadrid.png");
        ESCUDOS.put("FC Barcelona", "barcelona.png");
        ESCUDOS.put("Atletico de Madrid", "atlmadrid.png");
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

    private static final Map<String, String> HISTORIA = new HashMap<>();
    static {
        HISTORIA.put("Real Madrid", "Historia que tu hiciste, historia por hacer.");
        HISTORIA.put("FC Barcelona", "Més que un club.");
        HISTORIA.put("Atlético de Madrid", "Coraje y corazón.");
        HISTORIA.put("Sevilla FC", "Orgullo de sevillanía.");
        HISTORIA.put("Real Sociedad", "Txuri-urdin maitea, txuri-urdin aurrera.");
        HISTORIA.put("Villarreal CF", "Valentía grogueta.");
        HISTORIA.put("Real Betis", "Manquepierda.");
        HISTORIA.put("Athletic Club", "Euskal Herriaren erakusgarria.");
        HISTORIA.put("Valencia CF", "Amunt, sempre Amunt.");
        HISTORIA.put("RCD Mallorca", "Ca nostra, ca teva.");
        HISTORIA.put("CA Osasuna", "Osasuna nunca se rinde.");
        HISTORIA.put("RC Celta", "Afouteza, corazón, orgullo e tradición.");
        HISTORIA.put("Getafe CF", "Esto es fútbol,, papá.");
        HISTORIA.put("Rayo Vallecano", "La vida pirata la vida mejor.");
        HISTORIA.put("Deportivo Alavés", "Babazorro, alta la frente.");
        HISTORIA.put("Elche CF", "Valentía franjiverde.");
        HISTORIA.put("RCD Espanyol", "T'estimo, Espanyol.");
        HISTORIA.put("Girona FC", "Girona, cantem tots junts.");
        HISTORIA.put("Real Oviedo", "Me he cortado, y mi sangre sale azul.");
        HISTORIA.put("Levante UD", "Força granota.");
    }

    private static final Map<String, String> OBJETIVO = new HashMap<>();
    static {
        OBJETIVO.put("Real Madrid", "Ganar LaLiga");
        OBJETIVO.put("FC Barcelona", "Ganar LaLiga");
        OBJETIVO.put("Atlético de Madrid", "Clasificación UCL");
        OBJETIVO.put("Sevilla FC", "Clasificación europea");
        OBJETIVO.put("Real Sociedad", "Competir en Europa");
        OBJETIVO.put("Villarreal CF", "Puesto europeo");
        OBJETIVO.put("Real Betis", "Europa League");
        OBJETIVO.put("Athletic Club", "Luchar por Europa");
        OBJETIVO.put("Valencia CF", "Mitad alta");
        OBJETIVO.put("RCD Mallorca", "Salvarse");
        OBJETIVO.put("CA Osasuna", "Salvarse");
        OBJETIVO.put("RC Celta", "Salvarse");
        OBJETIVO.put("Getafe CF", "Evitar descenso");
        OBJETIVO.put("Rayo Vallecano", "Evitar descenso");
        OBJETIVO.put("Deportivo Alavés", "Permanencia");
        OBJETIVO.put("Elche CF", "Evitar descenso");
        OBJETIVO.put("RCD Espanyol", "Mitad de tabla");
        OBJETIVO.put("Girona FC", "Mitad alta");
        OBJETIVO.put("Real Oviedo", "Permanencia");
        OBJETIVO.put("Levante UD", "Permanencia");
    }

    private static final Map<String, String> REPUTACION = new HashMap<>();
    static {
        REPUTACION.put("Real Madrid", "Muy Alta");
        REPUTACION.put("FC Barcelona", "Muy Alta");
        REPUTACION.put("Atlético de Madrid", "Alta");
        REPUTACION.put("Sevilla FC", "Alta");
        REPUTACION.put("Real Sociedad", "Alta");
        REPUTACION.put("Villarreal CF", "Alta");
        REPUTACION.put("Athletic Club", "Alta");
        REPUTACION.put("Real Betis", "Media");
        REPUTACION.put("Valencia CF", "Media");
        REPUTACION.put("Girona FC", "Media");
        REPUTACION.put("RCD Mallorca", "Baja");
        REPUTACION.put("CA Osasuna", "Baja");
        REPUTACION.put("RC Celta", "Baja");
        REPUTACION.put("RCD Espanyol", "Baja");
        REPUTACION.put("Getafe CF", "Baja");
        REPUTACION.put("Rayo Vallecano", "Baja");
        REPUTACION.put("Deportivo Alavés", "Baja");
        REPUTACION.put("Elche CF", "Muy baja");
        REPUTACION.put("Real Oviedo", "Muy baja");
        REPUTACION.put("Levante UD", "Muy baja");
    }

    private static final String[] FORTALEZAS = {
            "Contraataque rápido", "Presión alta", "Posesión",
            "Juego directo", "Defensa sólida", "Creatividad ofensiva"
    };

    public TeamSelectionWindow(JFrame parent) {
        setTitle("Elegir equipo - Nueva partida");
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(new Color(5,10,30));
        fondo.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        add(fondo);

        // ===== LISTA =====
        DefaultListModel<Equipo> model = new DefaultListModel<>();
        for (Equipo e : LeagueData.getLaLiga20()) model.addElement(e);

        list = new JList<>(model);
        list.setFont(new Font("Arial", Font.BOLD, 16));
        list.setBackground(new Color(10,20,50));
        list.setForeground(Color.WHITE);
        list.setSelectionBackground(new Color(50,70,140));
        list.setSelectionForeground(Color.WHITE);
        list.setCellRenderer(new HighRowRenderer());

        JScrollPane scrollList = new JScrollPane(list);
        scrollList.setPreferredSize(new Dimension(260,500));
        fondo.add(scrollList, BorderLayout.WEST);

        // ===== PANEL DERECHO =====
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(new Color(8,16,45));
        right.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,255),2),
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

        // ===== PANEL DOBLE (INFO IZQ + ONCE DCHA) =====
        JPanel doble = new JPanel(new GridLayout(1,2,20,0));
        doble.setBackground(new Color(8,16,45));
        right.add(doble, BorderLayout.SOUTH);

        // INFO IZQUIERDA
        detalleTextLeft = new JTextArea();
        detalleTextLeft.setEditable(false);
        detalleTextLeft.setBackground(new Color(8,16,45));
        detalleTextLeft.setForeground(Color.WHITE);
        detalleTextLeft.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        detalleTextLeft.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JScrollPane scrollLeft = new JScrollPane(detalleTextLeft);
        scrollLeft.setBorder(null);
        doble.add(scrollLeft);

        // ONCE DERECHA
        detalleTextRight = new JTextArea();
        detalleTextRight.setEditable(false);
        detalleTextRight.setBackground(new Color(8,16,45));
        detalleTextRight.setForeground(Color.WHITE);
        detalleTextRight.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        detalleTextRight.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JScrollPane scrollRight = new JScrollPane(detalleTextRight);
        scrollRight.setBorder(null);
        doble.add(scrollRight);

        // BOTONES
        JPanel south = new JPanel();
        JButton btnElegir = new JButton("Elegir equipo");
        JButton btnAtras = new JButton("Atrás");
        south.add(btnElegir);
        south.add(btnAtras);
        fondo.add(south, BorderLayout.SOUTH);

        /* ======================================================
              KEY BINDINGS: ENTER = elegir, ESC = atrás
           ====================================================== */
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "elegir");
        am.put("elegir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnElegir.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "atras");
        am.put("atras", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnAtras.doClick();
            }
        });

        // EVENTOS
        list.addListSelectionListener(e -> updateDetails());

        btnElegir.addActionListener(e -> {
            Equipo sel = list.getSelectedValue();

            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un equipo.");
                return;
            }

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Seguro que quieres entrenar al " + sel.getNombre() + "?",
                    "Confirmar selección",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (opcion == JOptionPane.YES_OPTION) {
                new MainGameWindow(this, new GameSession(sel));
                dispose();
            }
        });

    }

    /* =====================================================
                       ACTUALIZAR DETALLES
       ===================================================== */
    private void updateDetails() {
        Equipo sel = list.getSelectedValue();
        if (sel == null) return;

        lblTituloEquipo.setText(sel.getNombre());

        // Escudo
        String file = ESCUDOS.get(sel.getNombre());
        if (file != null) {
            ImageIcon img = new ImageIcon("resources/images/escudos/" + file);
            Image scaled = img.getImage().getScaledInstance(150,150,Image.SCALE_SMOOTH);
            lblEscudo.setIcon(new ImageIcon(scaled));
        }

        // ===== INFO GENERAL IZQUIERDA =====
        StringBuilder left = new StringBuilder();

        left.append("“").append(HISTORIA.get(sel.getNombre())).append("”\n\n");
        left.append("Ciudad: ").append(sel.getCiudad()).append("\n");
        left.append("Estadio: ").append(sel.getEstadio()).append("\n");
        left.append("Reputación: ").append(REPUTACION.get(sel.getNombre())).append("\n");
        left.append("Formación: ").append(sel.getFormacion()).append("\n");
        left.append("Valoración: ").append(sel.getValoracion()).append(" / 5.0\n");
        left.append("Presupuesto: ").append(LeagueData.formatMoney(sel.getBudget())).append("\n");
        left.append("Objetivo: ").append(OBJETIVO.get(sel.getNombre())).append("\n");
        left.append("Fortaleza: ").append(FORTALEZAS[(int)(Math.random()*FORTALEZAS.length)]).append("\n\n");
        left.append("Ataque:   ").append(estrellas(sel.getValoracion()+0.5)).append("\n");
        left.append("Defensa:  ").append(estrellas(sel.getValoracion())).append("\n");

        detalleTextLeft.setText(left.toString());
        detalleTextLeft.setCaretPosition(0);

        // ===== ONCE TITULAR DERECHA =====
        StringBuilder right = new StringBuilder("Once titular:\n\n");
        int i = 1;
        for (var j : sel.getOnceTitular()) {
            right.append(i++).append(". ").append(j.getNombre())
                 .append(" - ").append(j.getPosicion())
                 .append(" (").append(j.getEdad()).append(" años) ")
                 .append(j.getValoracion()).append("/99\n");
        }

        detalleTextRight.setText(right.toString());
        detalleTextRight.setCaretPosition(0);
    }

    private String estrellas(double v) {
        int n = (int)Math.round(v);
        return "⭐".repeat(Math.min(n, 5));
    }

    class HighRowRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            label.setPreferredSize(new Dimension(label.getWidth(), 30));
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
