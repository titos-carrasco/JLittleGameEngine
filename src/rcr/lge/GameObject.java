package rcr.lge;

import java.awt.image.BufferedImage;
import java.util.UUID;

/**
 * Objeto base del juego. En Little Game Engine casi todo es un GameObject
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 */
public class GameObject {
    RectangleD rect;
    RectangleD[] collider;
    String name;
    BufferedImage surface = null;
    RectangleD bounds = null;
    String tag = "";
    boolean useColliders = false;
    boolean callOnCollision = false;
    int layer = -1;

    /**
     * Crea un objeto del juego
     *
     * @param origin posicion inicial (x,y) del GameObject
     * @param size   dimension (ancho,alto) del GameObject
     */
    public GameObject(PointD origin, Size size) {
        this(origin.x, origin.y, size.width, size.height, null);
    }

    /**
     * Crea un objeto del juego
     *
     * @param origin posicion inicial (x,y) del GameObject
     * @param size   dimension (ancho,alto) del GameObject
     * @param name   nombre unico para este GameObject
     */
    public GameObject(PointD origin, Size size, String name) {
        this(origin.x, origin.y, size.width, size.height, name);
    }

    /**
     * Crea un objeto del juego
     *
     * @param x      posicion inicial en X del GameObject
     * @param y      posicion inicial en X del GameObject
     * @param width  ancho del GameObject
     * @param height alto del GameObject
     */
    public GameObject(double x, double y, int width, int height) {
        this(x, y, width, height, null);
    }

    /**
     * Crea un objeto del juego
     *
     * @param x      posicion inicial en X del GameObject
     * @param y      posicion inicial en X del GameObject
     * @param width  ancho del GameObject
     * @param height alto del GameObject
     * @param name   nombre unico para este GameObject
     */
    public GameObject(double x, double y, int width, int height, String name) {
        rect = new RectangleD(x, y, width, height);
        if (name == null)
            name = "__no_name__" + UUID.randomUUID().toString();
        this.name = name;
        setCollider(new RectangleD(0, 0, width, height));
    }

    /**
     * Retorna la posicion de este objeto
     *
     * @return la posicion
     */
    public PointD getPosition() {
        return rect.getOrigin();
    }

    /**
     * Obtiene la coordenada X del GameObjec
     *
     * @return la coordenada X
     */
    public double getX() {
        return rect.x;
    }

    /**
     * Obtiene la coordenada Y del GameObject
     *
     * @return la coordenada Y
     */
    public double getY() {
        return rect.y;
    }

    /**
     * Retorna la dimension de este objeto
     *
     * @return la dimension
     */
    public Size getSize() {
        return rect.getSize();
    }

    /**
     * Retorna el ancho de este objeto
     *
     * @return el ancho
     */
    public int getWidth() {
        return rect.width;
    }

    /**
     * Retorna el alto de este objeto
     *
     * @return el alto
     */
    public int getHeight() {
        return rect.height;
    }

    /**
     * Retorna una copia del rectangulo que rodea a este objeto
     *
     * @return el rectangulo
     */
    public RectangleD getRectangle() {
        return new RectangleD(rect);
    }

    /**
     * Retorna el nombre de este objeto
     *
     * @return el nombre
     */
    public String getName() {
        return name;
    }

    /**
     * Retorna el layer de este objeto
     *
     * @return el layer
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Retorna el TAG de este objeto
     *
     * @return el tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Retorna el colisionador de este objeto
     *
     * @return los rectangulos que definen su colisionador
     */
    public RectangleD[] getCollider() {
        int l = collider.length;
        RectangleD[] rects = new RectangleD[l];

        for (int i = 0; i < l; i++) {
            RectangleD r = new RectangleD(collider[i]);
            r.x += rect.x;
            r.y += rect.y;
            rects[i] = r;
        }
        return rects;
    }

    /**
     * Establece el rectangulo que limita el movimiento de este objeto
     *
     * @param bounds el rectangulo en donde se permitira mover al objeto
     */
    public void setBounds(RectangleD bounds) {
        this.bounds = new RectangleD(bounds);
    }

    /**
     * Establece la posicion de este objeto
     *
     * @param position la posicion (x, y)
     */
    public void setPosition(PointD position) {
        setPosition(position.x, position.y);
    }

    /**
     * Establece la posicion de este objeto
     *
     * @param x la cordenada x
     * @param y la coordenada y
     */
    public void setPosition(double x, double y) {
        rect.x = x;
        rect.y = y;

        if (bounds == null)
            return;

        if (rect.width <= bounds.width && rect.height <= bounds.height) {
            if (rect.x < bounds.x)
                rect.x = bounds.x;
            else if (rect.x + rect.width >= bounds.x + bounds.width)
                rect.x = bounds.x + bounds.width - rect.width;
            if (rect.y < bounds.y)
                rect.y = bounds.y;
            else if (rect.y + rect.height >= bounds.y + bounds.height)
                rect.y = bounds.y + bounds.height - rect.height;
        }
    }

    /**
     * Establece el TAG para este objeto
     *
     * @param tag el tag a asignar
     */
    public void setTag(String tag) {
        this.tag = new String(tag);
    }

    /**
     * Establece el colisionador para este objeto
     *
     * @param rect el rectangulo que define la zona de colision
     */
    public void setCollider(RectangleD rect) {
        collider = new RectangleD[] { new RectangleD(rect) };
    }

    /**
     * Establece el colisionador para este objeto
     *
     * @param rects los rectangulos que definen la zona de colision
     */
    public void setCollider(RectangleD[] rects) {
        int l = rects.length;
        collider = new RectangleD[l];
        for (int i = 0; i < l; i++)
            collider[i] = new RectangleD(rects[i]);
    }

    /**
     * Establece si este objeto participara o no del procesamiento de colisiones
     *
     * @param useColliders si es verdadero participara del procesamiento de
     *                     colisiones
     */
    public void enableCollider(boolean useColliders) {
        this.useColliders = useColliders;
        this.callOnCollision = false;
    }

    /**
     * Establece si este objeto participara o no del procesamiento de colisiones
     *
     * @param useColliders si es verdadero participara del procesamiento de
     *                     colisiones
     * @param oncollision  si es verdadero se generara el evento OnCollision para
     *                     este objeto
     */
    public void enableCollider(boolean useColliders, boolean oncollision) {
        this.useColliders = useColliders;
        this.callOnCollision = oncollision;
    }

    /**
     * Determina si un GameObject colisiona con otro
     *
     * @param gobj el gobject a comparar
     * @return Verdadero si colisiona con el GameObject especificado
     */
    public boolean collidesWith(GameObject gobj) {
        if (layer == gobj.layer)
            for (RectangleD r1 : getCollider())
                for (RectangleD r2 : gobj.getCollider())
                    if (r1.intersects(r2))
                        return true;
        return false;
    }

    // manejo de eventos

    /**
     * Es invocada en el siguiente ciclo para todos los GameObjects marcados para
     * eliminacion
     */
    public void onDelete() {
    }

    /**
     * Es invocada en el siguiente ciclo para todos los GameObjects recien creados
     */
    public void onStart() {
    }

    /**
     * Es invocada en el siguiente ciclo para todos los GameObjects previo al evento
     * onUpdate()
     *
     * @param dt tiempo en segundos desde el ultimo ciclo
     */
    public void onPreUpdate(double dt) {
    }

    /**
     * Es invocada en el siguiente ciclo para todos los GameObjects previo al evento
     * onPostUpdate()
     *
     * @param dt tiempo en segundos desde el ultimo ciclo
     */
    public void onUpdate(double dt) {
    }

    /**
     * Es invocada en el siguiente ciclo para todos los GameObjects previo al evento
     * onCollision()
     *
     * @param dt tiempo en segundos desde el ultimo ciclo
     */
    public void onPostUpdate(double dt) {
    }

    /**
     * Es invocada en el siguiente ciclo para todos los GameObjects previo al evento
     * onPreRender()
     *
     * @param dt    tiempo en segundos desde el ultimo ciclo
     * @param gobjs los GameObjects que colisionan co este GameObject
     */
    public void onCollision(double dt, GameObject[] gobjs) {
    }

    /**
     * Es invocada en el siguiente ciclo para todos los GameObjects previo al
     * rendering del juego en pantalla
     *
     * @param dt tiempo en segundos desde el ultimo ciclo
     */
    public void onPreRender(double dt) {
    }

    /**
     * Es invocada para todos los GameObjects justo antes de finalizar el game loop
     */
    public void onQuit() {
    }

}
