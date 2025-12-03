package domain;

import java.io.Serializable;
import java.util.Random;

	//Jugadores con valoracion en una escala de 0 a 5.
	//Admite jugadores cargados desde el CSV, desde la base de datos y los generados para el mercado.

public class Jugador implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Random RNG = new Random();

    private String nombre;
    private String posicion;
    private int edad;
    private double valoracion;
    private String equipo;   // nombre del equipo al que pertenece

    // Constructor para mercado / jugadores inventados (sin equipo inicial)
    public Jugador(String nombre, String posicion, int edad, double valoracion) {
        this(nombre, posicion, edad, valoracion, null);
    }

    // Constructor para cargar desde BD (o para crear con equipo explícito)
    public Jugador(String nombre, String posicion, int edad, double valoracion, String equipo) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.edad = edad;
        this.equipo = equipo;
        setValoracion(valoracion);
    }

    // Constructor para CSV (nombre, posición, equipo) — edad y valoración aleatorias
    public Jugador(String nombre, String posicion, String equipo) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.equipo = equipo;

        this.edad = 18 + RNG.nextInt(17);  // 18-34

        double base = 1.5 + RNG.nextDouble() * 3.5; // 1.5 - 5.0
        setValoracion(base);
    }

    public String getNombre() { return nombre; }
    public String getPosicion() { return posicion; }
    public int getEdad() { return edad; }
    public String getEquipo() { return equipo; }
    public void setEquipo(String equipo) { this.equipo = equipo; }

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
