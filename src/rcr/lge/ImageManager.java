package rcr.lge;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * Manejador de imagenes en memoria
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 */
public class ImageManager {
    private final HashMap<String, BufferedImage[]> images;

    /**
     * Construye un objeto manejador de imagenes en memoria
     */
    public ImageManager() {
        images = new HashMap<String, BufferedImage[]>();
    }

    /**
     * Recupera un grupo de imagenes previamente cargadas
     *
     * @param iname el nombre asignado al grupo de imagenes
     *
     * @return la imagenes
     */
    public BufferedImage[] getImages(String iname) {
        return images.get(iname);
    }

    /**
     * Carga una imagen o grupo de imagenes desde archivos para ser utilizadas en el
     * juego
     *
     * @param iname   nombre a asignar a la imagen o grupo de imagenes cargados
     * @param pattern nombre del archivo de imagenes a cargar. Si contiene un '*' se
     *                cargaran todas las imaganes con igual nombre utilizando dicho
     *                caracter como caracter especial de busqueda (ej.
     *                imagen_0*.png)
     * @param flipX   si es verdadero al imagen se reflejara en el eje X
     * @param flipY   si es verdadero al imagen se reflejara en el eje Y
     */
    public void loadImages(String iname, String pattern, boolean flipX, boolean flipY) {
        loadImages(iname, pattern, 0, null, flipX, flipY);
    }

    /**
     * Carga una imagen o grupo de imagenes desde archivos para ser utilizadas en el
     * juego
     *
     * @param iname   nombre a asignar a la imagen o grupo de imagenes cargados
     * @param pattern nombre del archivo de imagenes a cargar. Si contiene un '*' se
     *                cargaran todas las imaganes con igual nombre utilizando dicho
     *                caracter como caracter especial de busqueda (ej.
     *                imagen_0*.png)
     * @param size    nuevo tamano de la imagen cargada
     * @param flipX   si es verdadero al imagen se reflejara en el eje X
     * @param flipY   si es verdadero al imagen se reflejara en el eje Y
     */
    public void loadImages(String iname, String pattern, Size size, boolean flipX, boolean flipY) {
        loadImages(iname, pattern, 0, size, flipX, flipY);
    }

    /**
     * Carga una imagen o grupo de imagenes desde archivos para ser utilizadas en el
     * juego
     *
     * @param iname   nombre a asignar a la imagen o grupo de imagenes cargados
     * @param pattern nombre del archivo de imagenes a cargar. Si contiene un '*' se
     *                cargaran todas las imaganes con igual nombre utilizando dicho
     *                caracter como caracter especial de busqueda (ej.
     *                imagen_0*.png)
     * @param scale   factor de escala a aplicar a la imagen cargada
     * @param flipX   si es verdadero al imagen se reflejara en el eje X
     * @param flipY   si es verdadero al imagen se reflejara en el eje Y
     */
    public void loadImages(String iname, String pattern, double scale, boolean flipX, boolean flipY) {
        loadImages(iname, pattern, scale, null, flipX, flipY);
    }

    /**
     * Cara una imagen o grupo de imagenes desde archivos para ser utilizadas en el
     * juego
     *
     * @param iname   nombre a asignar a la imagen o grupo de imagenes cargados
     * @param pattern nombre del archivo de imagenes a cargar. Si contiene un '*' se
     *                cargaran todas las imaganes con igual nombre utilizando dicho
     *                caracter como caracter especial de busqueda (ej.
     *                imagen_0*.png)
     * @param scale   factor de escala a aplicar a la imagen cargada
     * @param size    nuevo tamano de la imagen cargada
     * @param flipX   si es verdadero al imagen se reflejara en el eje X
     * @param flipY   si es verdadero al imagen se reflejara en el eje Y
     */
    private void loadImages(String iname, String pattern, double scale, Size size, boolean flipX, boolean flipY) {
        ArrayList<BufferedImage> images = readImages(pattern);

        int nimages = images.size();
        for (int i = 0; i < nimages; i++) {
            BufferedImage img = images.get(i);
            Image image;
            if (size != null) {
                image = img.getScaledInstance(size.width, size.height, BufferedImage.SCALE_SMOOTH);
                img.flush();
            } else if (scale > 0) {
                int width = (int) Math.round(img.getWidth() * scale);
                int height = (int) Math.round(img.getHeight() * scale);
                image = img.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
                img.flush();
            } else
                image = img;

            int w = image.getWidth(null);
            int h = image.getHeight(null);
            BufferedImage bi = createTranslucentImage(w, h);
            Graphics2D g2d = bi.createGraphics();

            if (flipX)
                g2d.drawImage(image, w, 0, -w, h, null);
            if (flipY)
                g2d.drawImage(image, 0, h, w, -h, null);
            if (!flipX && !flipY)
                g2d.drawImage(image, 0, 0, null);

            g2d.dispose();
            images.set(i, bi);

            image.flush();
        }

        this.images.put(iname, images.toArray(new BufferedImage[images.size()]));
    }

    /**
     * Carga una imagen o grupo de imagenes acorde al nombre de archivo dado
     *
     * @param pattern patron de busqueda de el o los archivos. El caracter '*' es
     *                usado como comodin
     *
     * @return la o las imagenes cargadas
     */
    private ArrayList<BufferedImage> readImages(String pattern) {
        pattern = pattern.replace('\\', '/');
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

        String dir = "";
        int pos = pattern.lastIndexOf('/');
        if (pos > -1) {
            dir = pattern.substring(0, pos);
            pattern = pattern.substring(pos + 1);
        }

        ArrayList<String> fnames = new ArrayList<String>();
        Path p = Paths.get(dir);

        DirectoryStream<Path> paths = null;
        try {
            paths = Files.newDirectoryStream(p, pattern);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        for (Path path : paths)
            fnames.add(path.toString());

        Collections.sort(fnames);
        for (String fname : fnames) {
            BufferedImage img = readImage(fname);
            images.add(img);
        }
        return images;
    }

    /**
     * Carga una imagen desde el archivo especificado
     *
     * @param fname el archivo que contiene la imagen
     *
     * @return la imagen
     */
    private BufferedImage readImage(String fname) {
        File f = new File(fname);
        BufferedImage img = null;
        try {
            img = ImageIO.read(f);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return img;
    }

    /**
     * Crea una imagen sin transparencia de dimensiones dadas
     *
     * @param width  ancho deseado
     * @param height alto deseado
     *
     * @return la imagen creada
     */
    static public BufferedImage createOpaqueImage(int width, int height) {
        GraphicsConfiguration gconfig;
        gconfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        return gconfig.createCompatibleImage(width, height, Transparency.OPAQUE);
    }

    /**
     * Crea una imagen con transparencia de dimensiones dadas
     *
     * @param width  ancho deseado
     * @param height alto deseado
     *
     * @return la imagen creada
     */
    static public BufferedImage createTranslucentImage(int width, int height) {
        GraphicsConfiguration gconfig;
        gconfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        return gconfig.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

}
