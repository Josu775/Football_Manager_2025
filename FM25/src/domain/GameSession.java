package domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class GameSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private Equipo jugadorEquipo;
    private LocalDateTime inicio;
    private List<Equipo> liga;
    

    // Manager
    private String managerName;
    private String managerAvatar;

    // Calendario de la temporada: cada Object[] = { jornada(int), rival(String), estadio(String), hora(String), pos(int), local(boolean) }
    private List<List<LeagueCalendar.Match>> calendario;

    // Jornada actual para la simulación partido a partido
    private int jornadaActual = 1;

    public GameSession(Equipo jugadorEquipo, List<Equipo> liga) {
        this.jugadorEquipo = jugadorEquipo;
        this.liga = liga;
        this.inicio = LocalDateTime.now();
        this.calendario = LeagueCalendar.generarCalendario(liga);
    }

    public Equipo getJugadorEquipo() {
        return jugadorEquipo;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public List<Equipo> getLiga() {
        return liga;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerAvatar() {
        return managerAvatar;
    }

    public void setManagerAvatar(String managerAvatar) {
        this.managerAvatar = managerAvatar;
    }

    // ==== Calendario ====
    public List<List<LeagueCalendar.Match>> getCalendario() {
        return calendario;
    }

    public void setCalendario(List<List<LeagueCalendar.Match>> calendario) {
        this.calendario = calendario;
    }

    // ==== Jornada actual ====
    public int getJornadaActual() {
        return jornadaActual;
    }

    public void avanzarJornada() {
        jornadaActual++;
    }

    public void reiniciarJornada() {
        jornadaActual = 1;
    }
}
