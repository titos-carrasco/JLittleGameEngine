package rcr.lge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.DoubleConsumer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * La Pequena Maquina de Juegos
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 */
public class LittleGameEngine extends JPanel implements KeyListener, MouseListener, WindowListener {
    private static final long serialVersionUID = 6162190111045934737L;

    public static final int GUI_LAYER = 0xFFFF;

    private static LittleGameEngine lge = null;
    boolean running = false;

    private TreeMap<Integer, ArrayList<GameObject>> gLayers;
    private HashMap<String, GameObject> gObjects;
    private ArrayList<GameObject> gObjectsToAdd;
    private ArrayList<GameObject> gObjectsToDel;
    private Camera camera;

    private Size winSize;
    private double[] fpsData;
    private int fpsIdx;
    private double[] lpsData;
    private int lpsIdx;

    public DoubleConsumer onMainUpdate = null;
    private HashMap<Integer, Boolean> keysPressed;
    private boolean[] mouseButtons;
    private Point[] mouseClicks;

    public final ImageManager imageManager;
    public final FontManager fontManager;
    public final SoundManager soundManager;

    private Color bgColor;
    private Color collidersColor = null;

    private JFrame win;
    private BufferedImage screen;
    private long screenTime = 0;

    // ------ game engine ------
    /**
     * Crea el juego
     *
     * @param winSize dimensiones de la ventana de despliegue
     * @param title   titulo de la ventana
     * @param bgColor color de fondo de la ventana
     */
    public LittleGameEngine(Size winSize, String title, Color bgColor) {
        if (lge != null) {
            System.out.println("LittleGameEngine ya se encuentra activa");
            System.exit(ERROR);
        }

        lge = this;
        this.winSize = winSize;

        fpsData = new double[10];
        fpsIdx = 0;
        lpsData = new double[10];
        lpsIdx = 0;

        this.bgColor = bgColor;

        imageManager = new ImageManager();
        fontManager = new FontManager();
        soundManager = new SoundManager();

        keysPressed = new HashMap<Integer, Boolean>();
        mouseButtons = new boolean[] { false, false, false };
        mouseClicks = new Point[] { null, null, null };

        gObjects = new HashMap<String, GameObject>();
        gLayers = new TreeMap<Integer, ArrayList<GameObject>>();
        gObjectsToAdd = new ArrayList<GameObject>();
        gObjectsToDel = new ArrayList<GameObject>();

        camera = new Camera(new PointD(0, 0), winSize);

        screen = ImageManager.createOpaqueImage(winSize.width, winSize.height);
        Graphics2D g2d = screen.createGraphics();
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, screen.getWidth(), screen.getHeight());
        g2d.dispose();

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        setSize(new Dimension(winSize.width, winSize.height));
        setPreferredSize(new Dimension(winSize.width, winSize.height));

        win = new JFrame();
        win.addWindowListener(this);
        win.setTitle(title);
        win.add(this);
        win.setResizable(false);
        win.pack();
        win.setVisible(true);

        repaint();
    }

    /**
     * Obtiene una instancia del juego en ejecucion. Util para las diferentes clases
     * utilizadas en un juego tal de acceder a metodos estaticos
     *
     * @return la instancia de LGE en ejecucion
     */
    public static LittleGameEngine getInstance() {
        if (lge == null) {
            System.out.println("LittleGameEngine no se encuentra activa");
            System.exit(ERROR);
        }

        return LittleGameEngine.lge;
    }

    /**
     * Obtiene los FPS calculados como el promedio de los ultimos valores
     *
     * @return los frame por segundo calculados
     */
    public double getFPS() {
        double dt = 0;
        synchronized (fpsData) {
            for (double val : fpsData)
                dt += val;
            dt = dt / fpsData.length;
        }
        return dt == 0 ? 0 : 1.0 / dt;
    }

    /**
     * Obtiene los LPS (Loops per Seconds) calculados como el promedio de los
     * ultimos valores
     *
     * @return los ciclos por segundo del GameLoop
     */
    public double getLPS() {
        double dt = 0;
        synchronized (lpsData) {
            for (double val : lpsData)
                dt += val;
            dt = dt / lpsData.length;
            return dt == 0 ? 0 : 1.0 / dt;
        }
    }

    /**
     * Si se especifica un color se habilita el despliegue del rectangulo que bordea
     * a todos los objetos (util para ver colisiones).
     *
     * Si se especifica null se desactiva
     *
     * @param color el color para los bordes de los rectangulos
     */
    public void showColliders(Color color) {
        collidersColor = color;
    }

    /**
     * Finaliza el Game Loop de LGE
     */
    public void quit() {
        running = false;
    }

    /**
     * Inicia el Game Loop de LGE tratando de mantener los fps especificados
     *
     * @param fps los fps a mantener
     */
    public void run(int fps) {
        screenTime = System.nanoTime();
        BufferedImage screenImage = ImageManager.createOpaqueImage(winSize.width, winSize.height);

        long tExpected = 1000000000 / fps;
        long tPrev = System.nanoTime();

        running = true;
        while (running) {
            // los eventos son atrapados por los listener

            // --- tiempo en ms desde el ciclo anterior
            long tElapsed = System.nanoTime() - tPrev;
            long t = tExpected - tElapsed;
            if (t > 0)
                try {
                    Thread.sleep(t / 1000000);
                } catch (InterruptedException e) {
                }

            long now = System.nanoTime();
            double dt = (now - tPrev) / 1000000000.0;
            tPrev = now;

            synchronized (lpsData) {
                lpsData[lpsIdx++] = dt;
                lpsIdx %= lpsData.length;
            }

            // --- Del gobj and gobj.OnDelete
            for (GameObject gobj : gObjectsToDel) {
                gObjects.remove(gobj.name);
                gLayers.get(gobj.layer).remove(gobj);
                if (camera.target == gobj)
                    camera.target = null;
            }
            for (GameObject gobj : gObjectsToDel)
                gobj.onDelete();
            gObjectsToDel.clear();

            // --- Add Gobj and gobj.OnStart
            for (GameObject gobj : gObjectsToAdd) {
                Integer layer = gobj.layer;
                ArrayList<GameObject> gobjs = gLayers.get(layer);
                if (gobjs == null) {
                    gobjs = new ArrayList<GameObject>();
                    gLayers.put(layer, gobjs);
                }
                if (!gobjs.contains(gobj)) {
                    gobjs.add(gobj);
                }
            }
            for (GameObject gobj : gObjectsToAdd)
                gobj.onStart();
            gObjectsToAdd.clear();

            // --- gobj.OnPreUpdate
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                for (GameObject gobj : elem.getValue()) {
                    gobj.onPreUpdate(dt);
                }
            }

            // --- gobj.OnUpdate
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                for (GameObject gobj : elem.getValue()) {
                    gobj.onUpdate(dt);
                }
            }

            // --- gobj.OnPostUpdate
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                for (GameObject gobj : elem.getValue()) {
                    gobj.onPostUpdate(dt);
                }
            }

            // --- game.OnMainUpdate
            if (onMainUpdate != null)
                onMainUpdate.accept(dt);

            // --- gobj.OnCollision
            LinkedHashMap<GameObject, ArrayList<GameObject>> oncollisions = new LinkedHashMap<GameObject, ArrayList<GameObject>>();
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                int layer = elem.getKey();
                if (layer != GUI_LAYER) {
                    for (GameObject gobj1 : elem.getValue()) {
                        if (!gobj1.callOnCollision)
                            continue;

                        if (!gobj1.useColliders)
                            continue;

                        ArrayList<GameObject> colliders = new ArrayList<GameObject>();
                        for (GameObject gobj2 : elem.getValue()) {
                            if (gobj1 == gobj2)
                                continue;
                            if (!gobj2.useColliders)
                                continue;
                            if (!gobj1.collidesWith(gobj2))
                                continue;
                            colliders.add(gobj2);
                        }
                        if (colliders.size() > 0)
                            oncollisions.put(gobj1, colliders);

                    }
                }
            }
            for (Entry<GameObject, ArrayList<GameObject>> elem : oncollisions.entrySet()) {
                GameObject gobj = elem.getKey();
                ArrayList<GameObject> gobjs = elem.getValue();
                gobj.onCollision(dt, gobjs.toArray(new GameObject[gobjs.size()]));
            }

            // --- gobj.OnPreRender
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                for (GameObject gobj : elem.getValue()) {
                    gobj.onPreRender(dt);
                }
            }

            // --- Camera Tracking
            camera.followTarget();

            // --- Rendering
            Graphics2D g2d = screenImage.createGraphics();
            g2d.setColor(bgColor);
            g2d.fillRect(0, 0, screenImage.getWidth(), screenImage.getHeight());

            // --- layers
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                int layer = elem.getKey();
                if (layer != GUI_LAYER) {
                    for (GameObject gobj : elem.getValue()) {
                        if (!gobj.rect.intersects(camera.rect))
                            continue;
                        PointD p = fixXY(gobj.getPosition());
                        BufferedImage surface = gobj.surface;
                        if (surface != null)
                            g2d.drawImage(surface, (int) p.x, (int) p.y, null);

                        if (collidersColor != null && gobj.useColliders) {
                            g2d.setColor(collidersColor);
                            for (RectangleD r : gobj.getCollider()) {
                                p = fixXY(new PointD(r.x, r.y));
                                g2d.drawRect((int) p.x, (int) p.y, r.width - 1, r.height - 1);
                            }
                        }
                    }
                }
            }

            // --- GUI
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                int layer = elem.getKey();
                if (layer == GUI_LAYER) {
                    for (GameObject gobj : elem.getValue()) {
                        BufferedImage surface = gobj.surface;
                        if (surface != null) {
                            double x = gobj.rect.x;
                            double y = gobj.rect.y;
                            g2d.drawImage(surface, (int) x, (int) y, null);
                        }
                    }
                }
            }
            g2d.dispose();

            g2d = screen.createGraphics();
            g2d.drawImage(screenImage, 0, 0, null);
            g2d.dispose();
            repaint();
        }

        // --- gobj.OnQuit
        for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
            for (GameObject gobj : elem.getValue()) {
                gobj.onQuit();
            }
        }

        // --- apaga los sonidos
        soundManager.stopAll();

        // eso es todo
        win.dispose();
    }

    // sistema cartesiano y zona visible dada por la camara
    /**
     * Traslada las coordenadas del GameObject a la zona de despliegue de la camara
     *
     * @param p las coordenadas a trasladar
     *
     * @return las coordenadas trasladadas
     */
    private PointD fixXY(PointD p) {
        double xo = p.x;
        double vx = camera.rect.x;
        double x = xo - vx;

        double yo = p.y;
        double vy = camera.rect.y;
        double y = yo - vy;

        return new PointD(x, y);
    }

    // ------ gobjects ------
    /**
     * Agrega un GameObject al juego el que quedara habilitado en el siguiente ciclo
     *
     * @param gobj  el GameObject a agregar
     * @param layer la capa a la cual pertenece
     */
    public void addGObject(GameObject gobj, int layer) {
        gobj.layer = layer;
        gObjects.put(gobj.name, gobj);
        gObjectsToAdd.add(gobj);
    }

    /**
     * Agrega un GameObject a la interfaz grafica del juego
     *
     * @param gobj el GameObject a agregar
     */
    public void addGObjectGUI(GameObject gobj) {
        addGObject(gobj, GUI_LAYER);
    }

    /**
     * Retorna el GameObject identificado con el nombre especificado
     *
     * @param name el nombre del GameObject a buscar
     *
     * @return el GameObject buscado (nulo si no lo encuentra)
     */
    public GameObject getGObject(String name) {
        return gObjects.get(name);
    }

    /**
     * Obtiene todos los GameObject de una capa cuyo tag comienza con un texto dado
     *
     * @param layer la capa den donde buscar
     * @param tag   el texto inicial del tag
     *
     * @return los GameObjects encontrados
     */
    public GameObject[] findGObjectsByTag(int layer, String tag) {
        ArrayList<GameObject> gobjs = new ArrayList<GameObject>();

        for (GameObject o : gLayers.get(layer))
            if (o.name.startsWith(tag))
                gobjs.add(o);

        return gobjs.toArray(new GameObject[gobjs.size()]);
    }

    /**
     * Retorna el total de GameObjects en el juego
     *
     * @return el total de GameObjects
     */
    public int getCountGObjects() {
        return gObjects.size();
    }

    /**
     * Elimina un GameObject del juego en el siguiente ciclo
     *
     * @param gobj el GameObject a eliminar
     */
    public void delGObject(GameObject gobj) {
        gObjectsToDel.add(gobj);
    }

    /**
     * Obtiene todos los GameObject que colisionan con un GameObject dado en la
     * misma capa
     *
     * @param gobj el GameObject a inspeccionar
     *
     * @return los GameObjects con los que colisiona
     */
    public GameObject[] collidesWith(GameObject gobj) {
        ArrayList<GameObject> gobjs = new ArrayList<GameObject>();

        if (gobj.useColliders)
            for (GameObject o : gLayers.get(gobj.layer))
                if (gobj != o && o.useColliders && gobj.collidesWith(o))
                    gobjs.add(o);

        return gobjs.toArray(new GameObject[gobjs.size()]);
    }

    /**
     * Obtiene todos los GameObjects de una capa dada que contienen un punto
     * especificado
     *
     * @param layer La capa a revisar
     * @param x     Coordenada X del punto a revisar
     * @param y     Coordenada Y del punto a revisar
     * @return Los GameObjects que contienen al punto
     */
    public GameObject[] contains(int layer, double x, double y) {
        return contains(layer, new PointD(x, y));
    }

    /**
     * Obtiene todos los GameObjects de una capa dada que contienen un punto
     * especificado
     *
     * @param layer    La capa a revisar
     * @param position El punto a revisar
     * @return Los GameObjects que contienen al punto
     */
    public GameObject[] contains(int layer, PointD position) {
        ArrayList<GameObject> gobjs = new ArrayList<GameObject>();

        for (GameObject o : gLayers.get(layer))
            if (o.rect.contains(position))
                gobjs.add(o);

        return gobjs.toArray(new GameObject[gobjs.size()]);
    }

    // ------ camera ------
    /**
     * Retorna la posiciona de la camara
     *
     * @return la posicion
     */
    public PointD getCameraPosition() {
        return camera.getPosition();
    }

    /**
     * retorna la dimension de la camara
     *
     * @return la dimension
     */
    public Size getCameraSize() {
        return camera.getSize();
    }

    /**
     * Establece el GameObject al cual la camara seguira de manera automatica
     *
     * @param gobj el GameObject a seguir
     */
    public void setCameraTarget(GameObject gobj) {
        if (gobj == null)
            camera.target = gobj;
        else
            setCameraTarget(gobj, true);
    }

    /**
     * Establece el GameObject al cual la camara seguira de manera automatica
     *
     * @param gobj   el GameObject a seguir
     * @param center si es verdadero la camara se centrara en el centro del
     *               GameObject, en caso contrario lo hara en el extremo superior
     *               izquierdo
     */
    public void setCameraTarget(GameObject gobj, boolean center) {
        camera.target = gobj;
        camera.targetInCenter = center;
    }

    /**
     * establece los limites en los cuales se movera la camara
     *
     * @param bounds los limites
     */
    public void setCameraBounds(RectangleD bounds) {
        camera.setBounds(bounds);
    }

    /**
     * Establece la posicion de la camara
     *
     * @param position la posicion
     */
    public void setCameraPosition(PointD position) {
        camera.setPosition(position);
    }

    // ------ keys ------
    /**
     * Determina si una tecla se encuentra presionada o no
     *
     * @param key la tecla a inspeccionar
     * @return verdadero si la tecla se encuentra presionada
     */
    public boolean keyPressed(int key) {
        synchronized (keysPressed) {
            return keysPressed.getOrDefault(key, false);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        synchronized (keysPressed) {
            keysPressed.put(e.getKeyCode(), true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        synchronized (keysPressed) {
            keysPressed.put(e.getKeyCode(), false);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // ------ mouse ------
    /**
     * Retorna el estado de los botones del mouse
     *
     * @return el estado de los botones
     */
    public boolean[] getMouseButtons() {
        synchronized (mouseButtons) {
            return mouseButtons.clone();
        }
    }

    /**
     * Determina la posicion del mouse en la ventana
     *
     * @return la posicion del mouse
     */
    @Override
    public Point getMousePosition() {
        Point p = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(p, this);

        if (p.x < 0 || p.x >= this.winSize.width || p.y < 0 || p.y >= this.winSize.height) {
            p.x = -1;
            p.y = -1;
        }

        return p;
    }

    /**
     * Determina si un boton del mouse se encuentra presionado
     *
     * @param button el boton a inspeccionar
     *
     * @return verdadero si se encuentra presionado
     */
    public Point getMouseClicked(int button) {
        synchronized (mouseClicks) {
            Point p = mouseClicks[button];
            mouseClicks[button] = null;
            return p;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        synchronized (mouseClicks) {
            mouseClicks[e.getButton() - 1] = new Point(e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        synchronized (mouseButtons) {
            mouseButtons[e.getButton() - 1] = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (mouseButtons) {
            mouseButtons[e.getButton() - 1] = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // ------ JPanel ------

    @Override
    public void paintComponent(Graphics g) {
        // los FPS
        double dt = (System.nanoTime() - screenTime) / 1000000.0;
        screenTime = System.nanoTime();

        synchronized (fpsData) {
            fpsData[fpsIdx++] = dt / 1000.0;
            fpsIdx %= fpsData.length;
        }

        g.drawImage(screen, 0, 0, null);
    }

    // ------ JFrame ------

    @Override
    public void windowClosing(WindowEvent e) {
        running = false;
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

}
