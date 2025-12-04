package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeagueCalendar {

    // ======= CAMBIO IMPORTANTE =======
    public record Match(String local, String visitante) implements Serializable {
        private static final long serialVersionUID = 1L;
    }
    // ==================================

    public static List<List<Match>> generarCalendario(List<Equipo> equipos) {
        int n = equipos.size();

        // Si es impar, añadimos un "fantasma"
        boolean impar = (n % 2 != 0);
        if (impar) {
            equipos = new ArrayList<>(equipos);
            equipos.add(new Equipo("DESCANSA", "", "", "", 0, 0));
            n++;
        }

        List<Equipo> rota = new ArrayList<>(equipos);
        rota.remove(0); // el primero fija
        int size = rota.size();

        int jornadas = n - 1;

        List<List<Match>> calendario = new ArrayList<>();

        for (int r = 0; r < jornadas; r++) {
            List<Match> jornada = new ArrayList<>();

            Equipo fijo = equipos.get(0);

            // partido del fijo
            if (!rota.get(0).getNombre().equals("DESCANSA"))
                jornada.add(new Match(fijo.getNombre(), rota.get(0).getNombre()));

            // resto
            for (int i = 1; i < size / 2 + 1; i++) {
                Equipo e1 = rota.get(i);
                Equipo e2 = rota.get(size - i);

                if (!e1.getNombre().equals("DESCANSA") && !e2.getNombre().equals("DESCANSA")) {
                    jornada.add(new Match(e1.getNombre(), e2.getNombre()));
                }
            }

            calendario.add(jornada);

            // ROTACIÓN
            Collections.rotate(rota, 1);
        }

        // VUELTA (cambiamos local/visitante)
        List<List<Match>> vuelta = new ArrayList<>();
        for (List<Match> j : calendario) {
            List<Match> j2 = new ArrayList<>();
            for (Match m : j) j2.add(new Match(m.visitante(), m.local()));
            vuelta.add(j2);
        }

        calendario.addAll(vuelta);

        return calendario;
    }
}
