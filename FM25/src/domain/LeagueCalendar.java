package domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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

        // 📅 Liga realista: empieza viernes 5 septiembre 2025
        LocalDate fechaBase = LocalDate.of(2025, 9, 5);

        // Horarios repartidos por orden
        LocalTime[] horas = {
                LocalTime.of(21, 0), // Viernes

                LocalTime.of(14, 0), LocalTime.of(16, 15),
                LocalTime.of(18, 30), LocalTime.of(21, 0), // Sábado

                LocalTime.of(14, 0), LocalTime.of(16, 15),
                LocalTime.of(18, 30), LocalTime.of(21, 0), // Domingo

                LocalTime.of(21, 0)  // Lunes
        };

        for (int r = 0; r < jornadas; r++) {

            List<String[]> duelos = new ArrayList<>();
            List<Match> jornada = new ArrayList<>();

            Equipo fijo = equipos.get(0);

            // Partido del fijo
            if (!rota.get(0).getNombre().equals("DESCANSA"))
                duelos.add(new String[]{fijo.getNombre(), rota.get(0).getNombre()});

            // Resto
            for (int i = 1; i < size / 2 + 1; i++) {
                Equipo e1 = rota.get(i);
                Equipo e2 = rota.get(size - i);

                if (!e1.getNombre().equals("DESCANSA") &&
                    !e2.getNombre().equals("DESCANSA")) {
                    duelos.add(new String[]{e1.getNombre(), e2.getNombre()});
                }
            }
            Collections.shuffle(duelos); // 🔥 aleatoriza el orden de los partidos

            // 🗓 Distribución viernes–lunes
            for (int i = 0; i < duelos.size(); i++) {

                LocalDate fechaPartido =
                        switch (i) {
                            case 0 -> fechaBase;            // viernes
                            case 1, 2, 3, 4 -> fechaBase.plusDays(1); // sábado
                            case 5, 6, 7, 8 -> fechaBase.plusDays(2); // domingo
                            default -> fechaBase.plusDays(3);         // lunes
                        };

                jornada.add(new Match(
                        duelos.get(i)[0],
                        duelos.get(i)[1],
                        fechaPartido,
                        horas[i]
                ));
            }

            calendario.add(jornada);

            // Rotamos equipos
            Collections.rotate(rota, 1);

            // 📅 SIGUIENTE JORNADA → siguiente VIERNES
            fechaBase = fechaBase.plusWeeks(1);
        }

     // --- GENERAR VUELTA ---
        List<List<Match>> vuelta = new ArrayList<>();

        LocalDate fechaBaseVuelta = fechaBase.plusWeeks(jornadas); // siguiente bloque completo

        for (int r = 0; r < jornadas; r++) {

            List<Match> jornadaIda = calendario.get(r);
            List<Match> jornadaVuelta = new ArrayList<>();

            // Mezclamos de nuevo para que NO coincida con los horarios de la ida
            List<String[]> duelos = new ArrayList<>();
            for (Match m : jornadaIda) {
                duelos.add(new String[]{m.visitante(), m.local()}); // inverso
            }
            Collections.shuffle(duelos);

            for (int i = 0; i < duelos.size(); i++) {

                LocalDate fechaPartido =
                        switch (i) {
                            case 0 -> fechaBaseVuelta;            // viernes
                            case 1,2,3,4 -> fechaBaseVuelta.plusDays(1); // sábado
                            case 5,6,7,8 -> fechaBaseVuelta.plusDays(2); // domingo
                            default -> fechaBaseVuelta.plusDays(3);     // lunes
                        };

                jornadaVuelta.add(
                    new Match(
                        duelos.get(i)[0],
                        duelos.get(i)[1],
                        fechaPartido,
                        horas[i]
                    )
                );
            }

            vuelta.add(jornadaVuelta);
            fechaBaseVuelta = fechaBaseVuelta.plusWeeks(1); // siguiente semana
        }

        calendario.addAll(vuelta);


        return calendario;
    }
}
