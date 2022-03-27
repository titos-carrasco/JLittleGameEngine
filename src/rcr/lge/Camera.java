package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;


public class Camera
{

    protected Rectangle rect;
    protected Rectangle bounds;

    protected GameObject target;
    protected boolean target_center;


    protected Camera( Point position, Dimension size )
    {
        rect = new Rectangle( position, size );
        bounds = null;
        target = null;
        target_center = true;
    }

    protected void SetPosition( Point position )
    {
        rect.setLocation( position );
        if( bounds != null )
        ;
        //self._rect.KeepInsideRectangle( self._bounds )
    }

    protected void SetBounds( Rectangle bounds )
    {
        this.bounds = bounds;
    }

    protected void FollowTarget()
    {
        // nadie a quien seguir
        if( target == null ) return;

        // el centro de la camara en el centro del gobj
        Point position = target.GetPosition();
        int x = position.x;
        int y = position.y;

        if( target_center )
        {
            Dimension size = target.rect.getSize();
            x = x + size.width/2;
            y = y + size.height/2;
        }

        rect.setLocation( x - rect.width/2, y-rect.height/2 );
    }

}
