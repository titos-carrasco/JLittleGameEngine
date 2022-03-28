package test.simple.demo05;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Colliders implements IEvents
{
    LittleGameEngine lge;

    public Colliders()
    {
        String resource_dir = this.getClass().getResource( "" ).getPath() + "../../resources";

        // creamos el juego
        Dimension win_size = new Dimension( 640, 480 );

        lge = LittleGameEngine.Init( win_size, "Colliders", new Color( 0xFFFF00 ) );
        lge.ShowColliders( new Color( 0xFF0000 ) );
        lge.SetOnMainUpdate( this );

        // cargamos los recursos que usaremos
        lge.LoadImage( "fondo", resource_dir + "/images/Backgrounds/FreeTileset/Fondo.png", false, false );
        lge.LoadImage( "heroe_idle_right", resource_dir + "/images/Swordsman/Idle/Idle_0*.png", 0.16, false, false );
        lge.LoadImage( "heroe_idle_left", resource_dir + "/images/Swordsman/Idle/Idle_0*.png", 0.16, true, false );
        lge.LoadImage( "heroe_run_right", resource_dir + "/images/Swordsman/Run/Run_0*.png", 0.16, false, false );
        lge.LoadImage( "heroe_run_left", resource_dir + "/images/Swordsman/Run/Run_0*.png", 0.16, true, false );
        lge.LoadImage( "ninja", resource_dir + "/images/Swordsman/Idle/Idle_000.png", 0.16, false, false );
        lge.LoadImage( "mute", resource_dir + "/images/icons/sound-*.png", false, false );
        lge.LoadTTFFont( "monospace.plain.16", resource_dir + "/fonts/FreeMono.ttf", Font.PLAIN, 16 );
        lge.LoadSound( "fondo", resource_dir + "/sounds/happy-and-sad.wav" );
        lge.LoadSound( "aves", resource_dir + "/sounds/bird-thrush-nightingale.wav" );
        lge.LoadSound( "poing", resource_dir + "/sounds/cartoon-poing.wav" );

        // activamos la musica de fondo
        lge.SetSoundVolume( "fondo", 0.5 );
        lge.PlaySound( "fondo", true );

        // agregamos el fondo
        Sprite fondo = new Sprite( "fondo", new Point( 0, 0 ), "fondo" );
        lge.AddGObject( fondo, 0 );

        // agregamos un ninja
        Sprite ninja = new Sprite( "ninja", new Point (350,250), "ninja" );
        ninja.UseColliders( true );
        lge.AddGObject( ninja, 1 );

        // agregamos al heroe
        MiHeroe heroe = new MiHeroe();
        heroe.UseColliders( true );
        lge.AddGObject( heroe, 1 );

        // agregamos la barra de info
        Canvas infobar = new Canvas( new Point( 0, 460), new Dimension( 640, 20 ), "infobar" );
        lge.AddGObjectGUI( infobar );

        // agregamos el icono del sonido
        Sprite mute = new Sprite( "mute", new Point( 8, 463 ), "mute" );
        mute.SetShape( "mute", 1 );
        lge.AddGObjectGUI( mute );

        //# configuramos la camara
        lge.SetCameraBounds( new Rectangle( 0, 0, 1920, 1056 ) );

        // establecemos que la camara siga al heroe
        lge.SetCameraTarget( heroe, true );
    }

    @Override
    public void OnMainUpdate( double dt )
    {
        // abortamos con la tecla Escape
        if( lge.KeyPressed( KeyEvent.VK_ESCAPE ) ) lge.Quit();

        // mostramos la info
        Point mouse_position = lge.GetMousePosition();
        boolean[] mouse_buttons = lge.GetMouseButtons();

        String info = String.format( "FPS: %07.2f - gObjs: %03d - Mouse: (%3d,%3d) (%d,%d,%d)",
                                    1.0/dt,
                                    lge.GetGObjects().length,
                                    mouse_position.x, mouse_position.y,
                                    mouse_buttons[0] ? 1 : 0, mouse_buttons[1] ? 1 : 0, mouse_buttons[2] ? 1 : 0 );
        Canvas infobar = (Canvas)lge.GetGObject( "infobar" );
        infobar.Fill( new Color( 0x10202020, true ) );
        infobar.DrawText( info, new Point( 50, 5 ), "monospace.plain.16", Color.BLACK );

        // mute on/mute off
        mouse_position = lge.GetMouseClicked( 0 );
        if( mouse_position != null )
        {
            if( mouse_position.x >= 8 && mouse_position.x <= 20 && mouse_position.y >= 463 && mouse_position.y <= 475 )
            {
                Sprite mute = (Sprite)lge.GetGObject( "mute");
                mute.NextShape( 0,  0 );
            }
        }

        // de manera aleatorio activamos sonido de aves
        int n = (int)( Math.random()*1000 );
        //if( n < 3 )
        //    lge.PlaySound( "aves", false );

    }

    // main loop
    public void Run( int fps )
    {
        lge.Run( fps );
    }

    // show time
    public static void main ( String[] args )
    {
        Colliders game = new Colliders();
        game.Run( 60 );
        System.out.println( "Eso es todo!!! ");
    }

}
