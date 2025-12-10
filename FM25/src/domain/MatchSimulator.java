package domain;

import java.util.List;
import java.util.Random;

import db.ClasificacionDAO;
import db.DataManager;

public class MatchSimulator {

    private static final Random RNG = new Random();

    private static final double HOME_ADV = 3.5; // ventaja casa

    // ============================
    //  SIMULAR TEMPORADA ENTERA
    // ============================
    public static void simularTemporada(List<Equipo> equipos) {
        if (equipos == null || equipos.size() < 2) return;

        for (Equipo e : equipos) {
            if (e.getStats() != null) e.getStats().reset();
        }

        int n = equipos.size();

        for (int vuelta = 0; vuelta < 2; vuelta++) {
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {

                    Equipo local     = (vuelta == 0 ? equipos.get(i) : equipos.get(j));
                    Equipo visitante = (vuelta == 0 ? equipos.get(j) : equipos.get(i));

                    simularPartido(local, visitante);

                    try { Thread.sleep(5); } catch (InterruptedException ignored) {}
                }
            }
        }

        ClasificacionDAO cdao = new ClasificacionDAO(DataManager.getGestor());
        for (Equipo e : equipos) cdao.guardarStats(e);
    }

    // ============================
    //  SIMULAR PARTIDO NORMAL
    // ============================
    private static void simularPartido(Equipo local, Equipo visitante) {
        
        double rLocal = local.calcularValoracionReal() + getClubBonus(local.getNombre()+ HOME_ADV + getLocaliaBonus(local.getNombre()));
        double rVisit = visitante.calcularValoracionReal() + getClubBonus(visitante.getNombre());


        double diff = (rLocal - rVisit) / 10.0;

        // Expected goals
        double expLocal = 1.2 + 1.0 / (1.0 + Math.exp(-diff));
        double expVisit = 1.2 + 1.0 / (1.0 + Math.exp(diff));

        int golesLocal = generarPoisson(expLocal);
        int golesVisit = generarPoisson(expVisit);

        local.getStats().addPartido(golesLocal, golesVisit);
        visitante.getStats().addPartido(golesVisit, golesLocal);

        ClasificacionDAO cdao = new ClasificacionDAO(DataManager.getGestor());
        cdao.guardarStats(local);
        cdao.guardarStats(visitante);
    }

    // ============================
    //  SIMULAR PARTIDO DIRECTO
    // ============================
    public static void simularPartidoDirecto(Equipo e1, Equipo e2, boolean local) {

        double r1 = e1.calcularValoracionReal();
        double r2 = e2.calcularValoracionReal();

        if (local) r1 += HOME_ADV;
        else       r2 += HOME_ADV;

        double diff = (r1 - r2) / 10.0;

        double exp1 = 1.2 + 1.0 / (1.0 + Math.exp(-diff));
        double exp2 = 1.2 + 1.0 / (1.0 + Math.exp(diff));

        int goles1 = generarPoisson(exp1);
        int goles2 = generarPoisson(exp2);

        if (local) {
            e1.getStats().addPartido(goles1, goles2);
            e2.getStats().addPartido(goles2, goles1);
        } else {
            e1.getStats().addPartido(goles2, goles1);
            e2.getStats().addPartido(goles1, goles2);
        }

        DataManager.getClasificacionDAO().guardarStats(e1);
        DataManager.getClasificacionDAO().guardarStats(e2);
    }

    // ============================
    //  SIMULAR JORNADA COMPLETA
    // ============================
    public static void simularJornadaCompleta(List<LeagueCalendar.Match> jornada, List<Equipo> liga) {

        for (LeagueCalendar.Match m : jornada) {

            Equipo local = null;
            Equipo visitante = null;

            for (Equipo e : liga) {
                if (e.getNombre().equals(m.local())) local = e;
                if (e.getNombre().equals(m.visitante())) visitante = e;
            }

            if (local == null || visitante == null) continue;

            simularPartido(local, visitante);
        }
    }

    // ============================
    //  POISSON DISTRIBUTION
    // ============================
    private static int generarPoisson(double lambda) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= RNG.nextDouble();
        } while (p > L);

        return k - 1;
    }
    
    private static double getClubBonus(String equipo) {

        return switch (equipo) {

            case "Real Madrid" -> 6.0;
            case "FC Barcelona" -> 6.0;
            case "Athletic Club" -> 3.0;
            
            case "Atlético de Madrid" -> 4.0;
            case "Valencia CF" -> 2.5;
            case "Sevilla FC" -> 2.0;

            case "RCD Espanyol" -> 1.5;
            case "Real Sociedad" -> 1.5;

            case "Real Betis" -> 1.0;
            case "Real Club Celta de Vigo" -> 1.0;

            default -> 0.0;
        };
    }
    
    private static double getLocaliaBonus(String equipo) {
        return switch(equipo) {

            case "Real Madrid", "FC Barcelona" -> 2.5;
            case "Atlético de Madrid" -> 2.0;
            case "Athletic Club", "Sevilla FC", "Valencia CF" -> 1.5;
            case "Real Sociedad", "Real Betis" -> 1.0;
            case "RCD Espanyol", "Real Club Celta de Vigo" -> 0.5;

            default -> 0.25; // Bonus normal para equipos pequeños
        };
    }

    
    
    
    
    

}
