package rcr.lge;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Manejador de sonidos
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 *
 */
public class SoundManager {
    private final HashMap<String, ClipData> sounds;
    private final ArrayList<Thread> players;

    /**
     * Construye un objeto manejador de sonidos en memoria
     */
    public SoundManager() {
        sounds = new HashMap<String, ClipData>();
        players = new ArrayList<Thread>();
    }

    /**
     * Carga un archivo de sonido para ser utilizado durante el juego
     *
     * @param name  nombre a asignar al sonido
     * @param fname nombre del archivo que contiene el sonido
     */

    public void loadSound(String name, String fname) {
        ClipData clipData = sounds.get(name);
        if (clipData != null)
            return;

        fname = fname.replace('\\', '/');

        byte[] data = null;
        AudioFormat format = null;
        try {
            File file = new File(fname);
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            format = ais.getFormat();
            data = new byte[(int) ais.getFrameLength() * ais.getFormat().getFrameSize()];
            ais.read(data, 0, data.length);
            ais.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        clipData = new ClipData();
        clipData.format = format;
        clipData.data = data;
        sounds.put(name, clipData);
    }

    /**
     * Inicia la reproduccion de un sonido
     *
     * @param name el sonido (previamente cargado) a reproducir
     * @param loop Verdadero para repetir el sonido de manera constante
     * @return el ID del player del sonido a reproducir
     */
    public Object playSound(String name, boolean loop) {
        return playSound(name, loop, 0);
    }

    /**
     * Inicia la reproduccion de un sonido
     *
     * @param name     el sonido (previamente cargado) a reproducir
     * @param loop     Verdadero para repetir el sonido de manera constante
     * @param buffSize Tamano del buffer asociado a la linea
     * @return el ID del player del sonido a reproducir
     */
    public Object playSound(String name, boolean loop, int buffSize) {
        ClipData clipData = sounds.get(name);

        if (clipData == null)
            return null;

        Thread player = new Thread() {
            public void run() {
                synchronized (players) {
                    players.add(this);
                }
                LittleGameEngine lge = LittleGameEngine.getInstance();
                AudioFormat format = clipData.format;
                int _buffSize = buffSize;
                if (buffSize <= 0) {
                    _buffSize = format.getFrameSize() * format.getChannels() * (int) format.getFrameRate();
                    if (!System.getProperty("os.name").startsWith("Windows"))
                        _buffSize /= 200;
                }

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = null;
                try {
                    line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(format, _buffSize);
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                    return;
                }

                line.start();
                ByteArrayInputStream bais = new ByteArrayInputStream(clipData.data);
                int total = bais.available();
                while (lge.running) {
                    int pos = 0;
                    while (lge.running && pos < total) {
                        int n = _buffSize;
                        if (n > total - pos)
                            n = total - pos;
                        int nb = line.write(clipData.data, pos, n);
                        pos += nb;
                    }
                    if (!loop)
                        break;
                }
                line.drain();
                line.close();
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bais = null;
                line = null;
                info = null;

                synchronized (players) {
                    players.remove(this);
                }

            }
        };
        player.start();
        return player;
    }

    /**
     * Detiene la reproduccion del sonido especificado
     *
     * @param player El ID del player del sonido a detene
     */
    public void stopSound(Object player) {
    }

    /**
     * Detiene la reproduccion de todos los sonidos
     *
     * @param name el nombre del sonido a detener
     */
    public void stopAll() {
        ArrayList<Thread> _players = null;

        synchronized (players) {
            _players = new ArrayList<Thread>(players);
        }

        for (Thread t : _players)
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

}
