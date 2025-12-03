package io;

import domain.GameSession;

import java.io.*;

public class SaveManager {

    private static final String SAVE_PATH = "resources/saves/savegame.dat";

    // Guarda una sesion del juego completa. 
    public static void guardarPartida(GameSession session) {
        try {
            // === 1. GUARDAR CLASIFICACIÓN ACTUAL EN BD ===
            db.ClasificacionDAO cdao = new db.ClasificacionDAO(db.DataManager.getGestor());
            for (domain.Equipo e : session.getLiga()) {
                cdao.guardarStats(e);
            }

            // === 2. GUARDAR ARCHIVO SAVEGAME ===
            File f = new File(SAVE_PATH);
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
                out.writeObject(session);
            }

            System.out.println("[SAVE] Partida guardada correctamente.");
        } catch (Exception e) {
            System.err.println("[SAVE] Error al guardar la partida");
            e.printStackTrace();
        }
    }

    // Carga la última partida guardada
    public static GameSession cargarPartida() {
        File f = new File(SAVE_PATH);
        if (!f.exists() || f.length() == 0) {
            System.out.println("[SAVE] No hay partida guardada.");
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            GameSession session = (GameSession) in.readObject();
            System.out.println("[SAVE] Partida cargada.");
            return session;
        } catch (Exception e) {
            System.err.println("[SAVE] Error al cargar la partida");
            e.printStackTrace();
            return null;
        }
    }
    
    public static void borrarPartida() {
        File f = new File(SAVE_PATH);
        if (f.exists()) {
            f.delete();
            System.out.println("[SAVE] Partida eliminada.");
        }
    }

}
