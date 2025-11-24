package gui;

import domain.Equipo;
import domain.LeagueData;
import domain.GameSession;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamSelectionWindow extends JFrame {

    private JList<Equipo> list;
    private JLabel lblEscudo;
    private JLabel lblTituloEquipo;

    private JTextArea detalleTextLeft;
    private JTextArea detalleTextRight; // NUEVA COLUMNA DERECHA

    private JButton btnVerPlantilla;
    private JLabel lblMensajeInicial;

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

    // === TEXTOS ANTIGUOS RESTAURADOS ===

    private static final Map<String, String> HISTORIA = new HashMap<>();
    static {
        HISTORIA.put("Real Madrid", "Historia que tú hiciste, historia por hacer.");
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
        HISTORIA.put("Real Club Celta de Vigo", "Afouteza,orgullo e tradición.");
        HISTORIA.put("Getafe CF", "Esto es fútbol, papá.");
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
        OBJETIVO.put("Real Club Celta de Vigo", "Salvarse");
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
        REPUTACION.put("Girona FC", "Baja");
        REPUTACION.put("RCD Mallorca", "Baja");
        REPUTACION.put("CA Osasuna", "Media");
        REPUTACION.put("Real Club Celta de Vigo", "Baja");
        REPUTACION.put("RCD Espanyol", "Baja");
        REPUTACION.put("Getafe CF", "Baja");
        REPUTACION.put("Rayo Vallecano", "Baja");
        REPUTACION.put("Deportivo Alavés", "Media");
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

        // ⚽️ Liga creada UNA vez para esta ventana
        List<Equipo> liga = LeagueData.getLaLiga20();

        DefaultListModel<Equipo> model = new DefaultListModel<>();
        for (Equipo e : liga) {
            model.addElement(e);
        }

        list = new JList<>(model);
        list.setFont(new Font("Arial", Font.BOLD, 17));
        list.setFixedCellHeight(30);
        list.setBackground(new Color(10,20,50));
        list.setForeground(Color.WHITE);
        list.setSelectionBackground(new Color(50,70,140));
        list.setSelectionForeground(Color.WHITE);

        fondo.add(new JScrollPane(list), BorderLayout.WEST);

        // ==== PANEL DERECHO ====
        JPanel right = new JPanel(null);
        right.setBackground(new Color(8,16,45));
        fondo.add(right, BorderLayout.CENTER);

        lblMensajeInicial = new JLabel("Selecciona un equipo", SwingConstants.CENTER);
        lblMensajeInicial.setForeground(Color.WHITE);
        lblMensajeInicial.setFont(new Font("Arial", Font.BOLD, 32));
        lblMensajeInicial.setBounds(100, 250, 500, 60);
        right.add(lblMensajeInicial);

        lblTituloEquipo = new JLabel("", SwingConstants.CENTER);
        lblTituloEquipo.setFont(new Font("Arial", Font.BOLD, 34));
        lblTituloEquipo.setForeground(Color.WHITE);
        lblTituloEquipo.setBounds(0, 20, 700, 45);
        lblTituloEquipo.setVisible(false);
        right.add(lblTituloEquipo);

        lblEscudo = new JLabel("", SwingConstants.CENTER);
        lblEscudo.setBounds(260, 80, 180, 180);
        lblEscudo.setVisible(false);
        right.add(lblEscudo);

        btnVerPlantilla = new JButton("Ver plantilla");
        btnVerPlantilla.setBounds(300, 270, 100, 32);
        btnVerPlantilla.setVisible(false);
        btnVerPlantilla.addActionListener(e -> {
            Equipo sel = list.getSelectedValue();
            if (sel != null) new FullSquadWindow(this, sel);
        });
        right.add(btnVerPlantilla);

        // ============================
        //   PANEL INFERIOR 2 COLUMNAS
        // ============================
        JPanel infoPanel = new JPanel(null);
        infoPanel.setBackground(new Color(8,16,45));
        infoPanel.setBounds(120, 330, 630, 230);

        right.add(infoPanel);

        // IZQUIERDA (5 datos)
        detalleTextLeft = new JTextArea();
        detalleTextLeft.setEditable(false);
        detalleTextLeft.setOpaque(false);
        detalleTextLeft.setForeground(Color.WHITE);
        detalleTextLeft.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 19));
        detalleTextLeft.setLineWrap(true);
        detalleTextLeft.setWrapStyleWord(true);
        detalleTextLeft.setBounds(0, 0, 300, 230);
        infoPanel.add(detalleTextLeft);

        // DERECHA (otros 5 datos)
        detalleTextRight = new JTextArea();
        detalleTextRight.setEditable(false);
        detalleTextRight.setOpaque(false);
        detalleTextRight.setForeground(Color.WHITE);
        detalleTextRight.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 19));
        detalleTextRight.setLineWrap(true);
        detalleTextRight.setWrapStyleWord(true);
        detalleTextRight.setBounds(310, 0, 300, 230);
        infoPanel.add(detalleTextRight);

        // EVENTO
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateDetails();
        });

        // BOTONES
        JPanel south = new JPanel();
        JButton btnElegir = new JButton("Elegir equipo");
        JButton btnAtras = new JButton("Atrás");
        south.add(btnElegir);
        south.add(btnAtras);
        fondo.add(south, BorderLayout.SOUTH);

        btnElegir.addActionListener(e -> {
            Equipo sel = list.getSelectedValue();
            if (sel != null) {
                // 👉 Pasamos también la liga completa a la GameSession
                new MainGameWindow(this, new GameSession(sel, liga));
                dispose();
            }
        });

        btnAtras.addActionListener(e -> {
            new WelcomeWindow();
            dispose();
        });
    }

    private void updateDetails() {

        Equipo sel = list.getSelectedValue();
        if (sel == null) return;

        lblMensajeInicial.setVisible(false);
        lblTituloEquipo.setVisible(true);
        lblEscudo.setVisible(true);
        btnVerPlantilla.setVisible(true);

        lblTituloEquipo.setText(sel.getNombre());

        String file = ESCUDOS.get(sel.getNombre());
        if (file != null) {
            ImageIcon img = new ImageIcon("resources/images/escudos/" + file);
            Image scaled = img.getImage().getScaledInstance(180,180,Image.SCALE_SMOOTH);
            lblEscudo.setIcon(new ImageIcon(scaled));
        }

        // ======================================================
        //      NUEVA VALORACIÓN ESTILO FOOTBALL MANAGER / EA FC
        // ======================================================

        int valor100 = Math.max(75, (int)(sel.getValoracion() * 20));

        int ataque  = Math.max(75, Math.min(99, valor100 + (int)(Math.random()*6 - 3)));
        int defensa = Math.max(75, Math.min(99, valor100 + (int)(Math.random()*6 - 3)));

        // COLUMNA IZQUIERDA
        StringBuilder left = new StringBuilder();
        left.append("“").append(HISTORIA.get(sel.getNombre())).append("”\n\n");
        left.append("Ciudad: ").append(sel.getCiudad()).append("\n");
        left.append("Estadio: ").append(sel.getEstadio()).append("\n");
        left.append("Reputación: ").append(REPUTACION.get(sel.getNombre())).append("\n");
        left.append("Formación: ").append(sel.getFormacion()).append("\n");

        detalleTextLeft.setText(left.toString());
        detalleTextLeft.setCaretPosition(0);

        // COLUMNA DERECHA
        StringBuilder right = new StringBuilder();
        right.append("Valoración: ").append(valor100).append("\n");
        right.append("Presupuesto: ").append(LeagueData.formatMoney(sel.getBudget())).append("\n");
        right.append("Objetivo: ").append(OBJETIVO.get(sel.getNombre())).append("\n");
        right.append("Fortaleza: ").append(FORTALEZAS[(int)(Math.random()*FORTALEZAS.length)]).append("\n");
        right.append("Ataque: ").append(ataque).append("\n");
        right.append("Defensa: ").append(defensa).append("\n");

        detalleTextRight.setText(right.toString());
        detalleTextRight.setCaretPosition(0);
    }
}
