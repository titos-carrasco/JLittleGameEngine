package rcr.lge;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class LittleGameEngine
{
    public static final int VLIMIT           = 0xFFFFFFFF;
    public static final int GUI_LAYER        = 0xFFFF;
    public static final int E_ON_DELETE      = 0b00000001;
    public static final int E_ON_START       = 0b00000010;
    public static final int E_ON_PRE_UPDATE  = 0b00000100;
    public static final int E_ON_UPDATE      = 0b00001000;
    public static final int E_ON_POST_UPDATE = 0b00010000;
    public static final int E_ON_COLLISION   = 0b00100000;
    public static final int E_ON_PRE_RENDER  = 0b01000000;
    public static final int E_ON_QUIT        = 0b10000010;

    private static LittleGameEngine lge = null;
    private IEvents on_main_update;
    private boolean running;

    private Camera camera;

    private Frame win;
    private BufferedImage screen;
    private String title;
    private Color bgColor;
    private Color collidersColor;

    private HashMap<String, ArrayList<BufferedImage>> images;
    private HashMap<String, Font> fonts;
    private HashMap<String, byte[]> sounds;
    private LinkedHashMap<Integer, ArrayList<GameObject>> gObjects;
    private ArrayList<GameObject> gObjectsToAdd;
    private ArrayList<GameObject> gObjectsToDel;
    private HashMap<Integer, Boolean> keys_pressed;
    private ArrayList<MouseEvent> mouse_events;
    private boolean[] mouse_buttons;

    private LittleGameEngine( Dimension win_size, String title, Color bgColor )
    {
        this.on_main_update = null;
        running = false;

        this.title = title;
        this.bgColor = bgColor;
        this.collidersColor = null;

        this.fonts = new LinkedHashMap<String, Font>();
        this.sounds = new LinkedHashMap<String, byte[]>();
        this.images = new LinkedHashMap<String, ArrayList<BufferedImage>>();

        this.gObjects = new LinkedHashMap<Integer, ArrayList<GameObject>>();
        this.gObjectsToAdd = new ArrayList<GameObject>();
        this.gObjectsToDel = new ArrayList<GameObject>();

        camera = new Camera( new Point( 0, 0),  win_size );

        keys_pressed = new HashMap<Integer, Boolean>();
        mouse_events = new ArrayList<MouseEvent>();
        mouse_buttons = new boolean[] { false, false, false };

        Canvas canvas = new Canvas();
        canvas.setSize( win_size );
        canvas.setBackground( bgColor );
        canvas.addKeyListener(
            new KeyAdapter()
            {
                @Override
                public void keyPressed( KeyEvent e )
                {
                    synchronized( keys_pressed )
                    {
                        keys_pressed.put( e.getKeyCode(), true );
                    }
                }

                @Override
                public void keyReleased( KeyEvent e )
                {
                    {
                        keys_pressed.put( e.getKeyCode(), false );
                    }
                }
            }
        );
        canvas.addMouseListener(
            new MouseAdapter()
            {
                @Override
                public void mouseClicked( MouseEvent e )
                {
                    synchronized( mouse_events )
                    {
                        mouse_events.add( e );
                    }
                }

                @Override
                public void mousePressed( MouseEvent e )
                {
                    synchronized( mouse_events )
                    {
                        mouse_buttons[ e.getButton() - 1 ] = true;
                    }
                }

                @Override
                public void mouseReleased( MouseEvent e )
                {
                    synchronized( mouse_events )
                    {
                        mouse_buttons[ e.getButton() - 1 ] = false;
                    }
                }
            }
        );

        win = new Frame();
        win.setTitle( this.title );
        win.add( canvas );
        win.pack();
        win.setResizable( false );
        win.setVisible( true );
        win.addWindowListener(
            new WindowAdapter()
            {
                @Override
                public void windowClosing( WindowEvent e )
                {
                    running = false;
                }
            }
        );

        screen = new BufferedImage( win_size.width, win_size.height, BufferedImage.TYPE_INT_ARGB);
    }

    public static LittleGameEngine Init( Dimension win_size, String title, Color bgColor )
    {
        if( lge == null ) lge = new LittleGameEngine( win_size, title, bgColor );
        return lge;
    }

    public static LittleGameEngine GetLGE()
    {
        return lge;
    }

    // gobjects
    public void AddGObject( GameObject gobj, int layer )
    {
        assert gobj.layer < 0 : "'gobj' ya fue agregado";
        assert layer >= 0 && layer <= GUI_LAYER : "'layer' invalido";
        gobj.layer = layer;
        gObjectsToAdd.add( gobj );
    }

    public void AddGObjectGUI( GameObject gobj )
    {
        AddGObject( gobj, GUI_LAYER );
    }

    public GameObject GetGObject( String name )
    {
        for( Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet() )
        {
            for( GameObject gobj : elem.getValue() )
            if( gobj.name == name )
            return gobj;
        }

    return null;
    }

    public GameObject[] GetGObjects()
    {
        return GetGObjects( "*" );
    }

    public GameObject[] GetGObjects( String pattern )
    {
        ArrayList<GameObject> gobjs = new ArrayList<GameObject>();

        for( Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet() )
        {
            for( GameObject gobj : elem.getValue() )
                if( pattern == "*" ) gobjs.add( gobj );
                else if( pattern == gobj.name ) gobjs.add( gobj );
                else
                {
                }
        }

        return gobjs.toArray( new GameObject[gobjs.size()] );
    }

    public void DelGObject( GameObject gobj )
    {
        assert gobj.layer >= 0 : "'gobj' no ha sido agregado";
        gObjectsToDel.add( gobj );
    }

    public void DelGObject( String pattern )
    {
        for( Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet() )
        {
            for( GameObject gobj : elem.getValue() )
            if( pattern == "*" ) DelGObject( gobj );
            else if( pattern == gobj.name ) DelGObject( gobj );
            else
            {
            }
        }
    }

    public void ShowColliders( Color color )
    {
        collidersColor = color;
    }

/*
    def GetCollisions( gobj ):
        return [ o
                    for o in Engine.gObjects[gobj._layer]
                        if o != gobj and \
                           o._use_colliders and \
                           gobj._rect.CollideRectangle( o._rect )
                ]

*/
    // camera
    public Point GetCameraPosition()
    {
        return camera.rect.getLocation();
    }

    public Dimension GetCameraSize()
    {
        return camera.rect.getSize();
    }

    public void SetCameraTarget( GameObject gobj )
    {
        if( gobj == null )
            camera.target = gobj;
        else
            SetCameraTarget( gobj, true );
    }

    public void SetCameraTarget( GameObject gobj, boolean center )
    {
        assert gobj.layer < 0 : "'gobj' ya fue agregado";
        assert gobj.layer >= 0 : "'gobj' no ha sido agregado";
        assert gobj.layer != GUI_LAYER : "'gobj' invalido";

        camera.target = gobj;
        camera.target_center = center;
    }

    public void SetCameraBounds( Rectangle bounds )
    {
        camera.bounds = bounds;
    }

    public void SetCameraPosition( Point position )
    {
        camera.SetPosition( position );
    }

    // events
    public boolean KeyPressed( int key )
    {
        synchronized( keys_pressed )
        {
            return keys_pressed.getOrDefault( key, false );
        }
    }

    public boolean[] GetMouseButtons()
    {
        synchronized( mouse_events )
        {
            return mouse_buttons;
        }
    }

    public Point GetMousePosition()
    {
        Point mouse_location = MouseInfo.getPointerInfo().getLocation();
        Point win_location = win.getLocation();
        Point panel_location = win.getComponent(0).getLocation();

        int wh = lge.camera.rect.height;
        int x = mouse_location.x - ( win_location.x + panel_location.x );
        int y = wh - ( mouse_location.y - ( win_location.y + panel_location.y ) );

        return new Point( x, y );
    }

    public Point GetMouseClicked( int button )
    {
        synchronized( mouse_events )
        {
            for( MouseEvent e : mouse_events )
                if( e.getID() == MouseEvent.MOUSE_CLICKED )
                    if( e.getButton() == button + 1 )
                    {
                        int wh = lge.camera.rect.height;
                        Point p = new Point( e.getX(), e.getY() );
                        p.y = wh - p.y;
                        return p;
                    }
        }
        return null;
    }


    // game
    public void SetOnMainUpdate( IEvents iface )
    {
        this.on_main_update = iface;
    }

    public void Quit()
    {
        running = false;
    }

    // main loop
    public void Run( int fps )
    {
        running = true;
        long tick_expected = (long)( 1000.0/fps );
        long tick_prev = System.currentTimeMillis();
        while( running )
        {
            // events
            synchronized( mouse_events ) { mouse_events.clear(); }

            // --- tiempo en ms desde el ciclo anterior
            long tick_elapsed = System.currentTimeMillis() - tick_prev;
            if( tick_elapsed < tick_expected )
                try { Thread.sleep( tick_expected - tick_elapsed  ); } catch (InterruptedException e) {}

            long now = System.currentTimeMillis();
            double dt = (now  - tick_prev)/1000.0;
            tick_prev = now;

            // --- Del gobj and gobj.OnDelete
            ArrayList<GameObject> ondelete = new  ArrayList<GameObject>();
            for( GameObject  gobj : gObjectsToDel )
            {
                gObjects.get( gobj.layer ).remove( gobj );
                if( camera.target == gobj ) camera.target = null;
                if( (gobj.on_events_enabled & E_ON_DELETE) != 0x00 ) ondelete.add( gobj );
            }
            gObjectsToDel.clear();
            for( GameObject gobj : ondelete ) gobj.OnDelete();
            ondelete = null;

            // --- Add Gobj and gobj.OnStart
            boolean reorder = false;
            ArrayList<GameObject> onstart = new ArrayList<GameObject>();
            for( GameObject  gobj : gObjectsToAdd )
            {
                Integer layer = gobj.layer;
                ArrayList<GameObject> gobjs = gObjects.get( layer );
                if( gobjs == null )
                {
                    gobjs = new ArrayList<GameObject>();
                    gObjects.put( layer, gobjs );
                }
                if( !gobjs.contains( gobj ) )
                {
                    gobjs.add( gobj );
                    reorder = true;
                    if( (gobj.on_events_enabled & E_ON_START) != 0x00 ) onstart.add( gobj );
                }
            }
            gObjectsToAdd.clear();
            for( GameObject gobj : onstart ) gobj.OnStart();
            onstart = null;

            // ---
            if( reorder )
            {
                //Engine.gObjects = dict( sorted( Engine.gObjects.items() ) )
                reorder = false;
            }
            // --

            // --- gobj.OnPreUpdate
            for( Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet() )
            {
                for( GameObject gobj : elem.getValue() )
                {
                    if( (gobj.on_events_enabled & E_ON_PRE_UPDATE) != 0x00 )
                    gobj.OnPreUpdate( dt );
                }
            }

            // --- gobj.OnUpdate
            for( Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet() )
            {
                for( GameObject gobj : elem.getValue() )
                {
                    if( (gobj.on_events_enabled & E_ON_UPDATE) != 0x00 )
                    gobj.OnUpdate( dt );
                }
            }

            // --- gobj.OnPostUpdate
            for( Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet() )
            {
                for( GameObject gobj : elem.getValue() )
                {
                    if( (gobj.on_events_enabled & E_ON_POST_UPDATE) != 0x00 )
                    gobj.OnPostUpdate( dt );
                }
            }

            // --- game.OnMainUpdate
            if( on_main_update != null ) on_main_update.OnMainUpdate( dt );

            // --- gobj.OnCollision
            LinkedHashMap<GameObject, ArrayList<GameObject>> oncollisions = new LinkedHashMap<GameObject, ArrayList<GameObject>>();
            for( Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet() )
            {
                int layer = elem.getKey();
                if( layer != GUI_LAYER )
                {
                    for( GameObject gobj1 : elem.getValue() )
                    {
                        ArrayList<GameObject> colliders = new ArrayList<GameObject>();
                        if( !gobj1.use_colliders ) continue;
                        for( GameObject gobj2 : elem.getValue() )
                        {
                            if( gobj1 == gobj2 ) continue;
                            if( !gobj2.use_colliders ) continue;
                            if( !gobj1.rect.intersects( gobj2.rect ) ) continue;
                            colliders.add( gobj2 );
                        }
                        if( colliders.size() > 0 ) oncollisions.put( gobj1, colliders );

                    }
                }
            }
            for( Entry<GameObject, ArrayList<GameObject>> elem : oncollisions.entrySet() )
                elem.getKey().OnCollision( dt, elem.getValue() );

            // --- gobj.OnPreRender
            for( Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet() )
            {
                for( GameObject gobj : elem.getValue() )
                {
                    if( (gobj.on_events_enabled & E_ON_PRE_RENDER) != 0x00 )
                    gobj.OnPreRender( dt );
                }
            }

            // --- Camera Tracking
            camera.FollowTarget();

            //# -- Rendering
            Graphics2D g2d = screen.createGraphics();
            g2d.setColor( bgColor);
            g2d.fillRect( 0, 0, screen.getWidth(), screen.getHeight() );

            int vh = (int)camera.rect.getHeight();
            for( Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet() )
            {
                int layer = elem.getKey();

                    // -- Layer Rendering
                if( layer != GUI_LAYER )
                {
                    for( GameObject gobj : elem.getValue() )
                    {
                        Point p = Fix_XY( gobj );
                        BufferedImage surface = gobj.surface;
                        if( surface != null )
                            g2d.drawImage( surface, p.x, p.y, null );

                        if( collidersColor != null && gobj.use_colliders )
                        {
                            g2d.setColor( collidersColor );
                            g2d.drawRect( p.x, p.y, gobj.rect.width,gobj.rect.height );
                        }
                    }
                }
                    // --- GUI rendering
                else
                {
                    for( GameObject gobj : elem.getValue() )
                    {
                        BufferedImage surface = gobj.surface;
                        if( surface != null )
                        {
                            int x = gobj.rect.x;
                            int y = gobj.rect.y;
                            //int w = gobj.width;
                            int h = gobj.rect.height;
                            g2d.drawImage( surface, x, vh - y - h, null );
                        }
                    }
                }
            }
            g2d.dispose();

            // ---
            win.getComponent(0).getGraphics().drawImage( screen, 0, 0, null );
        }

        // --- gobj.OnQuit
        for( Entry<Integer, ArrayList<GameObject>> elem : gObjects.entrySet() )
        {
            for( GameObject gobj : elem.getValue() )
            {
                if( (gobj.on_events_enabled & E_ON_PRE_UPDATE) != 0x00 )
                gobj.OnQuit();
            }
        }

        //# eso es todo
        win.dispose();
    }

    // sistema cartesiano y zona visible dada por la camara
    private Point Fix_XY( GameObject gobj  )
    {
        int xo = gobj.rect.x;
        int yo = gobj.rect.y;
        //int wo = (int)gobj.rect.width;
        int ho = gobj.rect.height;

        int wh = VLIMIT;

        int vx = camera.rect.x;
        int vy = camera.rect.y;
        //int vw = (int)camera.rect.width;
        int vh = camera.rect.height;

        int dy = wh - (vy + vh);
        int x = xo - vx;
        int y = wh - (yo + ho) - dy;

        return new Point( x, y );
    }


    // --- Recursos

    // fonts
    public String[] GetSysFonts()
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();

        String[] sys_fonts = new String[fonts.length];
        for( int i=0; i < fonts.length; i++  )
            sys_fonts[i] = fonts[i].getFontName();
        return sys_fonts;
    }

    public void LoadSysFont( String name, String fname, int fstyle, int fsize )
    {
        if( fonts.get( name ) == null )
        {
            Font f = new Font( fname, fstyle, fsize );
            fonts.put( name,  f );
        }
    }

    public void LoadTTFFont( String name, String fname, int fstyle, int fsize )
    {
        if( fonts.get( name ) == null )
        {
            Font font = null;
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try
            {
                File file = new File( fname );
                font = Font.createFont( Font.TRUETYPE_FONT, new FileInputStream( file ) );
            }
            catch( Exception e )
            {
                System.out.println( fname );
                System.out.println( e );
                System.exit( 1 );
            }
            Font f = font.deriveFont( fstyle, fsize );
            ge.registerFont( f );
            fonts.put( name,  f );
        }
    }

    public Font GetFont( String fname )
    {
        return fonts.get( fname );
    }


    // sonidos
    public void LoadSound( String name, String fname )
    {
        byte[] data = sounds.get( name );
        if( data == null )
        {
            try
            {
                FileInputStream fis = new FileInputStream( fname );
                data = fis.readAllBytes();
                fis.close();

                sounds.put( name,  data );
            }
            catch( Exception e )
            {
                System.out.println( "LoadSound: " + e );
            }
        }
    }

    public void PlaySound( String name, boolean loop )
    {
        byte[] data = sounds.get( name );
        if( data == null ) return;

        try
        {
            ByteArrayInputStream bis = new ByteArrayInputStream( data );
            AudioInputStream ais = AudioSystem.getAudioInputStream( bis );

            Clip clip = AudioSystem.getClip();
            clip.open( ais );
            if( loop ) clip.loop( Clip.LOOP_CONTINUOUSLY );
            else clip.start();
        }
        catch( Exception e )
        {
            System.out.println( "PlaySound: " + e );
        }
    }

    public void StopSound( String name )
    {
    }

    public void SetSoundVolume( String name, double volume )
    {
    }

    public void GetSoundVolume( String name )
    {
    }


    // imagenes
    public ArrayList<BufferedImage> GetImages( String iname )
    {
        return images.get( iname );
    }

    public void LoadImage( String iname, String pattern, boolean flipX, boolean flipY  )
    {
        LoadImage( iname, pattern, 1, new Dimension( 0, 0 ), flipX, flipY );
    }

    public void LoadImage( String iname, String pattern, Dimension size, boolean flipX, boolean flipY  )
    {
        LoadImage( iname, pattern, 0, size, flipX, flipY );
    }

    public void LoadImage( String iname, String pattern, double scale, boolean flipX, boolean flipY  )
    {
        LoadImage( iname, pattern, scale, new Dimension( 0, 0 ), flipX, flipY );
    }

    private void LoadImage( String iname, String pattern, double scale, Dimension size, boolean flipX, boolean flipY  )
    {
        ArrayList<BufferedImage> images = ReadImages( pattern );

        if( size.width > 0 && size.height > 0 )
        {
            for( int i = 0; i < images.size(); i++ )
            {
                BufferedImage img = images.get( i );
                Image scaled_image = img.getScaledInstance( size.width, size.height, BufferedImage.SCALE_SMOOTH );
                BufferedImage bi = new BufferedImage( scaled_image.getWidth(null), scaled_image.getHeight(null), BufferedImage.TYPE_INT_ARGB );
                Graphics2D g2d = bi.createGraphics();

                int w = scaled_image.getWidth(null);
                int h = scaled_image.getHeight(null);
                if( flipX ) g2d.drawImage( scaled_image, w, 0, -w, h, null );
                if( flipY ) g2d.drawImage( scaled_image, 0, h, w, -h, null );
                if( !flipX && !flipY ) g2d.drawImage( scaled_image, 0, 0, null );

                g2d.dispose();
                images.set( i, bi );
            }
        }
        else if( scale > 0 && scale != 1 )
        {
            for( int i = 0; i < images.size(); i++ )
            {
                BufferedImage img = images.get( i );
                int width = (int)Math.round( img.getWidth()*scale );
                int height = (int)Math.round( img.getHeight()*scale );
                Image scaled_image = img.getScaledInstance( width, height, BufferedImage.SCALE_SMOOTH );
                BufferedImage bi = new BufferedImage( scaled_image.getWidth(null), scaled_image.getHeight(null), BufferedImage.TYPE_INT_ARGB );
                Graphics2D g2d = bi.createGraphics();

                int w = scaled_image.getWidth(null);
                int h = scaled_image.getHeight(null);
                if( flipX ) g2d.drawImage( scaled_image, w, 0, -w, h, null );
                if( flipY ) g2d.drawImage( scaled_image, 0, h, w, -h, null );
                if( !flipX && !flipY ) g2d.drawImage( scaled_image, 0, 0, null );

                g2d.dispose();
                images.set( i, bi );
            }
        }

        this.images.put( iname, images );
    }

    private ArrayList<BufferedImage> ReadImages( String pattern )
    {
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

        String dir = "";
        int pos = pattern.lastIndexOf( '/' );
        if( pos > -1 )
        {
            dir = pattern.substring(0 , pos );
            pattern = pattern.substring( pos + 1 );
        }

        ArrayList<String> fnames = new ArrayList<String>();
        Path p = Path.of( dir );
        DirectoryStream<Path> paths = null;
        try
        {
            paths = Files.newDirectoryStream( p, pattern );
        }
        catch( Exception e )
        {
            System.out.println( pattern );
            System.out.println( e );
            System.exit( 1 );
        }
        for( Path path : paths )
            fnames.add( path.toString() );

        Collections.sort( fnames );
        for( String fname : fnames )
        {
            BufferedImage img = ReadImage( fname );
            images.add( img );
        }
        return images;
    }

    private BufferedImage ReadImage( String fname )
    {
        File f = new File( fname );
        BufferedImage img = null;
        try
        {
            img = ImageIO.read( f );
        }
        catch( Exception e )
        {
            System.out.println( fname );
            System.out.println( e );
            System.exit( 1 );
        }
        return img;
    }

}
