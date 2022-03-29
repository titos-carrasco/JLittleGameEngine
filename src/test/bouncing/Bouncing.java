package test.bouncing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;

public class Bouncing implements IEvents {
    private LittleGameEngine lge;
    private Canvas ground;

    public Bouncing() {
        // la ruta a los recursos del juego
        String resource_dir = getClass().getResource("../resources").getPath();

        // creamos el juego
        Dimension win_size = new Dimension(800, 600);

        lge = LittleGameEngine.Init(win_size, "Bouncing Balls", new Color(0xFFFFFF));
        lge.ShowColliders(new Color(0xFF0000));
        lge.SetOnMainUpdate(this);
        lge.SetOnEvents(LittleGameEngine.E_ON_UPDATE | LittleGameEngine.E_ON_COLLISION);

        // cargamos los recursos que usaremos
        lge.LoadTTFFont("monospace.plain.16", resource_dir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);

        // agregamos el suelo
        ground = new Canvas(new Point(0, 0), new Dimension(800, 100), "ground");
        ground.Fill(Color.GRAY);
        ground.SetTag("ground");
        ground.UseColliders(true);
        lge.AddGObject(ground, 1);

        // los objetos a rebotar
        for (int i = 0; i < 50; i++) {
            Ball gobj = new Ball();
            lge.AddGObject(gobj, 1);
        }

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 580), new Dimension(800, 20), "infobar");
        lge.AddGObjectGUI(infobar);

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
    }

    // main loop
    public void Run(int fps) {
        lge.Run(fps);
    }

    // show time
    public static void main(String[] args) {
        Bouncing game = new Bouncing();
        game.Run(60);
        System.out.println("Eso es todo!!! ");
    }

}
