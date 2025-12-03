package db;

import domain.Equipo;
import domain.TeamStats;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClasificacionDAO {

    private GestorBD gestor;

    public ClasificacionDAO(GestorBD gestor) {
        this.gestor = gestor;
    }

    // INSERTAR o ACTUALIZAR estadísticas
    public void guardarStats(Equipo e) {
        String sql = """
            INSERT INTO Clasificacion(equipo, puntos, gf, gc, dg)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(equipo)
            DO UPDATE SET puntos=?, gf=?, gc=?, dg=?;
        """;

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {
            TeamStats s = e.getStats();

            pst.setString(1, e.getNombre());
            pst.setInt(2, s.getPuntos());
            pst.setInt(3, s.getGf());
            pst.setInt(4, s.getGc());
            pst.setInt(5, s.getDg());

            pst.setInt(6, s.getPuntos());
            pst.setInt(7, s.getGf());
            pst.setInt(8, s.getGc());
            pst.setInt(9, s.getDg());

            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // CARGAR TODA LA CLASIFICACIÓN
    public List<Equipo> cargarClasificacion(List<Equipo> liga) {

        String sql = "SELECT * FROM Clasificacion";

        try (Statement st = gestor.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String nombre = rs.getString("equipo");

                for (Equipo e : liga) {
                    if (e.getNombre().equals(nombre)) {

                        TeamStats s = e.getStats();
                        s.setPuntos(rs.getInt("puntos"));
                        s.setGf(rs.getInt("gf"));
                        s.setGc(rs.getInt("gc"));
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return liga;
    }
}
