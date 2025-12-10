package domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
public class GameSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private Equipo jugadorEquipo;
    private LocalDateTime inicio;
    private List<Equipo> liga;
    
    
    private Map<Integer, LocalDate> fechasJornadas = new HashMap<>();

    public Map<Integer, LocalDate> getFechasJornadas() {
        return fechasJornadas;
    }

    public void setFechasJornadas(Map<Integer, LocalDate> fechasJornadas) {
        this.fechasJornadas = fechasJornadas;
    }

    // Manager
    private String managerName;
    private String managerAvatar;

    // Calendario de la temporada: cada Object[] = { jornada(int), rival(String), estadio(String), hora(String), pos(int), local(boolean) }
    private List<List<LeagueCalendar.Match>> calendario;

    // Jornada actual para la simulación partido a partido
    private int jornadaActual = 1;

    public GameSession(Equipo jugadorEquipoOriginal, List<Equipo> ligaOriginal) {

        // 1º Copia profunda de la liga
    	
        this.liga = new java.util.ArrayList<>();
        for (Equipo e : ligaOriginal) {
            this.liga.add(e); // (si Equipo es mutable, pero serializable, esto vale)
        }

        // 2️º Copia del equipo del jugador (importante)
        
        this.jugadorEquipo = null;
        for (Equipo e : this.liga) {
            if (e.getNombre().equals(jugadorEquipoOriginal.getNombre())) {
                this.jugadorEquipo = e;
                break;
            }
        }

        this.inicio = LocalDateTime.now();

        // 3️º Generar calendario SOLO con la copia de liga, nunca la original
        
        this.calendario = LeagueCalendar.generarCalendario(this.liga);

        // 4️º Jornada inicial
        
        this.jornadaActual = 1;

        // 5️⃣ Inicializamos fechas por defecto (vacías)
        
        this.fechasJornadas = new HashMap<>();
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
