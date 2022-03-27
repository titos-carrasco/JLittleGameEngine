package rcr.lge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Canvas extends GameObject
{
    public Canvas( Point origin, Dimension size )
    {
        this( origin, size, null );
    }

    public Canvas( Point origin, Dimension size, String name )
    {
        super( origin, size, name );
        surface = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_ARGB );
    }

    public void Fill( Color color )
    {
        Graphics2D g2d = surface.createGraphics();
        g2d.setBackground( color );
        g2d.clearRect( 0, 0, rect.width, rect.height  );
     }

    public void DrawText( String text, Point position, String fname, Color color )
    {
        int x = position.x;
        int y = position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );

        Font f = LittleGameEngine.GetLGE().GetFont( fname );

        g2d.setColor( color );
        g2d.setFont( f );
        g2d.drawString( text, x, rect.height - y );
        g2d.dispose();
    }

    public void DrawPoint( Point position, Color color )
    {
        //x, y = position
        //y = self._rect.GetSize()[1] - y
        //pygame.draw.circle( self._surface, color, ( x, y ), 0, 0 )
    }

    public void DrawCircle( Point position, int radius, Color color, boolean thickness )
    {
        //x, y = position
        //if( radius <= 0 ): radius = 1
        //thickness = 1 if thickness else 0

        //y = self._rect.GetSize()[1] - y
        //pygame.draw.circle( self._surface, color, ( x, y ), radius, thickness )
    }

    public void DrawRectangle( Point position, Dimension sise, Color color, boolean thickness )
    {
        //x, y = position
        //w, h = size
        //if( w <= 0 ): w = 1
        //if( h <= 0 ): h = 1
        //thickness = 1 if thickness else 0

        //y = self._rect.GetSize()[1] - h - y
        //pygame.draw.rect( self._surface, color, pygame.Rect( (x,y), (w,h) ), thickness )
    }

    public void DrawSurface( Point position, BufferedImage surface )
    {
        //x, y = position
        //w, h = self.GetSize()
        //self._surface.blit( surface, (x, h - surface.get_height() - y) )
    }

}
