package test.birds;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.LittleGameEngine;
import rcr.lge.PointD;
import rcr.lge.Size;
import rcr.lge.Sprite;

public class Birds {
    private LittleGameEngine lge;

    public Birds(String resourceDir) {
        // creamos el juego
        Size winSize = new Size(800, 440);

        lge = new LittleGameEngine(winSize, "Birds", Color.WHITE);
        lge.onMainUpdate = (dt) -> {
            onMainUpdate(dt);
        };

        // cargamos los recursos que usaremos
        lge.imageManager.loadImages("fondo", resourceDir + "/images/Backgrounds/FreeTileset/Fondo.png", winSize, false,
                false);
        lge.imageManager.loadImages("heroe", resourceDir + "/images/Swordsman/Idle/Idle_0*.png", 0.08, false, false);
        lge.imageManager.loadImages("bird", resourceDir + "/images/BlueBird/frame-*.png", 0.04, false, false);
        lge.fontManager.loadTTFont("monospace", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new PointD(0, 0), "fondo");
        lge.addGObject(fondo, 0);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new PointD(0, 0), new Size(800, 20), "infobar");
        lge.addGObjectGUI(infobar);

        // agregamos al heroe
        Sprite heroe = new Sprite("heroe", new PointD(226, 254), "Heroe");
        lge.addGObject(heroe, 1);

        // agregamos pajaros
        for (int i = 0; i < 500; i++) {
            double x = Math.random() * winSize.width;
            double y = 15 + Math.random() * winSize.height;
            Bird bird = new Bird("bird", new PointD(x, y));
            lge.addGObject(bird, 1);
        }
    }

    public void onMainUpdate(double dt) {
        // abortamos con la tecla Escape
        if (lge.keyPressed(KeyEvent.VK_ESCAPE))
            lge.quit();

        // mostramos la info
        Point mousePosition = lge.getMousePosition();
        boolean[] mouseButtons = lge.getMouseButtons();

        String info = String.format("FPS: %07.2f - LPS: %07.2f - gObjs: %03d - Mouse: (%3d,%3d) (%d,%d,%d)",
                lge.getFPS(), lge.getLPS(), lge.getCountGObjects(), mousePosition.x, mousePosition.y,
                mouseButtons[0] ? 1 : 0, mouseButtons[1] ? 1 : 0, mouseButtons[2] ? 1 : 0);
        Canvas infobar = (Canvas) lge.getGObject("infobar");
        infobar.fill(new Color(0x10202020, true));
        infobar.drawText(info, new PointD(40, 0), "monospace", Color.BLACK);
    }

    // main loop
    public void Run(int fps) {
        lge.run(fps);
    }

    // show time
    public static void main(String[] args) {
        String resourceDir = args[0];
        Birds game = new Birds(resourceDir);
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
