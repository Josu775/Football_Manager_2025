package db;

import domain.Jugador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MercadoDAO {

    private GestorBD gestor;

    public MercadoDAO(GestorBD gestor) {
        this.gestor = gestor;
    }

    public void insertarJugadorMercado(Jugador j, String origen, double precio) {
        String sql = """
            INSERT INTO Mercado(nombre, posicion, edad, valoracion, clubOrigen, precio)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            pst.setString(1, j.getNombre());
            pst.setString(2, j.getPosicion());
            pst.setInt(3, j.getEdad());
            pst.setDouble(4, j.getValoracion());
            pst.setString(5, origen);
            pst.setDouble(6, precio);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ERROR] MercadoDAO.insertarJugadorMercado");
            System.err.println("No se pudo insertar el jugador en el mercado.");
            System.err.println("Jugador: " + j.getNombre());
            System.err.println("Club origen: " + origen);
            System.err.println("Precio: " + precio);
            System.err.println("Mensaje SQL: " + e.getMessage());
        }

    }

    public List<Object[]> cargarMercado() {
        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT * FROM Mercado";

        try (Statement st = gestor.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("posicion"),
                    rs.getInt("edad"),
                    rs.getDouble("valoracion"),
                    rs.getString("clubOrigen"),
                    rs.getDouble("precio")
                });
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] MercadoDAO.cargarMercado");
            System.err.println("Error al cargar los jugadores del mercado.");
            System.err.println("Mensaje SQL: " + e.getMessage());
        }


        return lista;
    }

    public void borrarDeMercado(int id) {
        try (PreparedStatement pst = gestor.getConnection().prepareStatement(
                "DELETE FROM Mercado WHERE id=?")) {
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ERROR] MercadoDAO.borrarDeMercado");
            System.err.println("No se pudo eliminar el jugador del mercado.");
            System.err.println("ID mercado: " + id);
            System.err.println("Mensaje SQL: " + e.getMessage());
        }

    }
}


