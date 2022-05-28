package test.simple.demo02;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;
import rcr.lge.Position;
import rcr.lge.Rectangle;
import rcr.lge.Size;
import rcr.lge.Sprite;

public class MoveCamera implements IEvents {
    private LittleGameEngine lge;

    public MoveCamera() {
        // creamos el juego
        Size winSize = new Size(640, 480);

        lge = new LittleGameEngine(winSize, "Move Camera", new Color(0xFFFFFF));
        lge.setOnMainUpdate(this);

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "../../resources");

        lge.loadImage("fondo", resourceDir + "/images/Backgrounds/FreeTileset/Fondo.png", false, false);
        lge.loadImage("heroe", resourceDir + "/images/Swordsman/Idle/Idle_000.png", 0.16, false, false);
        lge.loadImage("mute", resourceDir + "/images/icons/sound-*.png", false, false);
        lge.loadTTFont("monospace.plain.16", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);
        lge.loadSound("fondo", resourceDir + "/sounds/happy-and-sad.wav");

        // activamos la musica de fondo
        lge.playSound("fondo", true, 50);

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new Position(0, 0), "fondo");
        lge.addGObject(fondo, 0);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Position(0, 0), new Size(640, 20), "infobar");
        lge.addGObjectGUI(infobar);

        // agregamos el icono del sonido
        Sprite mute = new Sprite("mute", new Position(8, 3), "mute");
        mute.setImage("mute", 1);
        lge.addGObjectGUI(mute);

        // agregamos al heroe
        Sprite heroe = new Sprite("heroe", new Position(550, 626), "Heroe");
        lge.addGObject(heroe, 1);

        // # configuramos la camara
        lge.setCameraBounds(new Rectangle(0, 0, 1920, 1056));

        // posicionamos la camara
        Position heroePosition = heroe.getPosition();
        Size heroeSize = heroe.getSize();
        Size cameraSize = lge.getCameraSize();
        double x = heroePosition.x + heroeSize.width / 2 - cameraSize.width / 2;
        double y = heroePosition.y + heroeSize.height / 2 - cameraSize.height / 2;
        lge.setCameraPosition(new Position(x, y));
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
        infobar.drawText(info, new Position(50, 0), "monospace.plain.16", Color.BLACK);

        // mute on/off
        mousePosition = lge.getMouseClicked(0);
        if (mousePosition != null) {
            Sprite mute = (Sprite) lge.getGObject("mute");
            Rectangle r = mute.getRectangle();
            if (r.contains(mousePosition)) {
                int idx = mute.getImagesIndex();
                if (idx == 1)
                    lge.setSoundVolume("fondo", 0);
                else
                    lge.setSoundVolume("fondo", 50);
                mute.nextImage();
            }
        }

        // velocity = pixeles por segundo
        double velocity = 240;
        double pixels = velocity * dt;

        // la posiciona actual de la camara
        Position cameraPosition = lge.getCameraPosition();

        // cambiamos sus coordenadas segun la tecla presionada
        if (lge.keyPressed(KeyEvent.VK_RIGHT))
            cameraPosition.x = cameraPosition.x + pixels;
        else if (lge.keyPressed(KeyEvent.VK_LEFT))
            cameraPosition.x = cameraPosition.x - pixels;

        if (lge.keyPressed(KeyEvent.VK_UP))
            cameraPosition.y = cameraPosition.y - pixels;
        else if (lge.keyPressed(KeyEvent.VK_DOWN))
            cameraPosition.y = cameraPosition.y + pixels;

        // posicionamos la camara
        lge.setCameraPosition(cameraPosition);

    }

    // main loop
    public void Run(int fps) {
        lge.run(fps);
    }

    // show time
    public static void main(String[] args) {
        MoveCamera game = new MoveCamera();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }
}
