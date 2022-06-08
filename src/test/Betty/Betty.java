package test.Betty;

import java.awt.event.KeyEvent;

import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.PointD;
import rcr.lge.Size;
import rcr.lge.Sprite;

public class Betty extends Sprite {
    private LittleGameEngine lge;

    private boolean alive;
    private Size winSize;
    private PointD lastPoint;

    public Betty(String name, Size winSize) {
        super("betty_idle", new PointD(0, 0), name);

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
        lastPoint = new PointD(x, y);

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
            x = (int) (x / 4) * 4;
            y = (int) (y / 4) * 4;
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

        GameObject[] gobjs = lge.collidesWith(this);
        for (GameObject gobj : gobjs)
            if (gobj.getTag().equals("zombie")) {
                alive = false;
                System.out.println("Un zombie me mato");
                return;
            } else if (gobj.getTag().equals("muro")) {
                double x = getX();
                double y = getY();
                double xo = gobj.getX();
                double yo = gobj.getY();

                if (lastPoint.x < x)
                    x = xo - getWidth();
                else if (lastPoint.x > x)
                    x = xo + gobj.getWidth();

                if (lastPoint.y < y)
                    y = yo - getHeight();
                else if (lastPoint.y > y)
                    y = yo + gobj.getHeight();

                setPosition(x, y);
            }
    }
}
