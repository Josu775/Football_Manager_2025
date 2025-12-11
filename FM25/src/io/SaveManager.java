package io;

import domain.GameSession;
import java.io.*;

public class SaveManager {

    private static final String SAVE_DIR = "resources/saves/";
    private static final String FILE_PREFIX = "slot";
    private static final String EXT = ".dat";

    // Devuelve ruta del slot
    private static String getPath(int slot) {
        return SAVE_DIR + FILE_PREFIX + slot + EXT;
    }

    // === GUARDAR SLOT ===
    public static void guardarPartida(GameSession session, int slot) {

        try {
            File folder = new File(SAVE_DIR);
            if (!folder.exists()) folder.mkdirs();

            File f = new File(getPath(slot));
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
                out.writeObject(session);
            }

            System.out.println("[SAVE] Guardado en slot " + slot);

        } catch (Exception e) {
            System.err.println("[SAVE] Error al guardar slot " + slot);
            e.printStackTrace();
        }
    }

    // === CARGAR SLOT ===
    public static GameSession cargarPartida(int slot) {
        File f = new File(getPath(slot));
        if (!f.exists()) return null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            return (GameSession) in.readObject();
        } catch (Exception e) {
            System.err.println("[SAVE] Error al cargar slot " + slot);
            return null;
        }
    }

    // === BORRAR SLOT ===
    public static void borrarSlot(int slot) {
        File f = new File(getPath(slot));
        if (f.exists()) f.delete();
    }
}
