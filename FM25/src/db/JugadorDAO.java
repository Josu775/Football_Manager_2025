package db;

import domain.Jugador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

	//DAO de Jugadores- completamente implementados

public class JugadorDAO {

    private GestorBD gestor;

    public JugadorDAO(GestorBD gestor) {
        this.gestor = gestor;
    }

    // --- INSERTAR JUGADOR ---
    public void insertarJugador(Jugador j) {
        String sql = """
            INSERT INTO Jugador(nombre, posicion, edad, valoracion, equipo)
            VALUES(?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            pst.setString(1, j.getNombre());
            pst.setString(2, j.getPosicion());
            pst.setInt(3, j.getEdad());
            pst.setDouble(4, j.getValoracion());
            pst.setString(5, j.getEquipo());
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[ERROR] JugadorDAO.insertarJugador");
            System.err.println("No se pudo insertar el jugador en la base de datos.");
            System.err.println("Jugador: " + j.getNombre());
            System.err.println("Equipo: " + j.getEquipo());
            System.err.println("Mensaje SQL: " + ex.getMessage());
        }

    }

    // --- OBTENER JUGADORES DE UN EQUIPO ---
    public List<Jugador> obtenerJugadores(String equipo) {
        List<Jugador> lista = new ArrayList<>();
        String sql = "SELECT * FROM Jugador WHERE equipo = ?";

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            pst.setString(1, equipo);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Jugador j = new Jugador(
                        rs.getString("nombre"),
                        rs.getString("posicion"),
                        rs.getInt("edad"),
                        rs.getDouble("valoracion"),
                        rs.getString("equipo")
                );
                lista.add(j);
            }
        } catch (SQLException ex) {
            System.err.println("[ERROR] JugadorDAO.obtenerJugadores");
            System.err.println("Error al obtener jugadores del equipo.");
            System.err.println("Equipo: " + equipo);
            System.err.println("Mensaje SQL: " + ex.getMessage());
        }


        return lista;
    }

    // --- ACTUALIZAR EQUIPO DEL JUGADOR (tras un fichaje) ---
    public void actualizarEquipoJugador(String nombre, String nuevoEquipo) {
        String sql = "UPDATE Jugador SET equipo = ? WHERE nombre = ?";

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            pst.setString(1, nuevoEquipo);
            pst.setString(2, nombre);
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[ERROR] JugadorDAO.actualizarEquipoJugador");
            System.err.println("No se pudo actualizar el equipo del jugador.");
            System.err.println("Jugador: " + nombre);
            System.err.println("Nuevo equipo: " + nuevoEquipo);
            System.err.println("Mensaje SQL: " + ex.getMessage());
        }

    }

    // --- BORRAR JUGADOR ---
    public void borrarJugador(String nombre) {
        String sql = "DELETE FROM Jugador WHERE nombre = ?";

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[ERROR] JugadorDAO.borrarJugador");
            System.err.println("No se pudo borrar el jugador de la base de datos.");
            System.err.println("Jugador: " + nombre);
            System.err.println("Mensaje SQL: " + ex.getMessage());
        }

    }
}
