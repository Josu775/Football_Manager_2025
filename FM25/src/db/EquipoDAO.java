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
            System.err.println("[ERROR] EquipoDAO.insertarEquipo");
            System.err.println("No se pudo insertar el equipo en la base de datos.");
            System.err.println("Equipo: " + e.getNombre());
            System.err.println("Mensaje SQL: " + ex.getMessage());
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
            System.err.println("[ERROR] EquipoDAO.obtenerEquipo");
            System.err.println("Error al obtener el equipo desde la base de datos.");
            System.err.println("Nombre buscado: " + nombre);
            System.err.println("Mensaje SQL: " + ex.getMessage());
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
            System.err.println("[ERROR] EquipoDAO.obtenerTodos");
            System.err.println("Error al obtener la lista completa de equipos.");
            System.err.println("Mensaje SQL: " + ex.getMessage());
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
            System.err.println("[ERROR] EquipoDAO.actualizarPresupuesto");
            System.err.println("No se pudo actualizar el presupuesto del equipo.");
            System.err.println("Equipo: " + nombreEquipo);
            System.err.println("Nuevo presupuesto: " + nuevoBudget);
            System.err.println("Mensaje SQL: " + ex.getMessage());
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
            System.err.println("[ERROR] EquipoDAO.actualizarFormacion");
            System.err.println("No se pudo actualizar la formación del equipo.");
            System.err.println("Equipo: " + nombreEquipo);
            System.err.println("Nueva formación: " + nuevaFormacion);
            System.err.println("Mensaje SQL: " + ex.getMessage());
        }

    }
    
    
}
