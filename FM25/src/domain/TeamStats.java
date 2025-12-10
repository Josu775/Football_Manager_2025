package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TeamStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private int puntos;
    private int gf;
    private int gc;
    private int victorias;
    private int empates;
    private int derrotas;
    private List<Character> historial = new ArrayList<>();

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

        char resultado;

        if (gf > gc) {
            victorias++;
            puntos += 3;
            resultado = 'V';
        } else if (gf == gc) {
            empates++;
            puntos += 1;
            resultado = 'E';
        } else {
            derrotas++;
            resultado = 'D';
        }

        // Añadir al inicio de la lista (equivalente a addFirst)
        historial.add(0, resultado);

        // Mantener solo 5 resultados
        if (historial.size() > 5) {
            historial.remove(historial.size() - 1); // equivalente a removeLast
        }
    }




    //reiniciar la temporada
    public void reset() {
        puntos = 0;
        gf = 0;
        gc = 0;
        victorias = 0;
        empates = 0;
        derrotas = 0;
        historial.clear();
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

	public int getVictorias() {
		return victorias;
	}

	public int getEmpates() {
		return empates;
	}

	public int getDerrotas() {
		return derrotas;
	}

	public void setVictorias(int victorias) {
		this.victorias = victorias;
	}

	public void setEmpates(int empates) {
		this.empates = empates;
	}

	public void setDerrotas(int derrotas) {
		this.derrotas = derrotas;
	}

	public List<Character> getHistorial() {
	    return historial;
	}

    

}