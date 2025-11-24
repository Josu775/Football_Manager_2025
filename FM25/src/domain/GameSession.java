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

    public GameSession(Equipo jugadorEquipo, List<Equipo> liga) {
        this.jugadorEquipo = jugadorEquipo;
        this.liga = liga;
        this.inicio = LocalDateTime.now();
    }

    public Equipo getJugadorEquipo() { return jugadorEquipo; }
    public LocalDateTime getInicio() { return inicio; }
    public List<Equipo> getLiga() { return liga; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getManagerAvatar() { return managerAvatar; }
    public void setManagerAvatar(String managerAvatar) { this.managerAvatar = managerAvatar; }
}
