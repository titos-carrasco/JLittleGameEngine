package test.birds;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Birds implements IEvents {
    private LittleGameEngine lge;

    public Birds() {
        // creamos el juego
        Dimension win_size = new Dimension(800, 440);

        lge = new LittleGameEngine(win_size, "Birds", new Color(0xFFFF00));
        lge.SetOnMainUpdate(this);

        // cargamos los recursos que usaremos
        String resource_dir = lge.GetRealPath(this, "../resources");

        lge.LoadImage("fondo", resource_dir + "/images/Backgrounds/FreeTileset/Fondo.png", win_size, false, false);
        lge.LoadImage("heroe", resource_dir + "/images/Swordsman/Idle/Idle_0*.png", 0.08, false, false);
        lge.LoadImage("mute", resource_dir + "/images/icons/sound-*.png", false, false);
        lge.LoadImage("bird", resource_dir + "/images/BlueBird/frame-*.png", 0.04, false, false);
        lge.LoadTTFFont("backlash.plain.40", resource_dir + "/fonts/backlash.ttf", Font.PLAIN, 40);
        lge.LoadSound("fondo", resource_dir + "/sounds/happy-and-sad.wav");

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new Point(0, 0), "fondo");
        lge.AddGObject(fondo, 0);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 420), new Dimension(800, 20), "infobar");
        lge.AddGObjectGUI(infobar);

        // agregamos al heroe
        Sprite heroe = new Sprite("heroe", new Point(226, 142), "Heroe");
        lge.AddGObject(heroe, 1);

        // agregamos pajaros
        for (int i = 0; i < 500; i++) {
            int x = (int) (Math.random() * win_size.width);
            int y = (int) (Math.random() * (win_size.height - 40));
            Bird bird = new Bird("bird", new Point(x, y));
            lge.AddGObject(bird, 1);
        }
    }

    @Override
    public void OnMainUpdate(double dt) {
        // abortamos con la tecla Escape
        if (lge.KeyPressed(KeyEvent.VK_ESCAPE))
            lge.Quit();

        // mostramos la info
        Point mouse_position = lge.GetMousePosition();
        boolean[] mouse_buttons = lge.GetMouseButtons();

        String info = String.format("FPS: %07.2f - gObjs: %03d - Mouse: (%3d,%3d) (%d,%d,%d)", lge.GetFPS(),
                lge.GetCountGObjects(), mouse_position.x, mouse_position.y, mouse_buttons[0] ? 1 : 0,
                mouse_buttons[1] ? 1 : 0, mouse_buttons[2] ? 1 : 0);
        Canvas infobar = (Canvas) lge.GetGObject("infobar");
        infobar.Fill(new Color(0x10202020, true));
        infobar.DrawText(info, new Point(140, 5), "monospace.plain.16", Color.BLACK);
    }

    // main loop
    public void Run(int fps) {
        lge.Run(fps);
    }

    // show time
    public static void main(String[] args) {
        Birds game = new Birds();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
