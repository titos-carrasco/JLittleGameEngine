package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.UUID;

/**
 * Objeto base del juego. En Little Game Engine casi todo es un GameObject
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 */
public class GameObject {
    Rectangle rect;
    Rectangle[] collider;
    String name;
    BufferedImage surface = null;
    Rectangle bounds = null;
    String tag = "";
    boolean useColliders = false;
    int layer = -1;
    int onEventsEnabled = 0x00;

    /**
     * Crea un objeto del juego
     *
     * @param origin posicion inicial (x,y) del GameObject
     * @param size   dimension (ancho,alto) del GameObject
     */
    public GameObject(Point origin, Dimension size) {
        this(origin.x, origin.y, size.width, size.height, null);
    }

    /**
     * Crea un objeto del juego
     *
     * @param origin posicion inicial (x,y) del GameObject
     * @param size   dimension (ancho,alto) del GameObject
     * @param name   nombre unico para este GameObject
     */
    public GameObject(Point origin, Dimension size, String name) {
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
    public GameObject(int x, int y, int width, int height) {
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
    public GameObject(int x, int y, int width, int height, String name) {
        rect = new Rectangle(x, y, width, height);
        if (name == null)
            name = "__no_name__" + UUID.randomUUID().toString();
        this.name = name;
        setCollider(new Rectangle(0, 0, width, height));
    }

    /**
     * Retorna la posicion de este objeto
     *
     * @return la posicion
     */
    public Point getPosition() {
        return new Point(rect.getLocation());
    }

    /**
     * Obtiene la coordenada X del GameObjec
     *
     * @return la coordenada X
     */
    public int getX() {
        return rect.x;
    }

    /**
     * Obtiene la coordenada Y del GameObject
     *
     * @return la coordenada Y
     */
    public int getY() {
        return rect.y;
    }

    /**
     * Retorna la dimension de este objeto
     *
     * @return la dimension
     */
    public Dimension getSize() {
        return new Dimension(rect.getSize());
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
    public Rectangle getRectangle() {
        return new Rectangle(rect);
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
     * Retorna el TAG de este objeto
     *
     * @return el tag
     */
    public String getTag() {
        return tag;
    }

    public Rectangle[] getCollider() {
        int l = collider.length;
        Rectangle[] rects = new Rectangle[l];

        for (int i = 0; i < l; i++) {
            Rectangle r = new Rectangle(collider[i]);
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
    public void setBounds(Rectangle bounds) {
        this.bounds = new Rectangle(bounds);
    }

    /**
     * Establece la posicion de este objeto
     *
     * @param position la posicion (x, y)
     */
    public void setPosition(Point position) {
        setPosition(position.x, position.y);
    }

    /**
     * Establece la posicion de este objeto
     *
     * @param x la cordenada x
     * @param y la coordenada y
     */
    public void setPosition(int x, int y) {
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
    public void setCollider(Rectangle rect) {
        collider = new Rectangle[] { new Rectangle(rect) };
    }

    /**
     * Establece el colisionador para este objeto
     * 
     * @param rects los rectangulos que definen la zona de colision
     */
    public void setCollider(Rectangle[] rects) {
        int l = rects.length;
        collider = new Rectangle[l];
        for (int i = 0; i < l; i++)
            collider[i] = new Rectangle(rects[i]);
    }

    /**
     * Establece si este objeto participara o no del procesamiento de colisiones
     *
     * @param useColliders si es verdadero participara del procesamiento de
     *                     colisiones
     */
    public void enableCollider(boolean useColliders) {
        this.useColliders = useColliders;
    }

    /**
     * Determina si un GamneObject colisiona con otro
     * 
     * @param gobj el gobject a comparar
     * @return Verdader si colisiona con el GameObject especificado
     */
    public boolean collidesWith(GameObject gobj) {
        if (layer == gobj.layer)
            for (Rectangle r1 : getCollider())
                for (Rectangle r2 : gobj.getCollider())
                    if (r1.intersects(r2))
                        return true;
        return false;
    }

    // manejo de eventos

    /**
     * Establece los eventos que recibira este objeto
     *
     * @param onEventsEnabled el evento que se sumara a los eventos que recibira
     *                        <ul>
     *                        <li>LittleGameEngine.E_ON_DELETE</li>
     *                        <li>LittleGameEngine.E_ON_START</li>
     *                        <li>LittleGameEngine.E_ON_PRE_UPDATE</li>
     *                        <li>LittleGameEngine.E_ON_UPDATE</li>
     *                        <li>LittleGameEngine.E_ON_POST_UPDATE</li>
     *                        <li>LittleGameEngine.E_ON_COLLISION</li>
     *                        <li>LittleGameEngine.E_ON_PRE_RENDER</li>
     *                        <li>LittleGameEngine.E_ON_QUIT</li>
     *                        </ul>
     *
     *                        Se deben sobreescribir los siguientes metodos segun se
     *                        habiliten los eventos: onDelete(), onStart(),
     *                        onPreUpdate(dt), onUpdate(dt), onPostUpdate( dt) ,
     *                        onCollision(dt, gobjs), onPreRender(dt), onQuit()
     */
    public void setOnEvents(int onEventsEnabled) {
        this.onEventsEnabled |= onEventsEnabled;
    }

    /**
     * Si es habilitada, ser� invocada en el siguiente ciclo para todos los
     * GameObjects marcados para eliminaci�n
     */
    public void onDelete() {
    };

    /**
     * Si es habilitada, ser� invocada en el siguiente ciclo para todos los
     * GameObjects recien creados
     */
    public void onStart() {
    };

    /**
     * Si es habilitada, ser� invocada en el siguiente ciclo para todos los
     * GameObjects previo al evento onUpdate()
     *
     * @param dt tiempo en segundos desde el ultimo ciclo
     */
    public void onPreUpdate(double dt) {
    };

    /**
     * Si es habilitada, ser� invocada en el siguiente ciclo para todos los
     * GameObjects previo al evento onPostUpdate()
     *
     * @param dt tiempo en segundos desde el ultimo ciclo
     */
    public void onUpdate(double dt) {
    };

    /**
     * Si es habilitada, ser� invocada en el siguiente ciclo para todos los
     * GameObjects previo al evento onCollision()
     *
     * @param dt tiempo en segundos desde el ultimo ciclo
     */
    public void onPostUpdate(double dt) {
    };

    /**
     * Si es habilitada, ser� invocada en el siguiente ciclo para todos los
     * GameObjects previo al evento onPreRender()
     *
     * @param dt tiempo en segundos desde el ultimo ciclo
     */
    public void onCollision(double dt, GameObject[] gobjs) {
    };

    /**
     * Si es habilitada, ser� invocada en el siguiente ciclo para todos los
     * GameObjects previo al rendering del juego en pantalla
     *
     * @param dt tiempo en segundos desde el ultimo ciclo
     */
    public void onPreRender(double dt) {
    };

    /**
     * Si es habilitada, ser� invocadapara todos los GameObjects jusrto antes de
     * finalizar el game loop
     */
    public void onQuit() {
    };

}
