package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Sprite extends GameObject
{
    private LinkedHashMap<String, ArrayList<BufferedImage>> surfaces;
    private String iname;
    private int idx;
    private double elapsed = 0;

    public Sprite( String iname, Point position  )
    {
        this( new String[]{ iname }, position, null );
    }

    public Sprite( String iname, Point position, String name )
    {
        this( new String[]{ iname }, position, name );
    }

    public Sprite( String[] inames, Point position )
    {
        this( inames, position, null );
    }

    public Sprite( String[] inames, Point position, String name )
    {
        super( position, new Dimension( 0, 0 ), name );
        surfaces = new LinkedHashMap<String, ArrayList<BufferedImage>>();

        for( String iname : inames )
            surfaces.put( iname, LittleGameEngine.GetLGE().GetImages( iname ) );

        Entry <String, ArrayList<BufferedImage>> elem = surfaces.entrySet().iterator().next();
        this.iname = elem.getKey();
        this.idx = 0;
        this.surface = elem.getValue().get(0);
        this.rect.setSize( this.surface.getWidth(), this.surface.getHeight() );
    }

    public String GetCurrentIName()
    {
        return iname;
    }

    public int GetCurrentIdx()
    {
        return idx;
    }

    public void NextShape( double dt, double delay )
    {
        elapsed= elapsed + dt;
        if( elapsed < delay ) return;

        elapsed = 0;
        idx = idx + 1;
        if( idx >= surfaces.get( iname ).size() ) idx = 0;

        surface = surfaces.get( iname ).get( idx );
        this.rect.setSize( this.surface.getWidth(), this.surface.getHeight() );
    }

    public void SetShape( String iname, int idx )
    {
        this.elapsed = 0;
        this.iname = iname;
        this.idx = idx;

        surface = surfaces.get( iname ).get( idx );
        this.rect.setSize( this.surface.getWidth(), this.surface.getHeight() );
    }
}
