package test.simple.demo01;

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

public class TheWorld {
    private LittleGameEngine lge;

    public TheWorld() {
        // creamos el juego
        Size winSize = new Size(800, 440);

        lge = new LittleGameEngine(winSize, "The World", new Color(0xFFFFFF));
        lge.onMainUpdate = (dt) -> {
            onMainUpdate(dt);
        };

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "../../resources");

        lge.loadImage("fondo", resourceDir + "/images/Backgrounds/FreeTileset/Fondo.png", winSize, false, false);
        lge.loadImage("heroe", resourceDir + "/images/Swordsman/Idle/Idle_0*.png", 0.08, false, false);
        lge.loadImage("mute", resourceDir + "/images/icons/sound-*.png", false, false);
        lge.loadTTFont("banner", resourceDir + "/fonts/backlash.ttf", Font.PLAIN, 40);
        lge.loadTTFont("monospace", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);
        // lge.loadSound("fondo", resourceDir + "/sounds/happy-and-sad.wav");

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new PointD(0, 0));
        lge.addGObject(fondo, 0);

        // activamos la musica de fondo
        // lge.playSound("fondo", true, 50);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new PointD(0, 0), new Size(800, 20), "infobar");
        lge.addGObjectGUI(infobar);

        // agregamos el icono del sonido
        Sprite mute = new Sprite("mute", new PointD(8, 3), "mute");
        mute.setImage("mute", 1);
        lge.addGObjectGUI(mute);

        // agregamos al heroe
        Sprite heroe = new Sprite("heroe", new PointD(226, 254), "Heroe");
        lge.addGObject(heroe, 1);

        // agregamos un texto con transparencia
        Canvas canvas = new Canvas(new PointD(200, 110), new Size(400, 200));
        canvas.drawText("Little Game Engine", new PointD(30, 90), "banner", new Color(20, 20, 20));
        lge.addGObjectGUI(canvas);
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
        infobar.drawText(info, new PointD(50, 0), "monospace", Color.BLACK);

        // sonido on/off
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

        // animamos al heroe
        Sprite heroe = (Sprite) lge.getGObject("Heroe");
        heroe.nextImage(dt, 0.060);
    }

    // main loop
    public void Run(int fps) {
        lge.run(fps);
    }

    // show time
    public static void main(String[] args) {
        TheWorld game = new TheWorld();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }
}
