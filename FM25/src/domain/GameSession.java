package domain;

import java.time.LocalDateTime;
import java.util.List;

public class GameSession {
    private Equipo jugadorEquipo;
    private LocalDateTime inicio;

    // NUEVO: lista completa de equipos de la liga para esta partida
    private List<Equipo> liga;

    public GameSession(Equipo jugadorEquipo, List<Equipo> liga) {
        this.jugadorEquipo = jugadorEquipo;
        this.inicio = LocalDateTime.now();
        this.liga = liga;
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
}
