package domain;

import java.util.List;
import java.util.Random;

import db.ClasificacionDAO;
import db.DataManager;

//Simula el partido/temporada
public class MatchSimulator {

    private static final Random RNG = new Random();

    //Simula una temporada entera (ida y vuelta)
    //Lo actualiza en la clasificacion
    public static void simularTemporada(List<Equipo> equipos) {
        if (equipos == null || equipos.size() < 2) return;

        //  1) Reiniciar estadísticas
        for (Equipo e : equipos) {
            if (e.getStats() != null) {
                e.getStats().reset();
            }
        }

        int n = equipos.size();

        //  2) Ida y vuelta: cada pareja de equipos juega dos veces
        for (int vuelta = 0; vuelta < 2; vuelta++) {
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    Equipo local      = (vuelta == 0 ? equipos.get(i) : equipos.get(j));
                    Equipo visitante  = (vuelta == 0 ? equipos.get(j) : equipos.get(i));
                    simularPartido(local, visitante);

                    // Pequeña pausa para que el hilo tarde un poco
                    try {
                        Thread.sleep(5); // 5 ms
                    } catch (InterruptedException ignored) {
                        return;
                    }
                }
            }
        }
        ClasificacionDAO cdao = new ClasificacionDAO(DataManager.getGestor());
        for (Equipo e : equipos) {
            cdao.guardarStats(e);
        }
    }

    private static void simularPartido(Equipo local, Equipo visitante) {
        double rLocal = local.getValoracion();    // 1.0 - 5.0
        double rVisit = visitante.getValoracion();
        double total  = rLocal + rVisit;

        double probLocal = (total <= 0.0) ? 0.5 : (rLocal / total);

        int golesLocal  = RNG.nextInt(4);
        int golesVisit  = RNG.nextInt(4);

        if (golesLocal == golesVisit) {
            double r = RNG.nextDouble();
            if (r < probLocal - 0.1) {
                golesLocal++;
            } else if (r > probLocal + 0.1) {
                golesVisit++;
            }
        }

        // Actualizar estadísticas de equipos
        local.getStats().addPartido(golesLocal, golesVisit);
        visitante.getStats().addPartido(golesVisit, golesLocal);
        ClasificacionDAO cdao = new ClasificacionDAO(DataManager.getGestor());
        cdao.guardarStats(local);
        cdao.guardarStats(visitante);
    }

    
    public static void simularPartidoDirecto(Equipo equipo, Equipo rival, boolean local) {

        double r1 = equipo.getValoracion();
        double r2 = rival.getValoracion();
        double total = r1 + r2;
        if (total <= 0) total = 1.0;

        double probEquipo = r1 / total;

        int goles1 = RNG.nextInt(4);
        int goles2 = RNG.nextInt(4);

        if (goles1 == goles2) {
            double r = RNG.nextDouble();
            if (r < probEquipo - 0.1) {
                goles1++;
            } else if (r > probEquipo + 0.1) {
                goles2++;
            }
        }

        if (local) {
            equipo.getStats().addPartido(goles1, goles2);
            rival.getStats().addPartido(goles2, goles1);
        } else {
            equipo.getStats().addPartido(goles2, goles1);
            rival.getStats().addPartido(goles1, goles2);
        }

        // === GUARDAR CLASIFICACIÓN EN BD ===
        DataManager.getClasificacionDAO().guardarStats(equipo);
        DataManager.getClasificacionDAO().guardarStats(rival);
    }
    
    public static void simularJornadaCompleta(List<LeagueCalendar.Match> jornada, List<Equipo> liga) {

        for (LeagueCalendar.Match m : jornada) {

            // Buscar objetos Equipo
            Equipo local = null;
            Equipo visitante = null;

            for (Equipo e : liga) {
                if (e.getNombre().equals(m.local())) local = e;
                if (e.getNombre().equals(m.visitante())) visitante = e;
            }

            if (local == null || visitante == null) continue;

            // Simular el partido (usa tu lógica global)
            simularPartido(local, visitante);
        }
    }


}
