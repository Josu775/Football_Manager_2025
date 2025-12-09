package db;

import java.util.List;

import domain.Equipo;
import domain.Jugador;

public class DataManager {

    private static GestorBD gestor;
    private static EquipoDAO equipoDAO;
    private static JugadorDAO jugadorDAO;
    private static MercadoDAO mercadoDAO;
    private static ClasificacionDAO clasificacionDAO;


    //Inicializa el sistema de datos ( se llama al abrir el juego) 
    public static void init() {
        gestor = new GestorBD();
        gestor.connect();
        gestor.inicializarTablas();

        equipoDAO = new EquipoDAO(gestor);
        jugadorDAO = new JugadorDAO(gestor);
        clasificacionDAO = new ClasificacionDAO(gestor);
        mercadoDAO = new MercadoDAO(gestor);

        // Cerrar la BD correctamente al salir del programa
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (gestor != null) {
                gestor.close();
            }
        }));

        System.out.println("[Data] Sistema de datos listo.");
    }
    
    // Guarda en la BasedeDatos todo la liga ( equipos + jugadores) 
    public static void guardarLiga(List<Equipo> liga) {
        if (equipoDAO == null || jugadorDAO == null) return;

        for (Equipo e : liga) {
            equipoDAO.insertarEquipo(e);

            for (Jugador j : e.getPlantilla()) {
                jugadorDAO.insertarJugador(j);
            }
        }

        System.out.println("[BD] Liga guardada correctamente.");
    }

    // Carga toda la liga desde la BasedeDatos ( equipos + plantillas )      
    /** Carga toda la liga desde la BD (equipos + plantillas) */
    public static List<Equipo> cargarLigaDeBD() {
        if (equipoDAO == null || jugadorDAO == null) return List.of();

        List<Equipo> liga = equipoDAO.obtenerTodos();
        for (Equipo e : liga) {
            List<Jugador> jugadores = jugadorDAO.obtenerJugadores(e.getNombre());
            e.getPlantilla().addAll(jugadores);
        }
        
     // Cargar clasificación desde BD
        if (clasificacionDAO != null) {
            clasificacionDAO.cargarClasificacion(liga);
        }

        System.out.println("[BD] Liga cargada desde BD. Equipos: " + liga.size());
        return liga;
    }

    public static EquipoDAO getEquipoDAO() {
        return equipoDAO;
    }

    public static JugadorDAO getJugadorDAO() {
        return jugadorDAO;
    }

    public static GestorBD getGestor() {
        return gestor;
    }
    
    public static ClasificacionDAO getClasificacionDAO() {
        return clasificacionDAO;
    }
    
    public static MercadoDAO getMercadoDAO() { 
    	return mercadoDAO;
    }

}
