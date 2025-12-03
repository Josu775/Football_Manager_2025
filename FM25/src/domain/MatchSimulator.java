package domain;

import java.util.List;
import java.util.Random;

/**
 * Motor de simulación de partidos.
 * - Simula temporada completa (ida y vuelta).
 * - Simula partido único (para "siguiente partido").
 */
public class MatchSimulator {

    private static final Random RNG = new Random();

    /** 
     * Simula una temporada completa (ida y vuelta) para la lista de equipos.
     * Modifica directamente los TeamStats de cada Equipo.
     */
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
    }

    /** Simula un único partido entre local y visitante (para temporada completa). */
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

        local.getStats().addPartido(golesLocal, golesVisit);
        visitante.getStats().addPartido(golesVisit, golesLocal);
    }

    /** 
     * NUEVO: Simula un partido único teniendo en cuenta si tu equipo juega de local o visitante.
     * Se usa para "Simular siguiente partido" según el calendario.
     */
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
    }
}
