package rcr.lge;

import javax.sound.sampled.Clip;

/**
 * Clase para almacener un Clip de Audio en un contenedor
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 *
 */
public class ClipData {
    Clip clip = null;
    byte[] data = null;
    double level = 50.0;

    public ClipData() {
    }
}
