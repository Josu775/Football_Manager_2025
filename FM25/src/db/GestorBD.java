package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * GestorBD para SQLite.
 */
public class GestorBD {

    // Ruta a la base de datos (archivo .db dentro de resources/db)
    private static final String URL = "jdbc:sqlite:resources/db/fm25.db";

    private Connection conn;

    /** Abrir conexión */
    public void connect() {
        try {
            // Cargar driver (útil en muchas configuraciones)
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                System.err.println("[BD] Driver SQLite no encontrado en el classpath.");
                e.printStackTrace();
            }

            conn = DriverManager.getConnection(URL);
            System.out.println("[BD] Conexión abierta");
        } catch (SQLException e) {
            System.err.println("[BD] Error al conectar con la BD");
            e.printStackTrace();
        }
    }

    /** Cerrar conexión */
    public void close() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("[BD] Conexión cerrada");
            }
        } catch (SQLException e) {
            System.err.println("[BD] Error al cerrar la BD");
            e.printStackTrace();
        }
    }

    /** Crear tablas si no existen */
    public void inicializarTablas() {
        try (Statement st = conn.createStatement()) {

            // Tabla de equipos
            st.execute("""
                CREATE TABLE IF NOT EXISTS Equipo (
                    nombre TEXT PRIMARY KEY,
                    ciudad TEXT,
                    estadio TEXT,
                    formacion TEXT,
                    valoracion REAL,
                    presupuesto REAL
                );
            """);

            // Tabla de jugadores
            st.execute("""
                CREATE TABLE IF NOT EXISTS Jugador (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT,
                    posicion TEXT,
                    edad INTEGER,
                    valoracion REAL,
                    equipo TEXT REFERENCES Equipo(nombre)
                );
            """);

            System.out.println("[BD] Tablas inicializadas");

        } catch (SQLException e) {
            System.err.println("[BD] Error al crear tablas");
            e.printStackTrace();
        }
    }

    /** Getter de la conexión */
    public Connection getConnection() {
        return conn;
    }
}
