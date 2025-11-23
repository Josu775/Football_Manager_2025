package domain;

import java.util.Random;

/**
 * Jugador con valoracion en escala 0.0 - 5.0 (double, 1 decimal).
 * Ahora también admite jugadores cargados desde CSV reales.
 */
public class Jugador {

    private static final Random RNG = new Random();

    private String nombre;
    private String posicion;
    private int edad;
    private double valoracion;
    private String equipo;   // nombre del equipo al que pertenece (para el CSV)

    // Constructor original (inventados / mercado)
    public Jugador(String nombre, String posicion, int edad, double valoracion) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.edad = edad;
        setValoracion(valoracion);
    }

    // Constructor nuevo para CSV (nombre, posición, equipo)
    public Jugador(String nombre, String posicion, String equipo) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.equipo = equipo;

        this.edad = 18 + RNG.nextInt(17);  // 18-34

        // Rating base para jugadores reales, 1.5 - 5.0
        double base = 1.5 + RNG.nextDouble() * 3.5;
        setValoracion(base);
    }

    public String getNombre() { return nombre; }
    public String getPosicion() { return posicion; }
    public int getEdad() { return edad; }
    public String getEquipo() { return equipo; }

    public double getValoracion() { return valoracion; }

    public void setValoracion(double nueva) {
        double v = Math.max(0.0, Math.min(5.0, nueva));
        this.valoracion = Math.round(v * 10.0) / 10.0;
    }

    @Override
    public String toString() {
        return nombre + " (" + posicion + ", " + edad + " años, "
                + String.format("%.1f", valoracion) + ")";
    }
}
