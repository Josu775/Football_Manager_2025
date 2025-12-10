package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String ciudad;
    private String estadio;
    private String formacion;
    private double valoracion;
    private double budget;

    private List<Jugador> plantilla = new ArrayList<>();

    private TeamStats stats = new TeamStats();

    public Equipo(String nombre, String ciudad, String estadio, String formacion, double valoracion, double budget) {
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.estadio = estadio;
        this.formacion = formacion;
        this.valoracion = valoracion;
        this.budget = budget;
    }

    public String getNombre() { return nombre; }
    public String getCiudad() { return ciudad; }
    public String getEstadio() { return estadio; }

    public String getFormacion() { return formacion; }
    public void setFormacion(String formacion) { this.formacion = formacion; }

    public double getValoracion() { return valoracion; }
    public void setValoracion(double valoracion) {
    	this.valoracion = valoracion;
    }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public List<Jugador> getPlantilla() { return plantilla; }

    public TeamStats getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return nombre;
    }

    // Valoración real según los 11 mejores
    public double calcularValoracionReal() {
        List<Jugador> once = getOnceTitularReal();

        double sum = 0;
        for (Jugador j : once)
            sum += j.getValoracion();

        return sum / once.size();
    }

    // NUEVO — Elegir 11 jugadores mejores
    public List<Jugador> getOnceTitularReal() {

        if (plantilla.size() <= 11)
            return new ArrayList<>(plantilla);

        return plantilla.stream()
                .sorted((a, b) -> Double.compare(b.getValoracion(), a.getValoracion()))
                .limit(11)
                .toList();
    }
    
}
