package test.simple.demo02;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class MoveCamera implements IEvents {
    private LittleGameEngine lge;

    public MoveCamera() {
        // creamos el juego
        Dimension winSize = new Dimension(640, 480);

        lge = new LittleGameEngine(winSize, "Move Camera", new Color(0xFFFF00));
        lge.setOnMainUpdate(this);

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "../../resources");

        lge.loadImage("fondo", resourceDir + "/images/Backgrounds/FreeTileset/Fondo.png", false, false);
        lge.loadImage("heroe", resourceDir + "/images/Swordsman/Idle/Idle_000.png", 0.16, false, false);
        lge.loadImage("mute", resourceDir + "/images/icons/sound-*.png", false, false);
        lge.loadTTFFont("monospace.plain.16", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);
        lge.loadSound("fondo", resourceDir + "/sounds/happy-and-sad.wav");

        // activamos la musica de fondo
        lge.playSound("fondo", true, 50);

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new Point(0, 0), "fondo");
        lge.addGObject(fondo, 0);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 460), new Dimension(640, 20), "infobar");
        lge.addGObjectGUI(infobar);

        // agregamos el icono del sonido
        Sprite mute = new Sprite("mute", new Point(8, 463), "mute");
        mute.setShape("mute", 1);
        lge.addGObjectGUI(mute);

        // agregamos al heroe
        Sprite heroe = new Sprite("heroe", new Point(550, 346), "Heroe");
        lge.addGObject(heroe, 1);

        // # configuramos la camara
        lge.setCameraBounds(new Rectangle(0, 0, 1920, 1056));

        // posicionamos la camara
        Point heroePosition = heroe.getPosition();
        Dimension heroeSize = heroe.getSize();
        Dimension cameraSize = lge.getCameraSize();
        int x = heroePosition.x + heroeSize.width / 2 - cameraSize.width / 2;
        int y = heroePosition.y + heroeSize.height / 2 - cameraSize.height / 2;
        lge.setCameraPosition(new Point(x, y));
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
        infobar.drawText(info, new Point(50, 5), "monospace.plain.16", Color.BLACK);

        // mute on/off
        mousePosition = lge.getMouseClicked(0);
        if (mousePosition != null) {
            Sprite mute = (Sprite) lge.getGObject("mute");
            Rectangle r = mute.getRectangle();
            if (r.contains(mousePosition)) {
                int idx = mute.getCurrentIdx();
                if (idx == 1)
                    lge.setSoundVolume("fondo", 0);
                else
                    lge.setSoundVolume("fondo", 50);
                mute.nextShape();
            }
        }

        // velocity = pixeles por segundo
        int velocity = 240;
        double pixels = velocity * dt;
        if (pixels < 1)
            pixels = 1;

        // la posiciona actual de la camara
        Point cameraPosition = lge.getCameraPosition();

        // cambiamos sus coordenadas segun la tecla presionada
        if (lge.keyPressed(KeyEvent.VK_RIGHT))
            cameraPosition.x = (int) (cameraPosition.x + pixels);
        else if (lge.keyPressed(KeyEvent.VK_LEFT))
            cameraPosition.x = (int) (cameraPosition.x - pixels);

        if (lge.keyPressed(KeyEvent.VK_UP))
            cameraPosition.y = (int) (cameraPosition.y + pixels);
        else if (lge.keyPressed(KeyEvent.VK_DOWN))
            cameraPosition.y = (int) (cameraPosition.y - pixels);

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
