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
        Dimension winSize = new Dimension(800, 440);

        lge = new LittleGameEngine(winSize, "Birds", new Color(0xFFFFFF));
        lge.setOnMainUpdate(this);
        // lge.showColliders(new Color(255, 0, 0));

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "../resources");

        lge.loadImage("fondo", resourceDir + "/images/Backgrounds/FreeTileset/Fondo.png", winSize, false, false);
        lge.loadImage("heroe", resourceDir + "/images/Swordsman/Idle/Idle_0*.png", 0.08, false, false);
        lge.loadImage("mute", resourceDir + "/images/icons/sound-*.png", false, false);
        lge.loadImage("bird", resourceDir + "/images/BlueBird/frame-*.png", 0.04, false, false);
        lge.loadTTFFont("backlash.plain.40", resourceDir + "/fonts/backlash.ttf", Font.PLAIN, 40);
        lge.loadSound("fondo", resourceDir + "/sounds/happy-and-sad.wav");

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new Point(0, 0), "fondo");
        lge.addGObject(fondo, 0);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 0), new Dimension(800, 20), "infobar");
        lge.addGObjectGUI(infobar);

        // agregamos al heroe
        Sprite heroe = new Sprite("heroe", new Point(226, 254), "Heroe");
        lge.addGObject(heroe, 1);

        // agregamos pajaros
        for (int i = 0; i < 500; i++) {
            int x = (int) (Math.random() * winSize.width);
            int y = (int) (Math.random() * winSize.height);
            Bird bird = new Bird("bird", new Point(x, y));
            // bird.enableCollider(true);
            lge.addGObject(bird, 1);
        }
    }

    @Override
    public void onMainUpdate(double dt) {
        // abortamos con la tecla Escape
        if (lge.keyPressed(KeyEvent.VK_ESCAPE))
            lge.quit();

        // mostramos la info
        Point mousePosition = lge.getMousePosition();
        boolean[] mouseButtons = lge.getMouseButtons();

        String info = String.format("FPS: %07.2f - gObjs: %03d - Mouse: (%3d,%3d) (%d,%d,%d)", lge.getFPS(),
                lge.getCountGObjects(), mousePosition.x, mousePosition.y, mouseButtons[0] ? 1 : 0,
                mouseButtons[1] ? 1 : 0, mouseButtons[2] ? 1 : 0);
        Canvas infobar = (Canvas) lge.getGObject("infobar");
        infobar.fill(new Color(0x10202020, true));
        infobar.drawText(info, new Point(140, 16), "monospace.plain.16", Color.BLACK);
    }

    // main loop
    public void Run(int fps) {
        lge.run(fps);
    }

    // show time
    public static void main(String[] args) {
        Birds game = new Birds();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
