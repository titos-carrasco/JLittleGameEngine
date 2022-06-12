package test.cementerio;

import java.awt.Color;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.LittleGameEngine;
import rcr.lge.PointD;
import rcr.lge.RectangleD;
import rcr.lge.Size;
import rcr.lge.Sprite;

public class Game {
    private LittleGameEngine lge;

    public Game(String resourceDir) {
        Size winSize = new Size(640, 342);

        lge = new LittleGameEngine(winSize, "El Cementerio", Color.BLACK);
        // lge.showColliders(Color.RED);
        lge.onMainUpdate = (dt) -> {
            onMainUpdate(dt);
        };

        // cargamos los recursos que usaremos
        lge.imageManager.loadImages("fondo", resourceDir + "/fondo.png", false, false);
        lge.imageManager.loadImages("ninja-idle-right", resourceDir + "/NinjaGirl/Idle_*.png", 0.1, false, false);
        lge.imageManager.loadImages("ninja-idle-left", resourceDir + "/NinjaGirl/Idle_*.png", 0.1, true, false);
        lge.imageManager.loadImages("ninja-run-right", resourceDir + "/NinjaGirl/Run_*.png", 0.1, false, false);
        lge.imageManager.loadImages("ninja-run-left", resourceDir + "/NinjaGirl/Run_*.png", 0.1, true, false);
        lge.imageManager.loadImages("platform", resourceDir + "/platform.png", 0.3, false, false);

        // el fondo
        Sprite fondo = new Sprite("fondo", new PointD(0, 0));
        lge.addGObject(fondo, 0);

        // los NonPlayer Characters (NPC)
        makeFloor();
        makePlatforms();

        // nuestra heroina
        Ninja ninja = new Ninja(90, 163);
        ninja.setBounds(new RectangleD(new PointD(0, 0), new Size(winSize.width, winSize.height + 100)));
        lge.addGObject(ninja, 1);
    }

    public void makeFloor() {
        Canvas[] suelos = new Canvas[] { new Canvas(new PointD(0, 85), new Size(170, 1)),
                new Canvas(new PointD(0, 214), new Size(170, 1)), new Canvas(new PointD(214, 300), new Size(128, 1)),
                new Canvas(new PointD(342, 214), new Size(127, 1)), new Canvas(new PointD(470, 257), new Size(127, 1)),
                new Canvas(new PointD(513, 86), new Size(127, 1)) };

        for (Canvas s : suelos) {
            s.enableCollider(true);
            s.setTag("suelo");
            lge.addGObject(s, 1);
        }
    }

    public void makePlatforms() {
        Platform[] platforms = new Platform[] { new Platform(200, 200, 'U', 100, 60),
                new Platform(400, 100, 'L', 100, 60) };
        for (Platform p : platforms) {
            lge.addGObject(p, 1);
        }
    }

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
        String resourceDir = args[0];
        Game game = new Game(resourceDir);
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
