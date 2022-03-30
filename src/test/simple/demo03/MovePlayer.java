package test.simple.demo03;

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

public class MovePlayer implements IEvents {
    private LittleGameEngine lge;

    public MovePlayer() {
        // la ruta a los recursos del juego
        String resource_dir = getClass().getResource("../../resources").getPath();

        // creamos el juego
        Dimension win_size = new Dimension(640, 480);

        lge = LittleGameEngine.Init(win_size, "Move Player", new Color(0xFFFF00));
        lge.ShowColliders(new Color(0xFF0000));
        lge.SetOnMainUpdate(this);
        lge.SetOnEvents(LittleGameEngine.E_ON_UPDATE);

        // cargamos los recursos que usaremos
        lge.LoadImage("fondo", resource_dir + "/images/Backgrounds/FreeTileset/Fondo.png", false, false);
        lge.LoadImage("heroe_right", resource_dir + "/images/Swordsman/Idle/Idle_000.png", 0.16, false, false);
        lge.LoadImage("heroe_left", resource_dir + "/images/Swordsman/Idle/Idle_000.png", 0.16, true, false);
        lge.LoadImage("mute", resource_dir + "/images/icons/sound-*.png", false, false);
        lge.LoadTTFFont("monospace.plain.16", resource_dir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);
        lge.LoadSound("fondo", resource_dir + "/sounds/happy-and-sad.wav");

        // activamos la musica de fondo
        lge.PlaySound("fondo", true, 50);

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new Point(0, 0), "fondo");
        lge.AddGObject(fondo, 0);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 460), new Dimension(640, 20), "infobar");
        lge.AddGObjectGUI(infobar);

        // agregamos el icono del sonido
        Sprite mute = new Sprite("mute", new Point(8, 463), "mute");
        mute.SetShape("mute", 1);
        lge.AddGObjectGUI(mute);

        // agregamos al heroe
        MiHeroe heroe = new MiHeroe();
        heroe.UseColliders(true);
        lge.AddGObject(heroe, 1);

        // # configuramos la camara
        lge.SetCameraBounds(new Rectangle(0, 0, 1920, 1056));

        // establecemos que la camara siga al heroe
        lge.SetCameraTarget(heroe, true);
    }

    @Override
    public void OnMainUpdate(double dt) {
        // abortamos con la tecla Escape
        if (lge.KeyPressed(KeyEvent.VK_ESCAPE))
            lge.Quit();

        // mostramos la info
        Point mouse_position = lge.GetMousePosition();
        boolean[] mouse_buttons = lge.GetMouseButtons();

        String info = String.format("FPS: %07.2f - gObjs: %03d - Mouse: (%3d,%3d) (%d,%d,%d)", 1.0 / dt,
                lge.GetCountGObjects(), mouse_position.x, mouse_position.y, mouse_buttons[0] ? 1 : 0,
                mouse_buttons[1] ? 1 : 0, mouse_buttons[2] ? 1 : 0);
        Canvas infobar = (Canvas) lge.GetGObject("infobar");
        infobar.Fill(new Color(0x10202020, true));
        infobar.DrawText(info, new Point(50, 5), "monospace.plain.16", Color.BLACK);

        // mute on/mute off
        mouse_position = lge.GetMouseClicked(0);
        if (mouse_position != null) {
            if (mouse_position.x >= 8 && mouse_position.x <= 20 && mouse_position.y >= 463 && mouse_position.y <= 475) {
                Sprite mute = (Sprite) lge.GetGObject("mute");
                mute.NextShape(0, 0);
                if (mute.GetCurrentIdx() == 0)
                    lge.SetSoundVolume("fondo", 0);
                else
                    lge.SetSoundVolume("fondo", 50);
            }
        }
    }

    // main loop
    public void Run(int fps) {
        lge.Run(fps);
    }

    // show time
    public static void main(String[] args) {
        MovePlayer game = new MovePlayer();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }
}
