package rcr.lge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
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
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public static final int E_ON_QUIT = 0b10000010;

    private static LittleGameEngine lge = null;
    private IEvents on_main_update = null;
    protected int on_events_enabled = 0x00;
    private double[] average_fps;
    private int average_fps_idx;
    private boolean running = false;

    private Camera camera;

    private JFrame win;
    private BufferedImage screen;
    private Color bgColor;
    private Color collidersColor = null;

    private HashMap<String, ArrayList<BufferedImage>> images;
    private HashMap<String, Font> fonts;
    private HashMap<String, ClipData> sounds;
    private TreeMap<Integer, ArrayList<GameObject>> gObjects;
    private ArrayList<GameObject> gObjectsToAdd;
    private ArrayList<GameObject> gObjectsToDel;
    private HashMap<Integer, Boolean> keys_pressed;
    private ArrayList<MouseEvent> mouse_events;
    private boolean[] mouse_buttons;

    private LittleGameEngine(Dimension win_size, String title, Color bgColor) {
        average_fps = new double[30];
        average_fps_idx = 0;

        this.bgColor = bgColor;

        fonts = new LinkedHashMap<String, Font>();
        sounds = new HashMap<String, ClipData>();
        images = new LinkedHashMap<String, ArrayList<BufferedImage>>();

        gObjects = new TreeMap<Integer, ArrayList<GameObject>>();
        gObjectsToAdd = new ArrayList<GameObject>();
        gObjectsToDel = new ArrayList<GameObject>();

        camera = new Camera(new Point(0, 0), win_size);

        keys_pressed = new HashMap<Integer, Boolean>();
        mouse_events = new ArrayList<MouseEvent>();
        mouse_buttons = new boolean[] { false, false, false };

        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        setSize(win_size);
        setPreferredSize(win_size);

        win = new JFrame();
        win.addWindowListener(this);
        win.setTitle(title);
        win.add(this);
        win.pack();
        win.setResizable(false);
        win.setVisible(true);

        screen = new BufferedImage(win_size.width, win_size.height, BufferedImage.TYPE_INT_ARGB);
    }

    public static LittleGameEngine Init(Dimension win_size, String title, Color bgColor) {
        if (lge == null)
            lge = new LittleGameEngine(win_size, title, bgColor);
        return lge;
    }

    public static LittleGameEngine GetLGE() {
        return lge;
    }

    // gobjects
    public void AddGObject(GameObject gobj, int layer) {
        assert gobj.layer < 0 : "'gobj' ya fue agregado";
        assert layer >= 0 && layer <= GUI_LAYER : "'layer' invalido";
        gobj.layer = layer;
        gObjectsToAdd.add(gobj);
    }

    public void AddGObjectGUI(GameObject gobj) {
        AddGObject(gobj, GUI_LAYER);
    }

    public GameObject GetGObject(String name) {
        for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
            for (GameObject gobj : elem.getValue())
                if (gobj.name == name)
                    return gobj;
        }

        return null;
    }

    public GameObject[] GetGObjects() {
        return GetGObjects("*");
    }

    public GameObject[] GetGObjects(String pattern) {
        ArrayList<GameObject> gobjs = new ArrayList<GameObject>();

        for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
            for (GameObject gobj : elem.getValue())
                if (pattern == "*")
                    gobjs.add(gobj);
                else if (pattern == gobj.name)
                    gobjs.add(gobj);
                else {
                }
        }

        return gobjs.toArray(new GameObject[gobjs.size()]);
    }

    public void DelGObject(GameObject gobj) {
        assert gobj.layer >= 0 : "'gobj' no ha sido agregado";
        gObjectsToDel.add(gobj);
    }

    public void DelGObject(String pattern) {
        for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
            for (GameObject gobj : elem.getValue())
                if (pattern == "*")
                    DelGObject(gobj);
                else if (pattern == gobj.name)
                    DelGObject(gobj);
                else {
                }
        }
    }

    public void ShowColliders(Color color) {
        collidersColor = color;
    }

    // camera
    public Point GetCameraPosition() {
        return camera.rect.getLocation();
    }

    public Dimension GetCameraSize() {
        return camera.rect.getSize();
    }

    public void SetCameraTarget(GameObject gobj) {
        if (gobj == null)
            camera.target = gobj;
        else
            SetCameraTarget(gobj, true);
    }

    public void SetCameraTarget(GameObject gobj, boolean center) {
        assert gobj.layer < 0 : "'gobj' ya fue agregado";
        assert gobj.layer >= 0 : "'gobj' no ha sido agregado";
        assert gobj.layer != GUI_LAYER : "'gobj' invalido";

        camera.target = gobj;
        camera.target_center = center;
    }

    public void SetCameraBounds(Rectangle bounds) {
        camera.bounds = bounds;
    }

    public void SetCameraPosition(Point position) {
        camera.SetPosition(position);
    }

    // key events
    public boolean KeyPressed(int key) {
        synchronized (keys_pressed) {
            return keys_pressed.getOrDefault(key, false);
        }
    }

    // events
    public void SetOnEvents(int on_events_enabled) {
        this.on_events_enabled = on_events_enabled;
    }

    // mouse events
    public boolean[] GetMouseButtons() {
        synchronized (mouse_events) {
            return mouse_buttons;
        }
    }

    public Point GetMousePosition() {
        Point mouse_location = MouseInfo.getPointerInfo().getLocation();
        Point win_location = win.getLocation();
        Point canvas_location = this.getLocation();

        int wh = lge.camera.rect.height;
        int x = mouse_location.x - (win_location.x + canvas_location.x);
        int y = wh - (mouse_location.y - (win_location.y + canvas_location.y));

        return new Point(x, y);
    }

    public Point GetMouseClicked(int button) {
        synchronized (mouse_events) {
            for (MouseEvent e : mouse_events)
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

    // game
    public double GetFPS() {
        double fps = 0;
        for (double val : average_fps)
            fps += val;
        fps = fps / average_fps.length;
        return fps;
    }

    public void SetOnMainUpdate(IEvents iface) {
        this.on_main_update = iface;
    }

    public void Quit() {
        running = false;
    }

    // main loop
    public void Run(int fps) {
        running = true;
        long tick_expected = (long) (1000.0 / fps);
        long tick_prev = System.currentTimeMillis();
        while (running) {
            // events
            synchronized (mouse_events) {
                mouse_events.clear();
            }

            // --- tiempo en ms desde el ciclo anterior
            long tick_elapsed = System.currentTimeMillis() - tick_prev;
            if (tick_elapsed < tick_expected)
                try {
                    Thread.sleep(tick_expected - tick_elapsed);
                } catch (InterruptedException e) {
                }

            long now = System.currentTimeMillis();
            double dt = (now - tick_prev) / 1000.0;
            tick_prev = now;
            average_fps[average_fps_idx++] = dt;
            average_fps_idx %= average_fps.length;

            // --- Del gobj and gobj.OnDelete
            ArrayList<GameObject> ondelete = new ArrayList<GameObject>();
            for (GameObject gobj : gObjectsToDel) {
                gObjects.get(gobj.layer).remove(gobj);
                if (camera.target == gobj)
                    camera.target = null;
                if ((gobj.on_events_enabled & E_ON_DELETE) != 0x00)
                    ondelete.add(gobj);
            }
            gObjectsToDel.clear();
            if ((on_events_enabled & E_ON_DELETE) != 0)
                for (GameObject gobj : ondelete)
                    gobj.OnDelete();
            ondelete = null;

            // --- Add Gobj and gobj.OnStart
            ArrayList<GameObject> onstart = new ArrayList<GameObject>();
            for (GameObject gobj : gObjectsToAdd) {
                Integer layer = gobj.layer;
                ArrayList<GameObject> gobjs = gObjects.get(layer);
                if (gobjs == null) {
                    gobjs = new ArrayList<GameObject>();
                    gObjects.put(layer, gobjs);
                }
                if (!gobjs.contains(gobj)) {
                    gobjs.add(gobj);
                    if ((gobj.on_events_enabled & E_ON_START) != 0x00)
                        onstart.add(gobj);
                }
            }
            gObjectsToAdd.clear();
            if ((on_events_enabled & E_ON_START) != 0)
                for (GameObject gobj : onstart)
                    gobj.OnStart();
            onstart = null;

            // --- gobj.OnPreUpdate
            if ((on_events_enabled & E_ON_PRE_UPDATE) != 0)
                for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
                    for (GameObject gobj : elem.getValue()) {
                        if ((gobj.on_events_enabled & E_ON_PRE_UPDATE) != 0x00)
                            gobj.OnPreUpdate(dt);
                    }
                }

            // --- gobj.OnUpdate
            if ((on_events_enabled & E_ON_UPDATE) != 0)
                for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
                    for (GameObject gobj : elem.getValue()) {
                        if ((gobj.on_events_enabled & E_ON_UPDATE) != 0x00)
                            gobj.OnUpdate(dt);
                    }
                }

            // --- gobj.OnPostUpdate
            if ((on_events_enabled & E_ON_POST_UPDATE) != 0)
                for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
                    for (GameObject gobj : elem.getValue()) {
                        if ((gobj.on_events_enabled & E_ON_POST_UPDATE) != 0x00)
                            gobj.OnPostUpdate(dt);
                    }
                }

            // --- game.OnMainUpdate
            if (on_main_update != null)
                on_main_update.OnMainUpdate(dt);

            // --- gobj.OnCollision
            if ((on_events_enabled & E_ON_COLLISION) != 0) {
                LinkedHashMap<GameObject, ArrayList<GameObject>> oncollisions = new LinkedHashMap<GameObject, ArrayList<GameObject>>();
                for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
                    int layer = elem.getKey();
                    if (layer != GUI_LAYER) {
                        for (GameObject gobj1 : elem.getValue()) {
                            ArrayList<GameObject> colliders = new ArrayList<GameObject>();
                            if (!gobj1.use_colliders)
                                continue;
                            for (GameObject gobj2 : elem.getValue()) {
                                if (gobj1 == gobj2)
                                    continue;
                                if (!gobj2.use_colliders)
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
                for (Entry<GameObject, ArrayList<GameObject>> elem : oncollisions.entrySet())
                    elem.getKey().OnCollision(dt, elem.getValue());
            }

            // --- gobj.OnPreRender
            if ((on_events_enabled & E_ON_PRE_RENDER) != 0)
                for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
                    for (GameObject gobj : elem.getValue()) {
                        if ((gobj.on_events_enabled & E_ON_PRE_RENDER) != 0x00)
                            gobj.OnPreRender(dt);
                    }
                }

            // --- Camera Tracking
            camera.FollowTarget();

            // --- Rendering
            synchronized (screen) {
                int vh = (int) camera.rect.getHeight();
                Graphics2D g2d = screen.createGraphics();
                g2d.setColor(bgColor);
                g2d.fillRect(0, 0, screen.getWidth(), screen.getHeight());

                // --- layers
                for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
                    int layer = elem.getKey();
                    if (layer != GUI_LAYER) {
                        for (GameObject gobj : elem.getValue()) {
                            Point p = Fix_XY(gobj);
                            BufferedImage surface = gobj.surface;
                            if (surface != null)
                                g2d.drawImage(surface, p.x, p.y, null);

                            if (collidersColor != null && gobj.use_colliders) {
                                g2d.setColor(collidersColor);
                                g2d.drawRect(p.x, p.y, gobj.rect.width - 1, gobj.rect.height - 1);
                            }
                        }
                    }
                }

                // --- GUI
                for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
                    int layer = elem.getKey();
                    if (layer == GUI_LAYER) {
                        for (GameObject gobj : elem.getValue()) {
                            BufferedImage surface = gobj.surface;
                            if (surface != null) {
                                int x = gobj.rect.x;
                                int y = gobj.rect.y;
                                // int w = gobj.width;
                                int h = gobj.rect.height;
                                g2d.drawImage(surface, x, vh - y - h, null);
                            }
                        }
                    }
                }
                g2d.dispose();
            }
            // ---
            this.getGraphics().drawImage(screen, 0, 0, null);
            this.repaint();
        }

        // --- gobj.OnQuit
        if ((on_events_enabled & E_ON_QUIT) != 0)
            for (Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet()) {
                for (GameObject gobj : elem.getValue()) {
                    if ((gobj.on_events_enabled & E_ON_PRE_UPDATE) != 0x00)
                        gobj.OnQuit();
                }
            }

        // apagamos los sonidos
        for (Entry<String, ClipData> elem : sounds.entrySet()) {
            ClipData clip_data = elem.getValue();
            if (clip_data.clip != null)
                clip_data.clip.stop();
        }

        // eso es todo
        win.dispose();
    }

    @Override
    public void paintComponent(Graphics g) {
        synchronized (screen) {
            g.drawImage(screen, 0, 0, null);
        }
    }

    // sistema cartesiano y zona visible dada por la camara
    private Point Fix_XY(GameObject gobj) {
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

    // --- Recursos

    // fonts
    public String[] GetSysFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();

        String[] sys_fonts = new String[fonts.length];
        for (int i = 0; i < fonts.length; i++)
            sys_fonts[i] = fonts[i].getFontName();
        return sys_fonts;
    }

    public void LoadSysFont(String name, String fname, int fstyle, int fsize) {
        if (fonts.get(name) == null) {
            Font f = new Font(fname, fstyle, fsize);
            fonts.put(name, f);
        }
    }

    public void LoadTTFFont(String name, String fname, int fstyle, int fsize) {
        try {
            fname = new URI(fname).getPath();
            if (fname.charAt(2) == ':')
                fname = fname.substring(3);
        } catch (Exception e) {
        }

        if (fonts.get(name) == null) {
            Font font = null;
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try {
                File file = new File(fname);
                font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(file));
            } catch (Exception e) {
                System.out.println(fname);
                System.out.println(e);
                System.exit(1);
            }
            Font f = font.deriveFont(fstyle, fsize);
            ge.registerFont(f);
            fonts.put(name, f);
        }
    }

    public Font GetFont(String fname) {
        return fonts.get(fname);
    }

    // sonidos
    public void LoadSound(String name, String fname) {
        try {
            fname = new URI(fname).getPath();
            if (fname.charAt(2) == ':')
                fname = fname.substring(3);
        } catch (Exception e) {
        }

        ClipData clip_data = sounds.get(name);
        if (clip_data == null) {
            try {
                FileInputStream fis = new FileInputStream(fname);
                clip_data = new ClipData();
                clip_data.clip = null;
                clip_data.data = fis.readAllBytes();
                clip_data.level = 50;
                sounds.put(name, clip_data);
                fis.close();
            } catch (Exception e) {
                //System.out.println("LoadSound(): " + e);
            }
        }
    }

    public void PlaySound(String name, boolean loop, double level) {
        ClipData clip_data = sounds.get(name);

        if (clip_data == null)
            return;

        if (clip_data.clip != null)
            return;

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(clip_data.data);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

            clip_data.clip = AudioSystem.getClip();

            clip_data.level = level;
            SetSoundVolume(clip_data);

            clip_data.clip.addLineListener(new LineListener() {
                private ClipData the_clip_data = clip_data;

                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        the_clip_data.clip.close();
                        the_clip_data.clip = null;
                    }
                }
            });
            clip_data.clip.open(ais);
            if (loop)
                clip_data.clip.loop(Clip.LOOP_CONTINUOUSLY);
            else
                clip_data.clip.start();
        } catch (Exception e) {
            //System.out.println("PlaySound(): " + e);
        }
    }

    public void StopSound(String name) {
        ClipData clip_data = sounds.get(name);

        if (clip_data == null)
            return;

        if (clip_data.clip == null)
            return;

        clip_data.clip.stop();
    }

    public void SetSoundVolume(String name, double level) {
        ClipData clip_data = sounds.get(name);

        if (clip_data == null)
            return;

        clip_data.level = level;

        if (clip_data.clip == null)
            return;

        SetSoundVolume(clip_data);
    }

    private void SetSoundVolume(ClipData clip_data) {
        try {
            FloatControl volume = (FloatControl) clip_data.clip.getControl(FloatControl.Type.VOLUME);
            volume.setValue((float) (clip_data.level / 100.0));
        } catch (Exception e) {
            //System.out.println("SetSoundVolume(): " + e);
        }
    }

    public double GetSoundVolume(String name) {
        ClipData clip_data = sounds.get(name);

        if (clip_data == null)
            return 0;

        return clip_data.level;
    }

    // imagenes
    public ArrayList<BufferedImage> GetImages(String iname) {
        return images.get(iname);
    }

    public void LoadImage(String iname, String pattern, boolean flipX, boolean flipY) {
        LoadImage(iname, pattern, 1, new Dimension(0, 0), flipX, flipY);
    }

    public void LoadImage(String iname, String pattern, Dimension size, boolean flipX, boolean flipY) {
        LoadImage(iname, pattern, 0, size, flipX, flipY);
    }

    public void LoadImage(String iname, String pattern, double scale, boolean flipX, boolean flipY) {
        LoadImage(iname, pattern, scale, new Dimension(0, 0), flipX, flipY);
    }

    private void LoadImage(String iname, String pattern, double scale, Dimension size, boolean flipX, boolean flipY) {
        ArrayList<BufferedImage> images = ReadImages(pattern);

        if (size.width > 0 && size.height > 0) {
            for (int i = 0; i < images.size(); i++) {
                BufferedImage img = images.get(i);
                Image scaled_image = img.getScaledInstance(size.width, size.height, BufferedImage.SCALE_SMOOTH);
                BufferedImage bi = new BufferedImage(scaled_image.getWidth(null), scaled_image.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bi.createGraphics();

                int w = scaled_image.getWidth(null);
                int h = scaled_image.getHeight(null);
                if (flipX)
                    g2d.drawImage(scaled_image, w, 0, -w, h, null);
                if (flipY)
                    g2d.drawImage(scaled_image, 0, h, w, -h, null);
                if (!flipX && !flipY)
                    g2d.drawImage(scaled_image, 0, 0, null);

                g2d.dispose();
                images.set(i, bi);
            }
        } else if (scale > 0 && scale != 1) {
            for (int i = 0; i < images.size(); i++) {
                BufferedImage img = images.get(i);
                int width = (int) Math.round(img.getWidth() * scale);
                int height = (int) Math.round(img.getHeight() * scale);
                Image scaled_image = img.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
                BufferedImage bi = new BufferedImage(scaled_image.getWidth(null), scaled_image.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bi.createGraphics();

                int w = scaled_image.getWidth(null);
                int h = scaled_image.getHeight(null);
                if (flipX)
                    g2d.drawImage(scaled_image, w, 0, -w, h, null);
                if (flipY)
                    g2d.drawImage(scaled_image, 0, h, w, -h, null);
                if (!flipX && !flipY)
                    g2d.drawImage(scaled_image, 0, 0, null);

                g2d.dispose();
                images.set(i, bi);
            }
        }

        this.images.put(iname, images);
    }

    private ArrayList<BufferedImage> ReadImages(String pattern) {
        try {
            pattern = new URI(pattern).getPath();
            if (pattern.charAt(2) == ':')
                pattern = pattern.substring(3);
        } catch (Exception e) {
        }

        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

        String dir = "";
        int pos = pattern.lastIndexOf('/');
        if (pos > -1) {
            dir = pattern.substring(0, pos);
            pattern = pattern.substring(pos + 1);
        }

        ArrayList<String> fnames = new ArrayList<String>();
        Path p = Path.of(dir);
        DirectoryStream<Path> paths = null;
        try {
            paths = Files.newDirectoryStream(p, pattern);
        } catch (Exception e) {
            System.out.println(pattern);
            System.out.println(e);
            System.exit(1);
        }
        for (Path path : paths)
            fnames.add(path.toString());

        Collections.sort(fnames);
        for (String fname : fnames) {
            BufferedImage img = ReadImage(fname);
            images.add(img);
        }
        return images;
    }

    private BufferedImage ReadImage(String fname) {
        File f = new File(fname);
        BufferedImage img = null;
        try {
            img = ImageIO.read(f);
        } catch (Exception e) {
            System.out.println(fname);
            System.out.println(e);
            System.exit(1);
        }
        return img;
    }

    // ---
    @Override
    public void mouseClicked(MouseEvent e) {
        synchronized (mouse_events) {
            mouse_events.add(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        synchronized (mouse_events) {
            mouse_buttons[e.getButton() - 1] = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (mouse_events) {
            mouse_buttons[e.getButton() - 1] = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    // ---
    @Override
    public void keyPressed(KeyEvent e) {
        synchronized (keys_pressed) {
            keys_pressed.put(e.getKeyCode(), true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        {
            keys_pressed.put(e.getKeyCode(), false);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // ---
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
