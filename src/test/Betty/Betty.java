package test.Betty;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Betty extends Sprite {
    private LittleGameEngine lge;

    private boolean alive;
    private Dimension winSize;
    private Point lastPoint;

    public Betty(String name, Dimension winSize) {
        super(new String[] { "betty_idle", "betty_down", "betty_up", "betty_left", "betty_right" }, new Point(0, 0),
                name);

        // acceso al motor de juegos
        lge = LittleGameEngine.getInstance();

        setOnEvents(LittleGameEngine.E_ON_UPDATE);
        setOnEvents(LittleGameEngine.E_ON_COLLISION);
        setShape("betty_idle");
        setTag("Betty");
        useColliders(true);
        alive = true;
        this.winSize = winSize;
    }

    public boolean IsAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
        setShape("betty_idle");
    }

    @Override
    public void onUpdate(double dt) {
        // solo si estoy viva
        if (!alive)
            return;

        // velocity = pixeles por segundo
        // double velocity = 120;
        // int pixels = (int)velocity*dt;
        int pixels = 2;

        // nuestra posicion actual y tamano
        int x = getX();
        int y = getY();
        lastPoint = new Point(x, y);

        // cambiamos sus coordenadas e imagen segun la tecla presionada
        int idx = getCurrentIdx();
        if (lge.keyPressed(KeyEvent.VK_RIGHT)) {
            setShape("betty_right", idx);
            x = x + pixels;
        } else if (lge.keyPressed(KeyEvent.VK_LEFT)) {
            setShape("betty_left", idx);
            x = x - pixels;
        } else if (lge.keyPressed(KeyEvent.VK_UP)) {
            setShape("betty_up", idx);
            y = y - pixels;
        } else if (lge.keyPressed(KeyEvent.VK_DOWN)) {
            setShape("betty_down", idx);
            y = y + pixels;
        } else {
            setShape("betty_idle", idx);
            if (x % 32 < 4)
                x = Math.round(x / 32) * 32;
            else if (x % 32 > 28)
                x = Math.round((x + 32) / 32) * 32;
            if (y % 32 < 4)
                y = Math.round(y / 32) * 32;
            else if (y % 32 > 28)
                y = Math.round((y + 32) / 32) * 32;
        }

        // tunel?
        if (x < -16)
            x = winSize.width - 16;
        else if (x > winSize.width - 16)
            x = -16;

        // siguiente imagen de la secuencia
        setPosition(x, y);
        nextShape(dt, 0.1);
    }

    @Override
    public void onCollision(double dt, GameObject[] gobjs) {
        if (!alive)
            return;

        for (GameObject gobj : gobjs)
            if (gobj.getTag().equals("zombie")) {
                alive = false;
                System.out.println("Un zombie me mato");
                return;
            }
        setPosition(lastPoint);
    }
}
