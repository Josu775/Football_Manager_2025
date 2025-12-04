package db;

import domain.Equipo;
import domain.TeamStats;

import java.sql.*;
import java.util.List;

public class ClasificacionDAO {

    private GestorBD gestor;

    public ClasificacionDAO(GestorBD gestor) {
        this.gestor = gestor;
    }

    // INSERTAR O ACTUALIZAR ESTADÍSTICAS DE UN EQUIPO
    public void guardarStats(Equipo e) {

        String sql = """
            INSERT INTO Clasificacion(equipo, puntos, victorias, empates, derrotas, gf, gc, dg)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(equipo) DO UPDATE SET
                puntos = excluded.puntos,
                victorias = excluded.victorias,
                empates = excluded.empates,
                derrotas = excluded.derrotas,
                gf = excluded.gf,
                gc = excluded.gc,
                dg = excluded.dg;
        """;

        try (PreparedStatement pst = gestor.getConnection().prepareStatement(sql)) {

            TeamStats s = e.getStats();

            pst.setString(1, e.getNombre());
            pst.setInt(2, s.getPuntos());
            pst.setInt(3, s.getVictorias());
            pst.setInt(4, s.getEmpates());
            pst.setInt(5, s.getDerrotas());
            pst.setInt(6, s.getGf());
            pst.setInt(7, s.getGc());
            pst.setInt(8, s.getDg());

            pst.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // CARGAR CLASIFICACIÓN DESDE BD
    public void cargarClasificacion(List<Equipo> liga) {

        String sql = "SELECT * FROM Clasificacion";

        try (Statement st = gestor.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                String nombre = rs.getString("equipo");
                Equipo eq = buscarEquipo(liga, nombre);
                if (eq == null) continue;

                TeamStats s = eq.getStats();

                s.setPuntos(rs.getInt("puntos"));
                s.setVictorias(rs.getInt("victorias"));
                s.setEmpates(rs.getInt("empates"));
                s.setDerrotas(rs.getInt("derrotas"));
                s.setGf(rs.getInt("gf"));
                s.setGc(rs.getInt("gc"));
                // dg se calcula solo → no se carga
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("asdasdasd");

        }
    }//Prueba2

    private Equipo buscarEquipo(List<Equipo> liga, String nombre) {
        for (Equipo e : liga)
            if (e.getNombre().equals(nombre)) return e;
        return null;
    }
}
