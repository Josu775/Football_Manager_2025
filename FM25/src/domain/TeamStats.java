package domain;

import java.io.Serializable;

public class TeamStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private int puntos;
    private int gf;
    private int gc;


    public TeamStats() {
        this.puntos = 0;
        this.gf = 0;
        this.gc = 0;
    }

    public int getPuntos() { return puntos; }
    public int getGf() { return gf; }
    public int getGc() { return gc; }
    public int getDg() { return gf - gc; }

    //actualiza las esstadisticas tras un partido
    //gf = goles a favot
    //gc = goles en contra
    
    public void addPartido(int gf, int gc) {
        this.gf += gf;
        this.gc += gc;
        if (gf > gc) {
            puntos += 3;
        } else if (gf == gc) {
            puntos += 1;
        }
    }

    //reiniciar la temporada
    public void reset() {
        this.puntos = 0;
        this.gf = 0;
        this.gc = 0;
    }
    
    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public void setGf(int gf) {
        this.gf = gf;
    }

    public void setGc(int gc) {
        this.gc = gc;
    }

}
