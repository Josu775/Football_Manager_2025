package db;

import domain.Jugador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Jugadores — completamente implementado.
 */
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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
            ex.printStackTrace();
        }
    }

    // --- BORRAR JUGADOR ---
    public void borrarJugador(String nombre) {
        String sql = "DELETE FROM Jugador WHERE nombre = ?";

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
