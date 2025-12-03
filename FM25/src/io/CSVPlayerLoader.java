package io;

import domain.Jugador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class CSVPlayerLoader {

    private static final Map<String, String> NORMALIZAR = Map.ofEntries(
        // Primera
        Map.entry("Athletic Club de Bilbao", "Athletic Club"),
        Map.entry("Club Atlético de Madrid", "Atlético de Madrid"),
        Map.entry("Real Sociedad de Fútbol", "Real Sociedad"),
        Map.entry("Sevilla Fútbol Club", "Sevilla FC"),
        Map.entry("Club Atlético Osasuna", "CA Osasuna"),
        Map.entry("Villarreal Club de Fútbol", "Villarreal CF"),
        Map.entry("Real Betis Balompié", "Real Betis"),
        Map.entry("Valencia Club de Fútbol", "Valencia CF"),
        Map.entry("Getafe Club de Fútbol", "Getafe CF"),
        Map.entry("Rayo Vallecano de Madrid", "Rayo Vallecano"),
        Map.entry("Club Deportivo Alavés", "Deportivo Alavés"),
        Map.entry("Elche Club de Fútbol", "Elche CF"),
        Map.entry("Real Club Deportivo Espanyol", "RCD Espanyol"),
        Map.entry("Girona Fútbol Club", "Girona FC"),
        Map.entry("Levante Unión Deportiva", "Levante UD"),
        Map.entry("Real Club Celta de Vigo", "Real Club Celta de Vigo"),
        Map.entry("Real Madrid Club de Fútbol", "Real Madrid"),
        Map.entry("Real Oviedo", "Real Oviedo"),
        Map.entry("Real Club Deportivo Mallorca", "RCD Mallorca"),

        //  EXTRA: variantes muy típicas de BARÇA
        Map.entry("Fútbol Club Barcelona", "FC Barcelona"),
        Map.entry("Futbol Club Barcelona", "FC Barcelona"),
        Map.entry("Barcelona", "FC Barcelona"),
        Map.entry("Barça", "FC Barcelona"),

        //  EXTRA: variantes muy típicas de GIRONA
        Map.entry("Girona Futbol Club", "Girona FC"),
        Map.entry("Girona FC", "Girona FC"),
        Map.entry("Girona", "Girona FC"),

        // Extras que suelen aparecer en CSV de transfermarkt, sofifa, etc.
        Map.entry("RC Celta", "Real Club Celta de Vigo"),
        Map.entry("Celta Vigo", "Real Club Celta de Vigo"),
        Map.entry("Mallorca", "RCD Mallorca"),
        Map.entry("Espanol", "RCD Espanyol"),
        Map.entry("Atletico Madrid", "Atlético de Madrid")
    );

    private static String normalizarEquipo(String nombre) {
        return NORMALIZAR.getOrDefault(nombre.trim(), nombre.trim());
    }

    public static List<Jugador> cargarJugadoresDesdeCSV(String ruta) {

        List<Jugador> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea = br.readLine(); // cabecera

            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;

                String[] partes = linea.split(",");

                if (partes.length < 3) continue;

                String equipoCSV = partes[0].trim();
                String nombre = partes[1].trim();
                String posicion = partes[2].trim();

                String equipo = normalizarEquipo(equipoCSV);

                lista.add(new Jugador(nombre, posicion, equipo));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}
