package test.simple.demo04;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class MiHeroe extends Sprite
{
    private LittleGameEngine lge;
    private int state;

    public MiHeroe()
    {
        super( new String[] { "heroe_idle_right","heroe_idle_left","heroe_run_right","heroe_run_left" }, new Point( 550, 346 ), "Heroe" );

        // acceso al motor de juegos
        lge = LittleGameEngine.GetLGE();

        // sus atributos
        SetOnEvents( LittleGameEngine.E_ON_UPDATE );
        SetShape( "heroe_idle_right", 0 );
        state = 1;
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

        // cambiamos sus coordenadas, orientacion e imagen segun la tecla presionada
        if( lge.KeyPressed( KeyEvent.VK_RIGHT ) )
        {
            position.x = (int)(position.x + pixels);
            if( state != 2 )
            {
                SetShape( "heroe_run_right", 0 );
                state = 2;
            }
        }
        else if( lge.KeyPressed( KeyEvent.VK_LEFT ) )
        {
            position.x = (int)(position.x - pixels);
            if( state != -2 )
            {
                SetShape( "heroe_run_left", 0 );
                state = -2;
            }
        }
        else if( state == 2 )
        {
            if( state != 1 )
            {
                SetShape( "heroe_idle_right", 0 );
                state = 1;
            }
        }
        else if( state == -2 )
        {
            if( state != -1 )
            {
                SetShape( "heroe_idle_left", 0 );
                state = -1;
            }
        }

        if( lge.KeyPressed( KeyEvent.VK_UP ) )
            position.y = (int)(position.y + pixels);
        else if( lge.KeyPressed( KeyEvent.VK_DOWN ) )
            position.y = (int)(position.y - pixels);

        // siguiente imagen de la secuencia
        NextShape( dt, 0.050 );

        // lo posicionamos
        SetPosition( position );
    }
}
