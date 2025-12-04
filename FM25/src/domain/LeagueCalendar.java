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

    public static List<List<Match>> generarCalendario(List<Equipo> equiposOriginal) {

        // Copia segura
        List<Equipo> equipos = new ArrayList<>(equiposOriginal);

        int n = equipos.size();

        if (n % 2 != 0) {
            equipos.add(new Equipo("DESCANSA", "", "", "", 0, 0));
            n++;
        }

        List<Equipo> rota = new ArrayList<>(equipos);
        rota.remove(0);

        int size = rota.size();
        int jornadas = n - 1;

        List<List<Match>> calendario = new ArrayList<>();

        LocalDate fechaPrimeraVuelta = LocalDate.of(2025, 9, 12);
        LocalTime[] horas = {
                LocalTime.of(21, 0),
                LocalTime.of(14, 0), LocalTime.of(16, 15), LocalTime.of(18, 30), LocalTime.of(21, 0),
                LocalTime.of(14, 0), LocalTime.of(16, 15), LocalTime.of(18, 30), LocalTime.of(21, 0),
                LocalTime.of(21, 0)
        };

        LocalDate semana = fechaPrimeraVuelta;

        // ===== PRIMERA VUELTA =====
        for (int r = 0; r < jornadas; r++) {

            List<String[]> duelos = new ArrayList<>();
            Equipo fijo = equipos.get(0);

            if (!rota.get(0).getNombre().equals("DESCANSA"))
                duelos.add(new String[]{fijo.getNombre(), rota.get(0).getNombre()});

            for (int i = 1; i < size / 2 + 1; i++) {
                Equipo e1 = rota.get(i);
                Equipo e2 = rota.get(size - i);

                if (!e1.getNombre().equals("DESCANSA") &&
                    !e2.getNombre().equals("DESCANSA"))
                {
                    duelos.add(new String[]{e1.getNombre(), e2.getNombre()});
                }
            }

            List<Match> jornada = new ArrayList<>();

            for (int i = 0; i < duelos.size(); i++) {
                LocalDate fechaPartido =
                        switch (i) {
                            case 0 -> semana;
                            case 1,2,3,4 -> semana.plusDays(1);
                            case 5,6,7,8 -> semana.plusDays(2);
                            default -> semana.plusDays(3);
                        };

                jornada.add(
                        new Match(duelos.get(i)[0], duelos.get(i)[1], fechaPartido, horas[i])
                );
            }

            calendario.add(jornada);

            Collections.rotate(rota, 1);
            semana = semana.plusWeeks(1);
        }

        // ===== SEGUNDA VUELTA =====
        LocalDate semanaVuelta = LocalDate.of(2026, 1, 2);

        List<List<Match>> calendarioVuelta = new ArrayList<>();

        for (List<Match> ida : calendario) {

            List<Match> vuelta = new ArrayList<>();

            for (int i = 0; i < ida.size(); i++) {
                Match m = ida.get(i);

                LocalDate fechaPartido =
                        switch (i) {
                            case 0 -> semanaVuelta;
                            case 1,2,3,4 -> semanaVuelta.plusDays(1);
                            case 5,6,7,8 -> semanaVuelta.plusDays(2);
                            default -> semanaVuelta.plusDays(3);
                        };

                vuelta.add(
                        new Match(m.visitante(), m.local(), fechaPartido, m.hora())
                );
            }

            calendarioVuelta.add(vuelta);

            semanaVuelta = semanaVuelta.plusWeeks(1);
        }

        // AGREGAR SEGUNDA VUELTA SIN MODIFICAR DURANTE EL BUCLE ORIGINAL
        calendario.addAll(calendarioVuelta);

        return calendario;
    }
}
