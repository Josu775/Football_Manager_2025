package io;

import domain.Equipo;
import domain.GameSession;

import java.io.*;

public class SaveManager {

    private static final String SAVE_PATH = "resources/saves/savegame.dat";

    /** Guarda una sesión de juego completa */
    public static void guardarPartida(GameSession session) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_PATH))) {
            out.writeObject(session);
            System.out.println("[SAVE] Partida guardada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Carga la última partida guardada */
    public static GameSession cargarPartida() {
        File f = new File(SAVE_PATH);
        if (!f.exists() || f.length() == 0) {
            System.out.println("[SAVE] No hay partida guardada.");
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_PATH))) {
            GameSession session = (GameSession) in.readObject();
            System.out.println("[SAVE] Partida cargada.");
            return session;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
