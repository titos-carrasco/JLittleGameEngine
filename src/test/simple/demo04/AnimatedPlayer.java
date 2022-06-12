package test.simple.demo04;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.LittleGameEngine;
import rcr.lge.PointD;
import rcr.lge.RectangleD;
import rcr.lge.Size;
import rcr.lge.Sprite;

public class AnimatedPlayer {
    private LittleGameEngine lge;

    public AnimatedPlayer(String resourceDir) {
        // creamos el juego
        Size winSize = new Size(640, 480);

        lge = new LittleGameEngine(winSize, "Animated Player", Color.WHITE);
        lge.onMainUpdate = (dt) -> {
            onMainUpdate(dt);
        };

        // cargamos los recursos que usaremos
        lge.imageManager.loadImages("fondo", resourceDir + "/images/Backgrounds/FreeTileset/Fondo.png", false, false);
        lge.imageManager.loadImages("heroe_idle_right", resourceDir + "/images/Swordsman/Idle/Idle_0*.png", 0.16, false,
                false);
        lge.imageManager.loadImages("heroe_idle_left", resourceDir + "/images/Swordsman/Idle/Idle_0*.png", 0.16, true,
                false);
        lge.imageManager.loadImages("heroe_run_right", resourceDir + "/images/Swordsman/Run/Run_0*.png", 0.16, false,
                false);
        lge.imageManager.loadImages("heroe_run_left", resourceDir + "/images/Swordsman/Run/Run_0*.png", 0.16, true,
                false);
        lge.fontManager.loadTTFont("monospace", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 15);
        lge.soundManager.loadSound("fondo", resourceDir + "/sounds/happy-and-sad.wav");

        // activamos la musica de fondo
        lge.soundManager.playSound("fondo", true);

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new PointD(0, 0), "fondo");
        lge.addGObject(fondo, 0);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new PointD(0, 0), new Size(640, 20), "infobar");
        lge.addGObjectGUI(infobar);

        MiHeroe heroe = new MiHeroe();
        lge.addGObject(heroe, 1);

        // # configuramos la camara
        lge.setCameraBounds(new RectangleD(0, 0, 1920, 1056));

        // establecemos que la camara siga al heroe
        lge.setCameraTarget(heroe, false);
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
        infobar.drawText(info, new PointD(30, 0), "monospace", Color.BLACK);
    }

    // main loop
    public void Run(int fps) {
        lge.run(fps);
    }

    // show time
    public static void main(String[] args) {
        String resourceDir = args[0];
        AnimatedPlayer game = new AnimatedPlayer(resourceDir);
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
