package rcr.lge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * La Pequena Maquina de Juegos
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 */
public class LittleGameEngine extends JPanel implements KeyListener, MouseListener, WindowListener {
    private static final long serialVersionUID = 6162190111045934737L;

    public static final int VLIMIT = 0xFFFFFFFF;
    public static final int GUI_LAYER = 0xFFFF;
    public static final int E_ON_DELETE = 0b00000001;
    public static final int E_ON_START = 0b00000010;
    public static final int E_ON_PRE_UPDATE = 0b00000100;
    public static final int E_ON_UPDATE = 0b00001000;
    public static final int E_ON_POST_UPDATE = 0b00010000;
    public static final int E_ON_COLLISION = 0b00100000;
    public static final int E_ON_PRE_RENDER = 0b01000000;
    public static final int E_ON_QUIT = 0b10000000;

    private static LittleGameEngine lge = null;
    private GraphicsConfiguration gconfig;

    private TreeMap<Integer, ArrayList<GameObject>> gLayers;
    private HashMap<String, GameObject> gObjects;
    private ArrayList<GameObject> gObjectsToAdd;
    private ArrayList<GameObject> gObjectsToDel;
    private Camera camera;

    private double[] fpsData;
    private int fpsIdx;
    private boolean running = false;

    private IEvents onMainUpdate = null;
    private HashMap<Integer, Boolean> keysPressed;
    private ArrayList<MouseEvent> mouseEvents;
    private boolean[] mouseButtons;

    private HashMap<String, BufferedImage[]> images;
    private HashMap<String, Font> fonts;
    private HashMap<String, ClipData> sounds;

    private JFrame win;

    private BufferedImage screen;
    private Color bgColor;
    private Color collidersColor = null;

    // ------ game engine ------
    /**
     * Crea el juego
     *
     * @param winSize dimensiones de la ventana de despliegue
     * @param title   titulo de la ventana
     * @param bgColor color de fondo de la ventana
     */
    public LittleGameEngine(Dimension winSize, String title, Color bgColor) {
        assertion(LittleGameEngine.lge == null, "LittleGameEngine ya se encuentra activa");
        lge = this;

        gconfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        fpsData = new double[30];
        fpsIdx = 0;

        this.bgColor = bgColor;

        fonts = new HashMap<String, Font>();
        sounds = new HashMap<String, ClipData>();
        images = new HashMap<String, BufferedImage[]>();

        keysPressed = new HashMap<Integer, Boolean>();
        mouseEvents = new ArrayList<MouseEvent>();
        mouseButtons = new boolean[] { false, false, false };

        gObjects = new HashMap<String, GameObject>();
        gLayers = new TreeMap<Integer, ArrayList<GameObject>>();
        gObjectsToAdd = new ArrayList<GameObject>();
        gObjectsToDel = new ArrayList<GameObject>();

        screen = createOpaqueImage(winSize.width, winSize.height);
        camera = new Camera(new Point(0, 0), winSize);

        Font f = new Font("Arial", Font.PLAIN, 40);
        Graphics2D g2d = screen.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, screen.getWidth(), screen.getHeight());
        g2d.setColor(Color.BLACK);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(f);
        g2d.drawString("Loading...", (winSize.width - 160) / 2, (winSize.height - 8) / 2);
        g2d.dispose();

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        setSize(winSize);
        setPreferredSize(winSize);

        win = new JFrame();
        win.addWindowListener(this);
        win.setTitle(title);
        win.add(this);
        win.pack();
        win.setResizable(false);
        win.setVisible(true);

        repaint();
    }

    /**
     * Obtiene una instancia del juego en ejecucion. Util para las diferentes clases
     * utilizadas en un juego tal de acceder a metodos estaticos
     *
     * @return
     */
    public static LittleGameEngine getInstance() {
        return LittleGameEngine.lge;
    }

    /**
     * Obtiene los FPS calculados como el promedio de los ultimos 30 valores
     *
     * @return los frame por segundo calculados
     */
    public double getFPS() {
        double dt = 0;
        for (double val : fpsData)
            dt += val;
        dt = dt / fpsData.length;
        return dt == 0 ? 0 : 1.0 / dt;
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
     * Establece la clase que recibira el evento onMainUpdate que es invocado justo
     * despues de invocar a los metodos onUpdate() de los GameObjects.
     *
     * Esta clase debe implementar IEvents
     *
     * @param iface
     */
    public void setOnMainUpdate(IEvents iface) {
        this.onMainUpdate = iface;
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
        BufferedImage _screen = createOpaqueImage(screen.getWidth(), screen.getHeight());
        running = true;
        long tickExpected = (long) (1000.0 / fps);
        long tickPrev = System.currentTimeMillis();
        while (running) {
            // events
            synchronized (mouseEvents) {
                mouseEvents.clear();
            }

            // --- tiempo en ms desde el ciclo anterior
            long tickElapsed = System.currentTimeMillis() - tickPrev;
            if (tickElapsed < tickExpected)
                try {
                    Thread.sleep(tickExpected - tickElapsed);
                } catch (InterruptedException e) {
                }

            long now = System.currentTimeMillis();
            double dt = (now - tickPrev) / 1000.0;
            tickPrev = now;

            fpsData[fpsIdx++] = dt;
            fpsIdx %= fpsData.length;

            // --- Del gobj and gobj.OnDelete
            ArrayList<GameObject> ondelete = new ArrayList<GameObject>();
            for (GameObject gobj : gObjectsToDel) {
                gObjects.remove(gobj.name);
                gLayers.get(gobj.layer).remove(gobj);
                if (camera.target == gobj)
                    camera.target = null;
                if ((gobj.onEventsEnabled & E_ON_DELETE) != 0x00)
                    ondelete.add(gobj);
            }
            gObjectsToDel.clear();
            for (GameObject gobj : ondelete)
                gobj.onDelete();
            ondelete = null;

            // --- Add Gobj and gobj.OnStart
            ArrayList<GameObject> onstart = new ArrayList<GameObject>();
            for (GameObject gobj : gObjectsToAdd) {
                Integer layer = gobj.layer;
                ArrayList<GameObject> gobjs = gLayers.get(layer);
                if (gobjs == null) {
                    gobjs = new ArrayList<GameObject>();
                    gLayers.put(layer, gobjs);
                }
                if (!gobjs.contains(gobj)) {
                    gobjs.add(gobj);
                    if ((gobj.onEventsEnabled & E_ON_START) != 0x00)
                        onstart.add(gobj);
                }
            }
            gObjectsToAdd.clear();
            for (GameObject gobj : onstart)
                gobj.onStart();
            onstart = null;

            // --- gobj.OnPreUpdate
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                for (GameObject gobj : elem.getValue()) {
                    if ((gobj.onEventsEnabled & E_ON_PRE_UPDATE) != 0x00)
                        gobj.onPreUpdate(dt);
                }
            }

            // --- gobj.OnUpdate
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                for (GameObject gobj : elem.getValue()) {
                    if ((gobj.onEventsEnabled & E_ON_UPDATE) != 0x00)
                        gobj.onUpdate(dt);
                }
            }

            // --- gobj.OnPostUpdate
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                for (GameObject gobj : elem.getValue()) {
                    if ((gobj.onEventsEnabled & E_ON_POST_UPDATE) != 0x00)
                        gobj.onPostUpdate(dt);
                }
            }

            // --- game.OnMainUpdate
            if (onMainUpdate != null)
                onMainUpdate.onMainUpdate(dt);

            // --- gobj.OnCollision
            LinkedHashMap<GameObject, ArrayList<GameObject>> oncollisions = new LinkedHashMap<GameObject, ArrayList<GameObject>>();
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                int layer = elem.getKey();
                if (layer != GUI_LAYER) {
                    for (GameObject gobj1 : elem.getValue()) {
                        if ((gobj1.onEventsEnabled & E_ON_COLLISION) == 0)
                            continue;

                        if (!gobj1.useColliders)
                            continue;

                        ArrayList<GameObject> colliders = new ArrayList<GameObject>();
                        for (GameObject gobj2 : elem.getValue()) {
                            if (gobj1 == gobj2)
                                continue;
                            if (!gobj2.useColliders)
                                continue;
                            if (!gobj1.rect.intersects(gobj2.rect))
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
                    if ((gobj.onEventsEnabled & E_ON_PRE_RENDER) != 0x00)
                        gobj.onPreRender(dt);
                }
            }

            // --- Camera Tracking
            camera.followTarget();

            // --- Rendering
            Graphics2D g2d = _screen.createGraphics();
            g2d.setColor(bgColor);
            g2d.fillRect(0, 0, screen.getWidth(), screen.getHeight());

            // --- layers
            for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
                int layer = elem.getKey();
                if (layer != GUI_LAYER) {
                    for (GameObject gobj : elem.getValue()) {
                        if (!gobj.rect.intersects(camera.rect))
                            continue;
                        Point p = fixXY(gobj);
                        BufferedImage surface = gobj.surface;
                        if (surface != null)
                            g2d.drawImage(surface, p.x, p.y, null);

                        if (collidersColor != null && gobj.useColliders) {
                            g2d.setColor(collidersColor);
                            g2d.drawRect(p.x, p.y, gobj.rect.width - 1, gobj.rect.height - 1);
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
                            int x = gobj.rect.x;
                            int y = gobj.rect.y;
                            // int w = gobj.width;
                            int h = gobj.rect.height;
                            g2d.drawImage(surface, x, camera.rect.height - y - h, null);
                        }
                    }
                }
            }
            g2d.dispose();

            synchronized (screen) {
                screen.setData(_screen.getData());
            }
            this.paintComponent(this.getGraphics());
        }

        // --- gobj.OnQuit
        for (Entry<Integer, ArrayList<GameObject>> elem : gLayers.entrySet()) {
            for (GameObject gobj : elem.getValue()) {
                if ((gobj.onEventsEnabled & E_ON_PRE_UPDATE) != 0x00)
                    gobj.onQuit();
            }
        }

        // apagamos los sonidos
        for (Entry<String, ClipData> elem : sounds.entrySet()) {
            ClipData clipData = elem.getValue();
            if (clipData.clip != null)
                clipData.clip.stop();
        }

        // eso es todo
        win.dispose();
    }

    // sistema cartesiano y zona visible dada por la camara
    /**
     * Convierte las coordenadas en sistema cartesiano del GameObject a coordenadas
     * de pantalla ajustandolas a la zona de vision de la camara
     *
     * @param gobj el objeto del cual convertir sus coordenadas
     *
     * @return las coordenadas ajustadas
     */
    private Point fixXY(GameObject gobj) {
        int xo = gobj.rect.x;
        int yo = gobj.rect.y;
        // int wo = (int)gobj.rect.width;
        int ho = gobj.rect.height;

        int wh = VLIMIT;

        int vx = camera.rect.x;
        int vy = camera.rect.y;
        // int vw = (int)camera.rect.width;
        int vh = camera.rect.height;

        int dy = wh - (vy + vh);
        int x = xo - vx;
        int y = wh - (yo + ho) - dy;

        return new Point(x, y);
    }

    // ------ gobjects ------
    /**
     * Agrega un GameObject al juego el que quedara habilitado en el siguiente ciclo
     *
     * @param gobj  el gameObject a agregar
     * @param layer la capa a la cual pertenece
     */
    public void addGObject(GameObject gobj, int layer) {
        assertion(gobj.layer < 0, "'gobj' ya fue agregado");
        assertion(layer >= 0 && layer <= GUI_LAYER, "'layer' invalido");
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
     * @return el gameObject buscado (nulo si no lo encuentra)
     */
    public GameObject getGObject(String name) {
        return gObjects.get(name);
    }

    /**
     * Retorna el total de GameObjects en el juego
     *
     * @return el total de gameObjects
     */
    public int getCountGObjects() {
        return gObjects.size();
    }

    /**
     * Elimina un GameObject del juego en el siguiente ciclo
     *
     * @param gobj el gameObject a eliminar
     */
    public void delGObject(GameObject gobj) {
        assertion(gobj.layer >= 0, "'gobj' no ha sido agregado");
        gObjectsToDel.add(gobj);
    }

    /**
     * Obtiene todos los GameObject que colisionan con un GameObject dado en la
     * misma capa
     *
     * @param gobj el GameObject a inspeccionar
     *
     * @return lois GameObjects con los que colisiona
     */
    public GameObject[] intersectGObjects(GameObject gobj) {
        ArrayList<GameObject> gobjs = new ArrayList<GameObject>();

        if (gobj.useColliders)
            for (GameObject o : gLayers.get(gobj.layer))
                if (gobj != o && o.useColliders && gobj.rect.intersects(o.rect))
                    gobjs.add(o);

        return gobjs.toArray(new GameObject[gobjs.size()]);
    }

    // ------ camera ------
    /**
     * Retorna la posiciona de la camara
     *
     * @return la posicion
     */
    public Point getCameraPosition() {
        return camera.getPosition();
    }

    /**
     * retorna la dimension de la camara
     *
     * @return la dimension
     */
    public Dimension getCameraSize() {
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
     *               GameObject, en caso contrario lo hara en el extremo inferior
     *               izquierdo
     */
    public void setCameraTarget(GameObject gobj, boolean center) {
        assertion(gobj.layer >= 0, "'gobj' no ha sido agregado");
        assertion(gobj.layer != GUI_LAYER, "'gobj' invalido");

        camera.target = gobj;
        camera.targetInCenter = center;
    }

    /**
     * establece los limites en los cuales se movera la camara
     *
     * @param bounds los limites
     */
    public void setCameraBounds(Rectangle bounds) {
        camera.setBounds(bounds);
    }

    /**
     * Establece la posicion de la camara
     *
     * @param position
     */
    public void setCameraPosition(Point position) {
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
        synchronized (mouseEvents) {
            return mouseButtons;
        }
    }

    /**
     * Determina la posicion del mouse en la ventana
     *
     * @return la posicion del mouse
     */
    @Override
    public Point getMousePosition() {
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        Point winLocation = win.getLocation();
        Point canvasLocation = this.getLocation();

        int wh = lge.camera.rect.height;
        int x = mouseLocation.x - (winLocation.x + canvasLocation.x);
        int y = wh - (mouseLocation.y - (winLocation.y + canvasLocation.y));

        return new Point(x, y);
    }

    /**
     * Determina si un boton del mouse se encuentra presionado
     *
     * @param button el boton a inspeccionar
     *
     * @return verdadero si se encuentra presionado
     */
    public Point getMouseClicked(int button) {
        synchronized (mouseEvents) {
            for (MouseEvent e : mouseEvents)
                if (e.getID() == MouseEvent.MOUSE_CLICKED)
                    if (e.getButton() == button + 1) {
                        int wh = lge.camera.rect.height;
                        Point p = new Point(e.getX(), e.getY());
                        p.y = wh - p.y;
                        return p;
                    }
        }
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        synchronized (mouseEvents) {
            mouseEvents.add(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        synchronized (mouseEvents) {
            mouseButtons[e.getButton() - 1] = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (mouseEvents) {
            mouseButtons[e.getButton() - 1] = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // ------ fonts ------
    /**
     * Obtiene los tipos de letra del sistema
     *
     * @return los tipos de letra
     */
    public String[] getSysFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();

        String[] sysFonts = new String[fonts.length];
        for (int i = 0; i < fonts.length; i++)
            sysFonts[i] = fonts[i].getFontName();
        return sysFonts;
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
    public void loadTTFFont(String name, String fname, int fstyle, int fsize) {
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

    // ------ sounds ------
    /**
     * Carga un archivo de sonido para ser utilizado durante el juego
     *
     * @param name  nombre a asignar al sonido
     * @param fname nombre del archivo que contiene el sonido
     */
    public void loadSound(String name, String fname) {
        if (fname.charAt(2) == ':')
            fname = fname.substring(1);

        ClipData clipData = sounds.get(name);
        if (clipData == null) {
            Path path = Paths.get(fname);
            byte[] data = null;

            try {
                data = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            clipData = new ClipData();
            clipData.clip = null;
            clipData.data = data;
            clipData.level = 50;
            sounds.put(name, clipData);

        }
    }

    /**
     * Inicia la reproduccion de un sonido
     *
     * @param name  el sonido (previamente cargado) a reproducir
     * @param loop  el numero de veces a repeptirlo
     * @param level el nivel de volumen
     */
    public void playSound(String name, boolean loop, double level) {
        ClipData clipData = sounds.get(name);

        if (clipData == null)
            return;

        if (clipData.clip != null)
            return;

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(clipData.data);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

            clipData.clip = AudioSystem.getClip();

            clipData.level = level;
            setSoundVolume(clipData);

            clipData.clip.addLineListener(new LineListener() {
                private ClipData TheClipData = clipData;

                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        TheClipData.clip.close();
                        TheClipData.clip = null;
                    }
                }
            });
            clipData.clip.open(ais);
            if (loop)
                clipData.clip.loop(Clip.LOOP_CONTINUOUSLY);
            else
                clipData.clip.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Detiene la reproduccion de un sonido
     *
     * @param name el nombre del sonido a detener
     */
    public void stopSound(String name) {
        ClipData clipData = sounds.get(name);

        if (clipData == null)
            return;

        if (clipData.clip == null)
            return;

        clipData.clip.stop();
    }

    /**
     * Establece el volumen de un sonido previamente cargado
     *
     * @param name  el nombre del sonido
     * @param level el nivel de volumen
     */
    public void setSoundVolume(String name, double level) {
        ClipData clipData = sounds.get(name);

        if (clipData == null)
            return;

        clipData.level = level;

        if (clipData.clip == null)
            return;

        setSoundVolume(clipData);
    }

    /**
     * Establece el volumen de un sonido previamente cargado
     *
     * @param clipData el sonido
     */
    private void setSoundVolume(ClipData clipData) {
        try {
            FloatControl volume = (FloatControl) clipData.clip.getControl(FloatControl.Type.VOLUME);
            volume.setValue((float) (clipData.level / 100.0));
        } catch (Exception e) {
            // System.out.println("SetSoundVolume(): " + e);
        }
    }

    /**
     * Obtiene el volumen de un sonido previamente cargado
     *
     * @param name el nombre del sonido
     *
     * @return el nivel de volumen
     */
    public double getSoundVolume(String name) {
        ClipData clipData = sounds.get(name);

        if (clipData == null)
            return 0;

        return clipData.level * 100;
    }

    // ------ images ------
    /**
     * Crea una imagen sin transparencia de dimensiones dadas
     *
     * @param width  ancho deseado
     * @param height alto deseado
     *
     * @return la imagen creada
     */
    BufferedImage createOpaqueImage(int width, int height) {
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
    BufferedImage createTranslucentImage(int width, int height) {
        return gconfig.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
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
     * Cara una imagen o grupo de imagenes desde archivos para ser utilizadas en el
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
    public void loadImage(String iname, String pattern, boolean flipX, boolean flipY) {
        loadImage(iname, pattern, 1, new Dimension(0, 0), flipX, flipY);
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
     * @param size    nuevo tamano de la imagen cargada
     * @param flipX   si es verdadero al imagen se reflejara en el eje X
     * @param flipY   si es verdadero al imagen se reflejara en el eje Y
     */
    public void loadImage(String iname, String pattern, Dimension size, boolean flipX, boolean flipY) {
        loadImage(iname, pattern, 0, size, flipX, flipY);
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
     * @param flipX   si es verdadero al imagen se reflejara en el eje X
     * @param flipY   si es verdadero al imagen se reflejara en el eje Y
     */
    public void loadImage(String iname, String pattern, double scale, boolean flipX, boolean flipY) {
        loadImage(iname, pattern, scale, new Dimension(0, 0), flipX, flipY);
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
    private void loadImage(String iname, String pattern, double scale, Dimension size, boolean flipX, boolean flipY) {
        ArrayList<BufferedImage> images = readImages(pattern);

        if (size.width > 0 && size.height > 0) {
            int imgSize = images.size();
            for (int i = 0; i < imgSize; i++) {
                BufferedImage img = images.get(i);
                Image scaledImage = img.getScaledInstance(size.width, size.height, BufferedImage.SCALE_SMOOTH);
                BufferedImage bi = createTranslucentImage(size.width, size.height);
                Graphics2D g2d = bi.createGraphics();

                int w = scaledImage.getWidth(null);
                int h = scaledImage.getHeight(null);
                if (flipX)
                    g2d.drawImage(scaledImage, w, 0, -w, h, null);
                if (flipY)
                    g2d.drawImage(scaledImage, 0, h, w, -h, null);
                if (!flipX && !flipY)
                    g2d.drawImage(scaledImage, 0, 0, null);

                g2d.dispose();
                images.set(i, bi);
            }
        } else if (scale > 0 && scale != 1) {
            int imgSize = images.size();
            for (int i = 0; i < imgSize; i++) {
                BufferedImage img = images.get(i);
                int width = (int) Math.round(img.getWidth() * scale);
                int height = (int) Math.round(img.getHeight() * scale);
                Image scaledImage = img.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
                BufferedImage bi = createTranslucentImage(width, height);
                Graphics2D g2d = bi.createGraphics();

                int w = scaledImage.getWidth(null);
                int h = scaledImage.getHeight(null);
                if (flipX)
                    g2d.drawImage(scaledImage, w, 0, -w, h, null);
                if (flipY)
                    g2d.drawImage(scaledImage, 0, h, w, -h, null);
                if (!flipX && !flipY)
                    g2d.drawImage(scaledImage, 0, 0, null);

                g2d.dispose();
                images.set(i, bi);
            }
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
        try {
            if (pattern.charAt(2) == ':')
                pattern = pattern.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

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

    // ------ JPanel ------

    @Override
    public void paintComponent(Graphics g) {
        synchronized (screen) {
            g.drawImage(screen, 0, 0, null);
        }
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

    // ------ utils ------

    /**
     * Finaliza el programa si la condicion recibida no se cumple
     *
     * @param condition condicion a vertificar
     * @param msg       mensaje a desplegar si la condicion no se cumple
     */
    private void assertion(boolean condition, String msg) {
        if (!condition) {
            System.out.println(msg);
            System.exit(1);
        }
    }

    /**
     * Obtiene la ruta en el sistema de archivos del path recibido
     *
     * @param objClass clase a utilizar como directorio actual (su localizacion)
     * @param path     ruta a convertir
     *
     * @return la ruta completa
     */
    public String getRealPath(Object objClass, String path) {
        String p = null;
        try {
            p = new URI(objClass.getClass().getResource(path).getPath()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return p;
    }

}
