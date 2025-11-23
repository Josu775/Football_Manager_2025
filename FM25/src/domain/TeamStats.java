package domain;

public class TeamStats {

    private int puntos;
    private int gf;
    private int gc;

    public TeamStats() {
        this.puntos = 0;
        this.gf = 0;
        this.gc = 0;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getGf() {
        return gf;
    }

    public int getGc() {
        return gc;
    }

    public int getDg() {
        return gf - gc;
    }

    /**
     * Actualiza estadísticas tras un partido.
     * gf = goles marcados por este equipo
     * gc = goles recibidos por este equipo
     */
    public void addPartido(int gf, int gc) {
        this.gf += gf;
        this.gc += gc;

        if (gf > gc) {
            puntos += 3;
        } else if (gf == gc) {
            puntos += 1;
        }
    }
}
