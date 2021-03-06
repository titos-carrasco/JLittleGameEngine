package test.simple.demo05;

import java.awt.event.KeyEvent;

import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.PointD;
import rcr.lge.RectangleD;
import rcr.lge.Sprite;

public class MiHeroe extends Sprite {
    private LittleGameEngine lge;
    GameObject ninja;
    private int state = -1;
    private PointD last;

    public MiHeroe() {
        super("heroe_idle_right", new PointD(550, 626), "Heroe");

        // acceso al motor de juegos
        lge = LittleGameEngine.getInstance();
        ninja = lge.getGObject("ninja");

        // sus atributos
        enableCollider(true);
        setBounds(new RectangleD(0, 0, 1920, 1056));
        last = getPosition();
    }

    @Override
    public void onUpdate(double dt) {
        // velocity = pixeles por segundo
        double velocity = 240;
        double pixels = velocity * dt;

        // la posiciona actual del heroe
        double x = getX();
        double y = getY();
        last = new PointD(x, y);

        // cambiamos sus coordenadas, orientacion e imagen segun la tecla presionada
        if (lge.keyPressed(KeyEvent.VK_RIGHT)) {
            x = x + pixels;
            if (state != 2) {
                setImage("heroe_run_right");
                state = 2;
            }
        } else if (lge.keyPressed(KeyEvent.VK_LEFT)) {
            x = x - pixels;
            if (state != -2) {
                setImage("heroe_run_left");
                state = -2;
            }
        } else if (state == 2) {
            if (state != 1) {
                setImage("heroe_idle_right");
                state = 1;
            }
        } else if (state == -2) {
            if (state != -1) {
                setImage("heroe_idle_left");
                state = -1;
            }
        }

        if (lge.keyPressed(KeyEvent.VK_UP))
            y = y - pixels;
        else if (lge.keyPressed(KeyEvent.VK_DOWN))
            y = y + pixels;

        // siguiente imagen de la secuencia
        nextImage(dt, 0.050);

        // lo posicionamos
        setPosition(x, y);
    }

    @Override
    public void onPostUpdate(double dt) {
        if (collidesWith(ninja)) {
            lge.soundManager.playSound("poing", false);
            setPosition(last);
        }
    }

}
