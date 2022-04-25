package test.simple.demo05;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class MiHeroe extends Sprite {
    private LittleGameEngine lge;
    private int state = -1;
    private Point last;

    public MiHeroe() {
        super(new String[] { "heroe_idle_right", "heroe_idle_left", "heroe_run_right", "heroe_run_left" },
                new Point(550, 626), "Heroe");

        // acceso al motor de juegos
        lge = LittleGameEngine.getInstance();

        // sus atributos
        setOnEvents(LittleGameEngine.E_ON_UPDATE);
        setOnEvents(LittleGameEngine.E_ON_COLLISION);
        setShape("heroe_idle_left");
        useColliders(true);
        setBounds(new Rectangle(0, 0, 1920, 1056));
        last = getPosition();
    }

    @Override
    public void onUpdate(double dt) {
        // velocity = pixeles por segundo
        int velocity = 240;
        int pixels = (int) (velocity * dt);
        if (pixels < 1)
            pixels = 1;

        // la posiciona actual del heroe
        int x = getX();
        int y = getY();
        last = new Point(x, y);

        // cambiamos sus coordenadas, orientacion e imagen segun la tecla presionada
        if (lge.keyPressed(KeyEvent.VK_RIGHT)) {
            x = x + pixels;
            if (state != 2) {
                setShape("heroe_run_right");
                state = 2;
            }
        } else if (lge.keyPressed(KeyEvent.VK_LEFT)) {
            x = x - pixels;
            if (state != -2) {
                setShape("heroe_run_left");
                state = -2;
            }
        } else if (state == 2) {
            if (state != 1) {
                setShape("heroe_idle_right");
                state = 1;
            }
        } else if (state == -2) {
            if (state != -1) {
                setShape("heroe_idle_left");
                state = -1;
            }
        }

        if (lge.keyPressed(KeyEvent.VK_UP))
            y = y - pixels;
        else if (lge.keyPressed(KeyEvent.VK_DOWN))
            y = y + pixels;

        // siguiente imagen de la secuencia
        nextShape(dt, 0.050);

        // lo posicionamos
        setPosition(x, y);
    }

    @Override
    public void onCollision(double dt, GameObject[] gobjs) {
        lge.playSound("poing", false, 50);
        setPosition(last);
    }

}
