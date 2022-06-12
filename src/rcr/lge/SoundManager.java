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
    private final ArrayList<Thread> threads;

    /**
     * Construye un objeto manejador de sonidos en memoria
     */
    public SoundManager() {
        sounds = new HashMap<String, ClipData>();
        threads = new ArrayList<Thread>();
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

        if (fname.charAt(2) == ':')
            fname = fname.substring(1);

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
     */
    public void playSound(String name, boolean loop) {
        ClipData clipData = sounds.get(name);

        if (clipData == null)
            return;

        new Thread() {
            public void run() {
                synchronized (threads) {
                    threads.add(this);
                }
                LittleGameEngine lge = LittleGameEngine.getInstance();
                AudioFormat format = clipData.format;

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine line = null;
                try {
                    line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(format, format.getFrameSize() * 128);
                } catch (LineUnavailableException e1) {
                    e1.printStackTrace();
                    return;
                }

                line.start();
                ByteArrayInputStream bais = new ByteArrayInputStream(clipData.data);
                int total = bais.available();
                while (lge.running) {
                    int pos = 0;
                    while (lge.running && pos < total) {
                        int n = format.getFrameSize() * 128;
                        if (n > total - pos)
                            n = total - pos;
                        int nb = line.write(clipData.data, pos, n);
                        pos += nb;
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                        }
                    }
                    if (!loop)
                        break;
                }
                // line.flush();
                // line.stop();
                line.close();
                try {
                    bais.close();
                } catch (IOException e) {
                }

                bais = null;
                line = null;
                info = null;

                synchronized (threads) {
                    threads.remove(this);
                }

            }
        }.start();
    }

    /**
     * Detiene la reproduccion de todos los sonidos
     *
     * @param name el nombre del sonido a detener
     */
    public void stopAll() {
        ArrayList<Thread> tsounds = null;

        synchronized (threads) {
            tsounds = new ArrayList<Thread>(threads);
        }

        for (Thread t : tsounds)
            try {
                t.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

}
