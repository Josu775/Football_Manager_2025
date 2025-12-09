package db;

import domain.Equipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//DAO de Equipos--------Completamente implementado para SQLite.

public class EquipoDAO {

    private GestorBD gestor;

    public EquipoDAO(GestorBD gestor) {
        this.gestor = gestor;
    }

    // --- INSERTAR EQUIPO ---
    public void insertarEquipo(Equipo e) {
        String sql = """
            INSERT OR IGNORE INTO Equipo(nombre, ciudad, estadio, formacion, valoracion, presupuesto)
            VALUES(?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            pst.setString(1, e.getNombre());
            pst.setString(2, e.getCiudad());
            pst.setString(3, e.getEstadio());
            pst.setString(4, e.getFormacion());
            pst.setDouble(5, e.getValoracion());
            pst.setDouble(6, e.getBudget());
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // --- OBTENER 1 EQUIPO POR NOMBRE ---
    public Equipo obtenerEquipo(String nombre) {
        String sql = "SELECT * FROM Equipo WHERE nombre = ?";

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            pst.setString(1, nombre);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new Equipo(
                        rs.getString("nombre"),
                        rs.getString("ciudad"),
                        rs.getString("estadio"),
                        rs.getString("formacion"),
                        rs.getDouble("valoracion"),
                        rs.getDouble("presupuesto")
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // --- OBTENER TODOS LOS EQUIPOS ---
    public List<Equipo> obtenerTodos() {
        List<Equipo> lista = new ArrayList<>();

        String sql = "SELECT * FROM Equipo";

        try (Statement st = gestor.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Equipo e = new Equipo(
                        rs.getString("nombre"),
                        rs.getString("ciudad"),
                        rs.getString("estadio"),
                        rs.getString("formacion"),
                        rs.getDouble("valoracion"),
                        rs.getDouble("presupuesto")
                );
                lista.add(e);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

    // --- ACTUALIZAR PRESUPUESTO ---
    public void actualizarPresupuesto(String nombreEquipo, double nuevoBudget) {
        String sql = "UPDATE Equipo SET presupuesto = ? WHERE nombre = ?";

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            pst.setDouble(1, nuevoBudget);
            pst.setString(2, nombreEquipo);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // --- ACTUALIZAR FORMACION ---
    public void actualizarFormacion(String nombreEquipo, String nuevaFormacion) {
        String sql = "UPDATE Equipo SET formacion = ? WHERE nombre = ?";

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            pst.setString(1, nuevaFormacion);
            pst.setString(2, nombreEquipo);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    
}
