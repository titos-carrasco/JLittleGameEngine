package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Clase para manejar GameObjects animados
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 */
public class Sprite extends GameObject {
    BufferedImage[] surfaces = null;
    String iname = null;
    int idx = 0;
    double elapsed = 0;

    /**
     * Crea un GameObject animado con la secuencia de imagenes a utilizar a ser
     * especificada posteriormente
     *
     * @param position posicion inicial (x, y) del GameObject
     */
    public Sprite(Point position) {
        this(null, position, null);
    }

    /**
     * Crea un GameObject animado con la secuencia de imagenes a utilizar
     *
     * @param iname    nombre de la secuencia de imagenes a utilizar
     * @param position posicion inicial (x, y) del GameObject
     */
    public Sprite(String iname, Point position) {
        this(iname, position, null);
    }

    /**
     * Crea un GameObject animado con la secuencia de imagenes a utilizar
     *
     * @param iname    nombre de la secuencia de imagenes a utilizar
     * @param position posicion inicial (x, y) del GameObject
     * @param name     nombre a asignar a este GameObject
     */
    public Sprite(String iname, Point position, String name) {
        super(position, new Dimension(0, 0), name);
        setImage(iname);
    }

    /**
     * Retorna el nombre de la secuencia actual de imagenes que utiliza este Sprite
     *
     * @return el nombre de la secuencia
     */
    public String getImagesName() {
        return iname;
    }

    /**
     * Retorna el indice de la secuencia actual de imagenes que utiliza este Sprite
     *
     * @return el numero de la imagen dentro de la secuencia actual
     */
    public int getImagesIndex() {
        return idx;
    }

    /**
     * Avanza automaticamente a la siguiente imagen de la secuencia de este Sprite
     */
    public int nextImage() {
        return nextImage(0, 0);
    }

    /**
     * Avanza automaticamente a la siguiente imagen de la secuencia de este Sprite
     *
     * @param dt tiempo transcurrido desde la ultima invocacion a este metodo
     */
    public int nextImage(double dt) {
        return nextImage(dt, 0);
    }

    /**
     * Avanza automaticamente a la siguiente imagen de la secuencia de este Sprite
     *
     * @param dt    tiempo transcurrido desde la ultima invocacion a este metodo
     * @param delay tiempo que debe transcurrir antes de pasar a la siguiente imagen
     *              de la secuencia
     */
    public int nextImage(double dt, double delay) {
        elapsed = elapsed + dt;
        if (elapsed < delay)
            return idx;

        elapsed = 0;
        idx = idx + 1;
        if (idx >= surfaces.length)
            idx = 0;

        surface = surfaces[idx];
        this.rect.setSize(this.surface.getWidth(), this.surface.getHeight());
        setCollider(new Rectangle(0, 0, this.rect.width, this.rect.height));
        return idx;
    }

    /**
     * Establece la secuencia de imagenes a utilizar en este Sprite
     *
     * @param iname el nombre de la secuencia (cargada con LoadImage y especificada
     *              al crear este Sprite)
     */
    public int setImage(String iname) {
        return setImage(iname, 0);
    }

    /**
     * Establece la secuencia de imagenes a utilizar en este Sprite
     *
     * @param iname el nombre de la secuencia (cargada con LoadImage y especificada
     *              al crear este Sprite)
     * @param idx   el numero de la secuencia a utilizar
     */
    public int setImage(String iname, int idx) {
        if (iname != null) {
            if (!iname.equals(this.iname)) {
                surfaces = LittleGameEngine.getInstance().getImages(iname);
                this.iname = iname;
            }

            if (idx >= surfaces.length)
                idx = 0;
            this.idx = idx;

            surface = surfaces[idx];
            this.rect.setSize(this.surface.getWidth(), this.surface.getHeight());
            setCollider(new Rectangle(0, 0, this.rect.width, this.rect.height));
        }

        return this.idx;
    }
}
