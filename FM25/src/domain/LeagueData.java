package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * LeagueData con 20 equipos y presupuestos más altos.
 * Incluye método público formatMoney(...) para mostrar K/M/B€ con coma decimal.
 * Genera jugadores con valoración 60..99 en función de teamVal.
 */
public class LeagueData {

    private static final Random RNG = new Random();

    public static List<Equipo> getLaLiga20() {
        List<Equipo> lista = new ArrayList<>();

        
        lista.add(createEquipo("Real Madrid", "Madrid", "Santiago Bernabéu", "4-3-3", 5.0, 1_000_000_000));
        lista.add(createEquipo("FC Barcelona", "Barcelona", "Camp Nou", "4-3-3", 4.9, 950_000_000));
        lista.add(createEquipo("Atlético de Madrid", "Madrid", "Cívitas Metropolitano", "4-4-2", 4.6, 600_000_000));
        lista.add(createEquipo("Sevilla FC", "Sevilla", "Ramón Sánchez Pizjuán", "4-2-3-1", 4.1, 300_000_000));
        lista.add(createEquipo("Real Sociedad", "Donostia/San Sebastián", "Reale Arena", "4-3-3", 4.0, 240_000_000));
        lista.add(createEquipo("Villarreal CF", "Villarreal", "Estadio de la Cerámica", "4-4-2", 3.9, 220_000_000));
        lista.add(createEquipo("Real Betis", "Sevilla", "Benito Villamarín", "4-3-3", 3.9, 210_000_000));
        lista.add(createEquipo("Athletic Club", "Bilbao", "San Mamés", "4-2-3-1", 3.8, 190_000_000));
        lista.add(createEquipo("Valencia CF", "Valencia", "Mestalla", "4-4-2", 3.7, 150_000_000));
        lista.add(createEquipo("RCD Mallorca", "Mallorca", "Iberostar", "4-4-2", 3.4, 90_000_000));
        lista.add(createEquipo("CA Osasuna", "Pamplona", "El Sadar", "4-2-3-1", 3.3, 85_000_000));
        lista.add(createEquipo("RC Celta", "Vigo", "Balaídos", "4-3-3", 3.3, 80_000_000));
        lista.add(createEquipo("Getafe CF", "Getafe", "Coliseum Alfonso Pérez", "4-4-2", 3.2, 70_000_000));
        lista.add(createEquipo("Granada CF", "Granada", "Nuevo Los Cármenes", "4-2-3-1", 3.1, 65_000_000));
        lista.add(createEquipo("Rayo Vallecano", "Madrid", "Vallecas", "4-2-3-1", 3.0, 60_000_000));
        lista.add(createEquipo("Deportivo Alavés", "Vitoria-Gasteiz", "Mendizorrotza", "4-4-2", 2.9, 55_000_000));
        lista.add(createEquipo("Cádiz CF", "Cádiz", "Nuevo Mirandilla", "4-4-2", 2.8, 50_000_000));
        lista.add(createEquipo("Elche CF", "Elche", "Martínez Valero", "4-4-2", 2.7, 45_000_000));
        lista.add(createEquipo("RCD Espanyol", "Barcelona", "RCDE Stadium", "4-3-3", 3.2, 75_000_000));
        lista.add(createEquipo("Girona FC", "Girona", "Montilivi", "4-3-3", 3.4, 100_000_000));

        
        for (Equipo e : lista) {
            List<Jugador> once = new ArrayList<>();
            once.add(new Jugador(randomNombre(), "POR", randomEdad(24,34), randomValor(e.getValoracion())));
            for (int i=0;i<4;i++) once.add(new Jugador(randomNombre(), "DEF", randomEdad(20,33), randomValor(e.getValoracion())));
            for (int i=0;i<3;i++) once.add(new Jugador(randomNombre(), "MID", randomEdad(19,32), randomValor(e.getValoracion())));
            for (int i=0;i<3;i++) once.add(new Jugador(randomNombre(), "ATT", randomEdad(19,33), randomValor(e.getValoracion())));
            e.setOnceTitular(once);
        }
        return lista;
    }

    private static Equipo createEquipo(String n, String c, String s, String f, double val, double budget) {
        return new Equipo(n, c, s, f, val, budget);
    }

    private static int randomEdad(int a, int b) { return a + RNG.nextInt(b - a + 1); }

    
    private static int playerBaseFromTeamVal(double teamVal) {
        double base = 3.63636 * teamVal + 65.8182;
        return (int) Math.round(base);
    }

    private static int randomValor(double teamVal) {
        int base = playerBaseFromTeamVal(teamVal);
        int variation = RNG.nextInt(13) - 6; 
        int val = base + variation;
        if (val < 60) val = 60;
        if (val > 99) val = 99;
        return val;
    }

    private static String randomNombre() {
        String[] fn = {"Álvaro","Alejandro","Sergio","David","Juan","Pablo","Luis","Miguel","Javier","Carlos",
                "Andrés","Fernando","Rubén","Óscar","Diego","Iván","Hugo","Martín","Marco","Iker"};
        String[] ln = {"García","González","Rodríguez","Fernández","López","Martínez","Sánchez","Pérez","Gómez","Ruiz"};
        return fn[RNG.nextInt(fn.length)] + " " + ln[RNG.nextInt(ln.length)];
    }

    /**
     * Método público para formatear dinero: 120M€, 1,8M€, 950K€, etc.
     */
    public static String formatMoney(double amount) {
        if (amount >= 1_000_000_000) {
            double v = amount / 1_000_000_000.0;
            return String.format("%.1f", v).replace('.', ',') + "B€";
        }
        if (amount >= 1_000_000) {
            double v = amount / 1_000_000.0;
            if (Math.abs(v - Math.round(v)) < 0.01) return String.format("%.0f", v) + "M€";
            return String.format("%.1f", v).replace('.', ',') + "M€";
        }
        if (amount >= 1000) {
            double v = amount / 1000.0;
            if (Math.abs(v - Math.round(v)) < 0.01) return String.format("%.0f", v) + "K€";
            return String.format("%.1f", v).replace('.', ',') + "K€";
        }
        return String.format("%.0f€", amount);
    }
}
