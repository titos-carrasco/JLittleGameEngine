package test.cementerio;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Game implements IEvents {
    private LittleGameEngine lge;

    public Game() {
        Dimension winSize = new Dimension(640, 342);

        lge = new LittleGameEngine(winSize, "El Cementerio", new Color(0, 0, 0));
        lge.setOnMainUpdate(this);
        lge.showColliders(new Color(255, 0, 0));

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "./resources");

        lge.loadImage("fondo", resourceDir + "/fondo.png", false, false);
        lge.loadImage("ninja-idle-right", resourceDir + "/NinjaGirl/Idle_*.png", 0.1, false, false);
        lge.loadImage("ninja-idle-left", resourceDir + "/NinjaGirl/Idle_*.png", 0.1, true, false);
        lge.loadImage("ninja-run-right", resourceDir + "/NinjaGirl/Run_*.png", 0.1, false, false);
        lge.loadImage("ninja-run-left", resourceDir + "/NinjaGirl/Run_*.png", 0.1, true, false);
        lge.loadImage("platform", resourceDir + "/platform.png", 0.3, false, false);

        // el fondo
        Sprite fondo = new Sprite("fondo", new Point(0, 0));
        lge.addGObject(fondo, 0);

        // los NonPlayer Characters (NPC)
        makeFloor();
        makePlatforms();

        // nuestra heroina
        Ninja ninja = new Ninja(90, 163);
        ninja.setBounds(new Rectangle(new Point(0, 0), new Dimension(winSize.width, winSize.height + 100)));
        lge.addGObject(ninja, 1);
    }

    public void makeFloor() {
        Canvas[] suelos = new Canvas[] { new Canvas(new Point(0, 85), new Dimension(170, 1)),
                new Canvas(new Point(0, 214), new Dimension(170, 1)),
                new Canvas(new Point(214, 300), new Dimension(128, 1)),
                new Canvas(new Point(342, 214), new Dimension(127, 1)),
                new Canvas(new Point(470, 257), new Dimension(127, 1)),
                new Canvas(new Point(513, 86), new Dimension(127, 1)) };

        for (Canvas s : suelos) {
            s.enableCollider(true);
            s.setTag("suelo");
            lge.addGObject(s, 1);
        }
    }

    public void makePlatforms() {
        Platform[] platforms = new Platform[] { new Platform(200, 200, 'U', 100, 1),
                new Platform(400, 100, 'L', 100, 1) };
        for (Platform p : platforms) {
            lge.addGObject(p, 1);
        }
    }

    @Override
    public void onMainUpdate(double dt) {
        // abortamos con la tecla Escape
        if (lge.keyPressed(KeyEvent.VK_ESCAPE))
            lge.quit();
    }

    // main loop
    public void Run(int fps) {
        lge.run(fps);
    }

    // show time
    public static void main(String[] args) {
        Game game = new Game();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
