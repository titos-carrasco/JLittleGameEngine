package test.simple.demo01;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class TheWorld implements IEvents {
    private LittleGameEngine lge;

    public TheWorld() {
        // la ruta a los recursos del juego
        String resource_dir = getClass().getResource("../../resources").getPath();

        // creamos el juego
        Dimension win_size = new Dimension(800, 440);

        lge = LittleGameEngine.Init(win_size, "The World", new Color(0xFFFF00));
        lge.ShowColliders(new Color(0xFF0000));
        lge.SetOnMainUpdate(this);

        // cargamos los recursos que usaremos
        lge.LoadImage("fondo", resource_dir + "/images/Backgrounds/FreeTileset/Fondo.png", win_size, false, false);
        lge.LoadImage("heroe", resource_dir + "/images/Swordsman/Idle/Idle_00*.png", 0.08, false, false);
        lge.LoadImage("mute", resource_dir + "/images/icons/sound-*.png", false, false);
        lge.LoadTTFFont("backlash.plain.40", resource_dir + "/fonts/backlash.ttf", Font.PLAIN, 40);
        lge.LoadTTFFont("monospace.plain.16", resource_dir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);
        lge.LoadSound("fondo", resource_dir + "/sounds/happy-and-sad.wav");

        // activamos la musica de fondo
        lge.SetSoundVolume("fondo", 0.5);
        lge.PlaySound("fondo", true);

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new Point(0, 0), "fondo");
        lge.AddGObject(fondo, 0);

        // agregamos un Sprite
        Sprite heroe = new Sprite("heroe", new Point(226, 142), "Heroe");
        heroe.UseColliders(true);
        lge.AddGObject(heroe, 1);

        // agregamos un texto con transparencia
        Canvas canvas = new Canvas(new Point(200, 110), new Dimension(400, 200));
        canvas.DrawText("Little Game Engine", new Point(30, 90), "backlash.plain.40", new Color(20, 20, 20));
        lge.AddGObjectGUI(canvas);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 420), new Dimension(800, 20), "infobar");
        lge.AddGObjectGUI(infobar);

        // agregamos el icono del sonido
        Sprite mute = new Sprite("mute", new Point(8, 423), "mute");
        mute.SetShape("mute", 1);
        lge.AddGObjectGUI(mute);
    }

    @Override
    public void OnMainUpdate(double dt) {
        // abortamos con la tecla Escape
        if (lge.KeyPressed(KeyEvent.VK_ESCAPE))
            lge.Quit();

        // mostramos la info
        Point mouse_position = lge.GetMousePosition();
        boolean[] mouse_buttons = lge.GetMouseButtons();

        String info = String.format("FPS: %07.2f - gObjs: %03d - Mouse: (%3d,%3d) (%d,%d,%d)", 1.0 / lge.GetFPS(),
                lge.GetGObjects().length, mouse_position.x, mouse_position.y, mouse_buttons[0] ? 1 : 0,
                mouse_buttons[1] ? 1 : 0, mouse_buttons[2] ? 1 : 0);
        Canvas infobar = (Canvas) lge.GetGObject("infobar");
        infobar.Fill(new Color(0x10202020, true));
        infobar.DrawText(info, new Point(140, 5), "monospace.plain.16", Color.BLACK);

        // sonido on/off
        mouse_position = lge.GetMouseClicked(0);
        if (mouse_position != null) {
            if (mouse_position.x >= 8 && mouse_position.x <= 20 && mouse_position.y >= 423 && mouse_position.y <= 435) {
                Sprite mute = (Sprite) lge.GetGObject("mute");
                mute.NextShape(0, 0);
            }
        }

        // animamos al heroe
        Sprite heroe = (Sprite) lge.GetGObject("Heroe");
        heroe.NextShape(dt, 0.060);
    }

    // main loop
    public void Run(int fps) {
        lge.Run(fps);
    }

    // show time
    public static void main(String[] args) {
        TheWorld game = new TheWorld();
        game.Run(60);
        System.out.println("Eso es todo!!! ");
    }
}
