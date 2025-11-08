package domain;

/**
 * Jugador con valoracion en escala 0.0 - 5.0 (double, 1 decimal).
 */
public class Jugador {
    private String nombre;
    private String posicion;
    private int edad;
    private double valoracion; 

    public Jugador(String nombre, String posicion, int edad, double valoracion) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.edad = edad;
        setValoracion(valoracion); 
    }

    public String getNombre() { return nombre; }
    public String getPosicion() { return posicion; }
    public int getEdad() { return edad; }

    
    public double getValoracion() { return valoracion; }

    
    public void setValoracion(double nueva) {
        double v = Math.max(0.0, Math.min(5.0, nueva));
        this.valoracion = Math.round(v * 10.0) / 10.0;
    }

    @Override
    public String toString() {
        return nombre + " (" + posicion + ", " + edad + " años, " + String.format("%.1f", valoracion) + ")";
    }
}
