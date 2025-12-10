package domain;

import io.CSVPlayerLoader;
import db.DataManager;

import java.util.*;

public class LeagueData {

    private static final Random RNG = new Random();

    //ciudad y estadio de los equipos
    private static final Map<String, String[]> INFO_EQUIPOS = Map.ofEntries(
        Map.entry("Athletic Club", new String[]{"Bilbao", "San Mamés"}),
        Map.entry("Atlético de Madrid", new String[]{"Madrid", "Cívitas Metropolitano"}),
        Map.entry("Real Madrid", new String[]{"Madrid", "Santiago Bernabéu"}),
        Map.entry("CA Osasuna", new String[]{"Pamplona", "El Sadar"}),
        Map.entry("Rayo Vallecano", new String[]{"Madrid", "Estadio de Vallecas"}),
        Map.entry("RCD Mallorca", new String[]{"Palma", "Son Moix"}),
        Map.entry("Deportivo Alavés", new String[]{"Vitoria", "Mendizorrotza"}),
        Map.entry("RCD Espanyol", new String[]{"Barcelona", "RCDE Stadium"}),
        Map.entry("Levante UD", new String[]{"Valencia", "Ciutat de València"}),
        Map.entry("Getafe CF", new String[]{"Getafe", "Coliseum Alfonso Pérez"}),
        Map.entry("Sevilla FC", new String[]{"Sevilla", "Ramón Sánchez Pizjuán"}),
        Map.entry("Villarreal CF", new String[]{"Vila-real", "Estadio de la Cerámica"}),
        Map.entry("FC Barcelona", new String[]{"Barcelona", "Camp Nou"}),
        Map.entry("Real Club Celta de Vigo", new String[]{"Vigo", "Balaídos"}),
        Map.entry("Real Betis", new String[]{"Sevilla", "Benito Villamarín"}),
        Map.entry("Valencia CF", new String[]{"Valencia", "Mestalla"}),
        Map.entry("Real Sociedad", new String[]{"Donostia / San Sebastián", "Reale Arena"}),
        Map.entry("Real Oviedo", new String[]{"Oviedo", "Carlos Tartiere"}),
        Map.entry("Girona FC", new String[]{"Girona", "Montilivi"}),
        Map.entry("Elche CF", new String[]{"Elche", "Martínez Valero"})
    );

    //Devuelve la lista real de equipos con jugadores
    
    public static List<Equipo> getLaLiga20() {

        // 0. Intentar cargar desde BD (si ya se ha guardado antes)
        try {
            if (DataManager.getEquipoDAO() != null) {
                List<Equipo> desdeBD = DataManager.cargarLigaDeBD();
                if (!desdeBD.isEmpty()) {
                    return desdeBD;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //  1. Cargar jugadores reales del CSV
        List<Jugador> jugadores = CSVPlayerLoader.cargarJugadoresDesdeCSV(
            "resources/data/plantillas_laliga_25_26.csv"
        );

        //  2. Crear mapa equipo → objeto Equipo
        Map<String, Equipo> mapa = new LinkedHashMap<>();

        for (Jugador j : jugadores) {

            String nombreEq = j.getEquipo();

            //  Normalizar el nombre
            String canonical = normalizarNombre(nombreEq);

            if (!mapa.containsKey(canonical)) {

                String[] info = INFO_EQUIPOS.get(canonical);
                String ciudad = info != null ? info[0] : "España";
                String estadio = info != null ? info[1] : canonical + " Stadium";

                //  Valoración realista según tamaño de club
                double valoracion = 0;
                double budget = generarPresupuesto(canonical);

                Equipo eq = new Equipo(
                	    canonical, ciudad, estadio,
                	    "4-3-3", valoracion, budget
                	);

                	// asignamos bonus según histórico
                	eq.setReputacionBonus(reputacion(canonical));

                	mapa.put(canonical, eq);

            }

            mapa.get(canonical).getPlantilla().add(j);
        }

        List<Equipo> liga = new ArrayList<>(mapa.values());

        //  3. Guardar en BD para futuras ejecuciones
        try {
            if (DataManager.getEquipoDAO() != null) {
                DataManager.guardarLiga(liga);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return liga;
    }

    //Normaliza nombres largos del SCV a nombres cortos para crear abreviaturas
    private static String normalizarNombre(String n) {
        if (n == null) return null;

        return switch (n) {
            case "Real Madrid Club de Fútbol" -> "Real Madrid";
            case "Fútbol Club Barcelona", "Futbol Club Barcelona" -> "FC Barcelona";
            case "Club Atlético de Madrid" -> "Atlético de Madrid";
            case "Athletic Club de Bilbao" -> "Athletic Club";
            case "Club Atlético Osasuna" -> "CA Osasuna";
            case "Real Club Deportivo Mallorca" -> "RCD Mallorca";
            case "Girona Futbol Club", "Girona Fútbol Club" -> "Girona FC";
            case "Real club Deportivo Espanyol" -> "RCD Espanyol";
            case "Getafe Club de Fútbol" -> "Getafe CF";
            case "Valencia Club de Fútbol" -> "Valencia CF";
            case "Real Sociedad de Fútbol" -> "Real Sociedad";
            case "Rayo Vallecano de Madrid" -> "Rayo Vallecano";
            case "Real Club Celta de Vigo", "Real Club Celta De Vigo", "RC Celta", "RC Celta de Vigo" -> "Real Club Celta de Vigo";
            case "Club Deportivo Alavés" -> "Deportivo Alavés";
            case "Levante Unión Deportiva" -> "Levante UD";
            default -> n;
        };
    }

    // Valoracion del club, ralista mas o menos
    private static double generarValoracion(String equipo) {
        return switch (equipo) {
            case "Real Madrid", "FC Barcelona" -> 4.8;
            case "Atlético de Madrid" -> 4.6;
            case "Sevilla FC", "Real Sociedad", "Villarreal CF" -> 4.3;
            case "Athletic Club", "Real Betis", "Valencia CF" -> 4.0;
            default -> 3.5;
        };
    }

    //Presupuesto del club
    private static double generarPresupuesto(String equipo) {
        return switch (equipo) {
            case "Real Madrid" -> 800_000_000;
            case "FC Barcelona" -> 750_000_000;
            case "Atlético de Madrid" -> 500_000_000;
            case "Sevilla FC", "Villarreal CF", "Real Sociedad" -> 250_000_000;
            case "Athletic Club", "Real Betis", "Valencia CF" -> 150_000_000;
            default -> 80_000_000;
        };
    }

    //Formatea dinero 
    public static String formatMoney(double amount) {
        if (amount >= 1_000_000_000)
            return String.format("%.1fB€", amount / 1_000_000_000).replace('.', ',');
        if (amount >= 1_000_000)
            return String.format("%.1fM€", amount / 1_000_000).replace('.', ',');
        return ((int) amount) + "€";
    }
    
    private static int reputacion(String equipo) {
        return switch (equipo) {
            case "Real Madrid" -> 10;
            case "FC Barcelona" -> 9;
            case "Atlético de Madrid" -> 8;
            case "Athletic Club" -> 7;
            case "Sevilla FC", "Valencia CF" -> 6;
            case "Real Sociedad", "Villarreal CF", "Real Betis" -> 5;
            default -> 2; // por defecto para equipos pequeños
        };
    }


}
