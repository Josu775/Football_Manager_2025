package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre; //nombre
    private String ciudad;
    private String estadio;
    private String formacion;
    private double valoracion;
    private double budget;
    private List<Jugador> onceTitular = new ArrayList<>();

    // Estadísticas del equipo (para clasificación)
    private TeamStats stats = new TeamStats();

    public Equipo(String nombre, String ciudad, String estadio, String formacion, double valoracion, double budget) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.estadio = estadio;
        this.formacion = formacion;
        this.valoracion = Math.round(Math.max(1.0, Math.min(5.0, valoracion)) * 10.0) / 10.0;
        this.budget = budget;
    }

    public String getNombre() { return nombre; }
    public String getCiudad() { return ciudad; }
    public String getEstadio() { return estadio; }

    public String getFormacion() { return formacion; }
    public void setFormacion(String formacion) { this.formacion = formacion; }

    public double getValoracion() { return valoracion; }
    public void setValoracion(double valoracion) {
        this.valoracion = Math.round(Math.max(1.0, Math.min(5.0, valoracion)) * 10.0) / 10.0;
    }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public List<Jugador> getOnceTitular() { return onceTitular; }

    public void setOnceTitular(List<Jugador> once) {
        this.onceTitular.clear();
        if (once != null) this.onceTitular.addAll(once);
    }

    // Estadísticas (clasificación)
    public TeamStats getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
