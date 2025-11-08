package domain;

import java.time.LocalDateTime;

public class GameSession {
    private Equipo jugadorEquipo;
    private LocalDateTime inicio;

    public GameSession(Equipo jugadorEquipo) {
        this.jugadorEquipo = jugadorEquipo;
        this.inicio = LocalDateTime.now();
    }

    public Equipo getJugadorEquipo() { return jugadorEquipo; }
    public LocalDateTime getInicio() { return inicio; }
}
