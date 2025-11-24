package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * GestorBD preparado para SQLite, pero aún sin activar.
 * Mantiene toda la estructura real lista.
 */
public class GestorBD {

    // Ruta futura a la base de datos
    private static final String URL = "jdbc:sqlite:resources/db/fm25.db";
    
    private Connection conn;

    /** FUTURO: Abrir conexión */
    public void connect() {
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("[BD] Conexión abierta");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** FUTURO: Cerrar conexión */
    public void close() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("[BD] Conexión cerrada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** FUTURO: Crear tablas si no existen */
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
            e.printStackTrace();
        }
    }

    /** Getter de la conexión (cuando la uses en el futuro) */
    public Connection getConnection() {
        return conn;
    }
}
