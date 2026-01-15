package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


	//GestorBasedeDatos para SQLite

public class GestorBD {

    // Ruta a la base de datos (archivo .db dentro de resources/db)
    private static final String URL = "jdbc:sqlite:resources/db/fm25.db";

    private Connection conn;

    //Abrir la conexión
   
    public void connect() {
        try {
            // Cargar driver (útil en muchas configuraciones)
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                System.err.println("[ERROR] GestorBD.connect");
                System.err.println("Driver SQLite no encontrado en el classpath.");
                System.err.println("Asegúrate de que sqlite-jdbc está incluido en el proyecto.");
            }

            conn = DriverManager.getConnection(URL);
            System.out.println("[BD] Conexión abierta");
        } catch (SQLException e) {
            System.err.println("[ERROR] GestorBD.connect");
            System.err.println("No se pudo establecer conexión con la base de datos.");
            System.err.println("Ruta BD: " + URL);
            System.err.println("Mensaje SQL: " + e.getMessage());
        }

    }

   
        }

    }
    
    public void limpiarClasificacion() {
        try (Statement st = conn.createStatement()) {
            st.execute("DELETE FROM Clasificacion;");
            System.out.println("[BD] Clasificación reiniciada.");
        } catch (SQLException e) {
            System.err.println("[ERROR] GestorBD.limpiarClasificacion");
            System.err.println("No se pudo reiniciar la tabla de clasificación.");
            System.err.println("Mensaje SQL: " + e.getMessage());
        }

    }


    //Crear las tablas si no existen
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
            
         // Tabla de clasificación (TeamStats)
            st.execute("""
				CREATE TABLE IF NOT EXISTS Clasificacion (
				    equipo TEXT PRIMARY KEY REFERENCES Equipo(nombre),
				    puntos INTEGER,
				    victorias INTEGER,
				    empates INTEGER,
				    derrotas INTEGER,
				    gf INTEGER,
				    gc INTEGER,
				    dg INTEGER
				);
            """);
            
         // Tabla de mercado (jugadores generados)
            st.execute("""
                CREATE TABLE IF NOT EXISTS Mercado (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT,
                    posicion TEXT,
                    edad INTEGER,
                    valoracion REAL,
                    clubOrigen TEXT,
                    precio REAL
                );
            """);


            System.out.println("[BD] Tablas inicializadas");

        } catch (SQLException e) {
            System.err.println("[ERROR] GestorBD.inicializarTablas");
            System.err.println("Error al crear o verificar las tablas de la base de datos.");
            System.err.println("Mensaje SQL: " + e.getMessage());
        }
        
        
    }

    // Getter de la conexión crear
    public Connection getConnection() {
        return conn;
    }
}
