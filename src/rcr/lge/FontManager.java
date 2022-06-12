package rcr.lge;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

/**
 * Manejador de Tipos de Letras en memoria
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 */

public class FontManager {
    private final HashMap<String, Font> fonts;

    /**
     * Construye una objeto manejador de tipos de letra en memoria
     */
    public FontManager() {
        fonts = new HashMap<String, Font>();
    }

    /**
     * Carga un tipo de letra para ser utilizado en el juego
     *
     * @param name   nombre interno a asignar
     * @param fname  nombre del tipo de letra
     * @param fstyle estilo del tipo de letra
     * @param fsize  tamano del tipo de letra
     */
    public void loadSysFont(String name, String fname, int fstyle, int fsize) {
        if (fonts.get(name) == null) {
            Font f = new Font(fname, fstyle, fsize);
            fonts.put(name, f);
        }
    }

    /**
     * Carga un tipo de letra True Type para ser utilizado en el juego
     *
     * @param name   nombre interno a asignar
     * @param fname  nombre del archivo que contiene la fuente TTF
     * @param fstyle estilo del tipo de letra
     * @param fsize  tamano del tipo de letra
     */
    public void loadTTFont(String name, String fname, int fstyle, int fsize) {
        try {
            if (fname.charAt(2) == ':')
                fname = fname.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        if (fonts.get(name) == null) {
            Font font = null;
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try {
                File file = new File(fname);
                font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            Font f = font.deriveFont(fstyle, fsize);
            ge.registerFont(f);
            fonts.put(name, f);
        }
    }

    /**
     * Recupera un tipo de letra previamente cargado
     *
     * @param fname el nombre del tipo de letra a recuperar
     *
     * @return el tipo de letra
     */
    public Font getFont(String fname) {
        return fonts.get(fname);
    }

    /**
     * Obtiene los tipos de letra del sistema
     *
     * @return los tipos de letra
     */
    static public String[] getSysFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();

        String[] sysFonts = new String[fonts.length];
        for (int i = 0; i < fonts.length; i++)
            sysFonts[i] = fonts[i].getFontName();
        return sysFonts;
    }

}
