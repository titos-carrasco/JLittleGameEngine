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
        // creamos el juego
        Dimension winSize = new Dimension(800, 440);

        lge = new LittleGameEngine(winSize, "Bouncing Balls", new Color(0xFFFFFF));
        lge.setOnMainUpdate(this);
        // lge.showColliders(new Color(0xFF0000));

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "../resources");

        lge.loadTTFFont("monospace.plain.16", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);

        // agregamos el suelo
        ground = new Canvas(new Point(0, 340), new Dimension(800, 100), "ground");
        ground.fill(Color.GRAY);
        ground.setTag("ground");
        ground.enableCollider(true);
        lge.addGObject(ground, 1);

        // los objetos a rebotar
        for (int i = 0; i < 200; i++) {
            int x = (int) (50 + Math.random() * 700);
            int y = (int) (50 + Math.random() * 150);
            double vx = -50 + Math.random() * 100;
            double vy = 0;
            Ball gobj = new Ball(x, y, vx, vy);
            lge.addGObject(gobj, 1);
        }

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 0), new Dimension(800, 20), "infobar");
        lge.addGObjectGUI(infobar);

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
        Bouncing game = new Bouncing();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
