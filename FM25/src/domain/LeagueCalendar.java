package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeagueCalendar {

    public record Match(String local, String visitante, LocalDate fecha, LocalTime hora)
            implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    public static List<List<Match>> generarCalendario(List<Equipo> equipos) {

        int n = equipos.size();

        // Si es impar, añadimos equipo fantasma
        if (n % 2 != 0) {
            equipos = new ArrayList<>(equipos);
            equipos.add(new Equipo("DESCANSA", "", "", "", 0, 0));
            n++;
        }

        List<Equipo> rota = new ArrayList<>(equipos);
        rota.remove(0);

        int size = rota.size();
        int jornadas = n - 1;

        List<List<Match>> calendario = new ArrayList<>();

        // FECHA REAL DE INICIO (Viernes)
        LocalDate fechaBase = LocalDate.of(2025, 8, 15);

        // Horarios repartidos por orden
        LocalTime[] horas = {
                LocalTime.of(21, 0),                 // Viernes
                LocalTime.of(14, 0), LocalTime.of(16, 15), LocalTime.of(18, 30), LocalTime.of(21, 0),  // Sábado
                LocalTime.of(14, 0), LocalTime.of(16, 15), LocalTime.of(18, 30), LocalTime.of(21, 0),  // Domingo
                LocalTime.of(21, 0)                  // Lunes
        };

        for (int r = 0; r < jornadas; r++) {

            List<Match> jornada = new ArrayList<>();

            // Partidos ordenados como siempre
            List<String[]> duelos = new ArrayList<>();

            Equipo fijo = equipos.get(0);

            if (!rota.get(0).getNombre().equals("DESCANSA"))
                duelos.add(new String[]{fijo.getNombre(), rota.get(0).getNombre()});

            for (int i = 1; i < size / 2 + 1; i++) {
                Equipo e1 = rota.get(i);
                Equipo e2 = rota.get(size - i);

                if (!e1.getNombre().equals("DESCANSA")
                        && !e2.getNombre().equals("DESCANSA")) {
                    duelos.add(new String[]{e1.getNombre(), e2.getNombre()});
                }
            }

            // FECHAS: viernes/sábado/domingo/lunes
            for (int i = 0; i < duelos.size(); i++) {

                LocalDate fechaPartido =
                        switch (i) {
                            case 0 -> fechaBase;          // viernes
                            case 1,2,3,4 -> fechaBase.plusDays(1); // sábado
                            case 5,6,7,8 -> fechaBase.plusDays(2); // domingo
                            default -> fechaBase.plusDays(3);      // lunes
                        };

                jornada.add(
                        new Match(
                                duelos.get(i)[0],
                                duelos.get(i)[1],
                                fechaPartido,
                                horas[i]
                        )
                );
            }

            calendario.add(jornada);

            Collections.rotate(rota, 1);

            // SIGUIENTE JORNADA = siguiente viernes
            fechaBase = fechaBase.plusDays(7);
        }

        // VUELTA (inversión local/visitante)
        List<List<Match>> vuelta = new ArrayList<>();
        for (List<Match> j : calendario) {
            List<Match> j2 = new ArrayList<>();
            LocalDate fechaVuelta = j.get(0).fecha().plusWeeks(jornadas);
            for (Match m : j) {
                j2.add(new Match(
                        m.visitante(),
                        m.local(),
                        fechaVuelta,
                        m.hora()
                ));
            }
            vuelta.add(j2);
        }

        calendario.addAll(vuelta);
        return calendario;
    }
  
    
    public static Map<Integer, LocalDate> generarFechas(int totalJornadas, LocalDate inicio) {

        Map<Integer, LocalDate> mapa = new HashMap<>();

        int jornada = 1;
        LocalDate fecha = inicio;

        while (jornada <= totalJornadas) {

            // Solo viernes-sábado-domingo-lunes
            switch (fecha.getDayOfWeek()) {
                case FRIDAY, SATURDAY, SUNDAY, MONDAY -> {
                    mapa.put(jornada, fecha);
                    jornada++;
                }
            }

            fecha = fecha.plusDays(1);
        }

        return mapa;
    }


}
