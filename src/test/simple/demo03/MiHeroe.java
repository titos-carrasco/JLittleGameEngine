package test.simple.demo03;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class MiHeroe extends Sprite
{
    private int heading;
    private LittleGameEngine lge;

    public MiHeroe()
    {
        super( new String[] { "heroe_right","heroe_left" }, new Point( 550, 346 ), "Heroe" );
        SetShape( "heroe_right", 0 );
        heading = 1;
        this.SetOnEvents( LittleGameEngine.E_ON_UPDATE );

        // acceso al motor de juegos
        lge = LittleGameEngine.GetLGE();

        // para posicionarlo dentro de los limites
        SetBounds( new Rectangle( 0, 0, 1920, 1056 ) );
    }

    @Override
    public void OnUpdate( double dt )
    {
        // velocity = pixeles por segundo
        int velocity = 240;
        double pixels = velocity*dt;

        // la posiciona actual del heroe
        Point position = GetPosition();

        // cambiamos sus coordenadas segun la tecla presionada
        if( lge.KeyPressed( KeyEvent.VK_RIGHT ) )
        {
            position.x = (int)(position.x + pixels);
            if( heading != 1 )
            {
                SetShape( "heroe_right", 0 );
                heading = 1;
            }
        }
        else if( lge.KeyPressed( KeyEvent.VK_LEFT ) )
        {
            position.x = (int)(position.x - pixels);
            if( heading != -1 )
            {
                SetShape( "heroe_left", 0 );
                heading = -1;
            }
        }

        if( lge.KeyPressed( KeyEvent.VK_UP ) )
            position.y = (int)(position.y + pixels);
        else if( lge.KeyPressed( KeyEvent.VK_DOWN ) )
            position.y = (int)(position.y - pixels);

        // lo posicionamos
        SetPosition( position );
    }
}
