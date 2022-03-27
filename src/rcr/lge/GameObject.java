package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.UUID;

public class GameObject
{
    protected Rectangle rect;
    protected BufferedImage surface = null;
    protected int layer = -1;
    protected boolean use_colliders = false;
    protected String name;
    protected String tag = "";
    protected int on_events_enabled = 0x00;

    public GameObject( Point origin, Dimension size )
    {
        this( origin, size, null );
    }

    public GameObject( Point origin, Dimension size, String name )
    {
        if( name == null ) name = "__no_name__" + UUID.randomUUID().toString();
        rect = new Rectangle( origin, size );
        this.name = name;
    }

    public Point GetPosition()
    {
        return rect.getLocation();
    }

    public Dimension GetSize()
    {
        return rect.getSize();
    }

    public String GetName()
    {
        return name;
    }

    public String GetTag()
    {
        return tag;
    }

    public void SetPosition( Point position )
    {
        this.rect.setLocation( position );
    }

    public void SetTag( String tag )
    {
        this.tag = tag;
    }

    public void UseColliders( boolean use_colliders )
    {
        this.use_colliders = use_colliders;
    }

    // manejo de eventos
    public void SetOnEvents( int on_events_enabled )
    {
        this.on_events_enabled = on_events_enabled;
    }

    public void OnDelete(){};
    public void OnStart(){};
    public void OnPreUpdate( double dt ){};
    public void OnUpdate( double dt ){};
    public void OnPostUpdate( double dt ){};
    public void OnCollision( double dt ){};
    public void OnPreRender( double dt ){};
    public void OnQuit() {};

}
