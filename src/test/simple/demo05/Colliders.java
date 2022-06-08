package test.simple.demo05;

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

public class Colliders {
    private LittleGameEngine lge;

    public Colliders() {
        // creamos el juego
        Size winSize = new Size(640, 480);

        lge = new LittleGameEngine(winSize, "Colliders", new Color(0xFFFFFF));
        lge.showColliders(new Color(0xFF0000));
        lge.onMainUpdate = (dt) -> {
            onMainUpdate(dt);
        };

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "../../resources");

        lge.loadImage("fondo", resourceDir + "/images/Backgrounds/FreeTileset/Fondo.png", false, false);
        lge.loadImage("heroe_idle_right", resourceDir + "/images/Swordsman/Idle/Idle_0*.png", 0.16, false, false);
        lge.loadImage("heroe_idle_left", resourceDir + "/images/Swordsman/Idle/Idle_0*.png", 0.16, true, false);
        lge.loadImage("heroe_run_right", resourceDir + "/images/Swordsman/Run/Run_0*.png", 0.16, false, false);
        lge.loadImage("heroe_run_left", resourceDir + "/images/Swordsman/Run/Run_0*.png", 0.16, true, false);
        lge.loadImage("ninja", resourceDir + "/images/Swordsman/Idle/Idle_000.png", 0.16, false, false);
        lge.loadImage("mute", resourceDir + "/images/icons/sound-*.png", false, false);
        lge.loadTTFont("monospace", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 15);
        // lge.loadSound("fondo", resourceDir + "/sounds/happy-and-sad.wav");
        // lge.loadSound("aves", resourceDir + "/sounds/bird-thrush-nightingale.wav");
        // lge.loadSound("poing", resourceDir + "/sounds/cartoon-poing.wav");

        // activamos la musica de fondo
        // lge.playSound("fondo", true, 100);

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new PointD(0, 0), "fondo");
        lge.addGObject(fondo, 0);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new PointD(0, 0), new Size(640, 20), "infobar");
        lge.addGObjectGUI(infobar);

        // agregamos el icono del sonido
        Sprite mute = new Sprite("mute", new PointD(8, 3), "mute");
        mute.setImage("mute", 1);
        lge.addGObjectGUI(mute);

        // agregamos un ninja
        Sprite ninja = new Sprite("ninja", new PointD(350, 720), "ninja");
        ninja.enableCollider(true);
        ninja.setCollider(new RectangleD[] { new RectangleD(36, 8, 36, 36), new RectangleD(28, 44, 44, 36) });
        lge.addGObject(ninja, 1);

        // agregamos al heroe
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

        // mute on/mute off
        mousePosition = lge.getMouseClicked(0);
        if (mousePosition != null) {
            Sprite mute = (Sprite) lge.getGObject("mute");
            RectangleD r = mute.getRectangle();
            if (r.contains(mousePosition.x, mousePosition.y)) {
                int idx = mute.getImagesIndex();
                if (idx == 1)
                    ; // lge.setSoundVolume("fondo", 0);
                else
                    ; // lge.setSoundVolume("fondo", 50);
                mute.nextImage();
            }
        }

        // de manera aleatorio activamos sonido de aves
        int n = (int) (Math.random() * 1000);
        if (n < 3)
            ; // lge.playSound("aves", false, 50);
    }

    // main loop
    public void Run(int fps) {
        lge.run(fps);
    }

    // show time
    public static void main(String[] args) {
        Colliders game = new Colliders();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
