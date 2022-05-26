package test.Betty;

import java.awt.event.KeyEvent;

import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.Position;
import rcr.lge.Size;
import rcr.lge.Sprite;

public class Betty extends Sprite {
    private LittleGameEngine lge;

    private boolean alive;
    private Size winSize;
    private Position lastPoint;

    public Betty(String name, Size winSize) {
        super("betty_idle", new Position(0, 0), name);

        // acceso al motor de juegos
        lge = LittleGameEngine.getInstance();

        setTag("Betty");
        enableCollider(true);
        alive = true;
        this.winSize = winSize;
    }

    public boolean IsAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
        setImage("betty_idle");
    }

    @Override
    public void onUpdate(double dt) {
        // solo si estoy viva
        if (!alive)
            return;

        // velocity = pixeles por segundo
        // double velocity = 120;
        // double pixels = velocity*dt;
        double pixels = 2;

        // nuestra posicion actual y tamano
        double x = getX();
        double y = getY();
        lastPoint = new Position(x, y);

        // cambiamos sus coordenadas e imagen segun la tecla presionada
        int idx = getImagesIndex();
        if (lge.keyPressed(KeyEvent.VK_RIGHT)) {
            setImage("betty_right", idx);
            x = x + pixels;
        } else if (lge.keyPressed(KeyEvent.VK_LEFT)) {
            setImage("betty_left", idx);
            x = x - pixels;
        } else if (lge.keyPressed(KeyEvent.VK_UP)) {
            setImage("betty_up", idx);
            y = y - pixels;
        } else if (lge.keyPressed(KeyEvent.VK_DOWN)) {
            setImage("betty_down", idx);
            y = y + pixels;
        } else {
            setImage("betty_idle", idx);
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
        nextImage(dt, 0.1);
    }

    @Override
    public void onPostUpdate(double dt) {
        if (!alive)
            return;

        GameObject[] gobjs = lge.collidesWithGObjects(this);
        for (GameObject gobj : gobjs)
            if (gobj.getTag().equals("zombie")) {
                alive = false;
                System.out.println("Un zombie me mato");
                return;
            } else if (gobj.getTag().equals("muro")) {
                setPosition(lastPoint);
            }
    }
}
