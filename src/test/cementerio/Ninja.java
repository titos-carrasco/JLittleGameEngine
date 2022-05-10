package test.cementerio;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Ninja extends Sprite {
    private LittleGameEngine lge;
    private Rectangle colisionador;
    private double vx = 2;
    private double vy = 0;
    private double g = 0.8;
    private double vsalto = 8;

    public Ninja(int x, int y) {
        super("ninja-idle-right", new Point(x, y));

        // acceso a LGE
        lge = LittleGameEngine.getInstance();

        // los eventos que recibiremos
        setOnEvents(LittleGameEngine.E_ON_POST_UPDATE);
        setOnEvents(LittleGameEngine.E_ON_COLLISION);
        enableCollider(true);

        // el colisionador
        colisionador = new Rectangle(new Point(20, 5), new Dimension(15, getHeight() - 5));
        setCollider(colisionador);
    }

    public boolean fixPosition(double dx, double dy) {
        GameObject[] gobjs = lge.collidesWithGObjects(this);
        for (GameObject gobj : gobjs) {
            String tag = gobj.getTag();
            if (tag.equals("suelo")) {
                setPosition(getX(), gobj.getY() - getHeight());
                return true;
            } else if (tag.equals("plataforma")) {
                Platform p = (Platform) gobj;
                if (p.getDir() == 'L')
                    setPosition(getX() - p.getSpeed(), p.getY() - getHeight());
                if (p.getDir() == 'R')
                    setPosition(getX() + p.getSpeed(), p.getY() - getHeight());
                else
                    setPosition(getX(), p.getY() - getHeight());
                return true;
            }
        }
        return false;
    }

    // despues de que todo fue actualizado
    @Override
    public void onPostUpdate(double dt) {
        // nuestra posicion actual
        Point position = getPosition();
        double x = position.x;
        double y = position.y;
        double x0 = x;
        double y0 = y;

        // primero el movimiento en X
        int move_x = 0;
        if (lge.keyPressed(KeyEvent.VK_LEFT)) {
            move_x = -1;
            setImage("ninja-run-left");
        } else if (lge.keyPressed(KeyEvent.VK_RIGHT)) {
            move_x = 1;
            setImage("ninja-run-right");
        } else {
            setImage("ninja-idle-right");
        }
        x = x + move_x * vx;

        // ahora el movimiento en Y
        y = y + vy;

        // nueva posicion
        setPosition((int) x, (int) y);
        nextImage(dt, 0.04);
        setCollider(colisionador);

        // la velocidad en Y es afectada por la gravedad
        vy = vy + g;

        // estamos en un suelo?
        boolean onfloor = fixPosition(x - x0, y - y0);

        // nos piden saltar
        if (onfloor && lge.keyPressed(KeyEvent.VK_SPACE))
            vy = -vsalto;

        if (onfloor && vy > 0)
            vy = 1;
    }

    // solo para detectar premios, energia, muerte, etc...
    @Override
    public void onCollision(double dt, GameObject[] gobjs) {
    }

}
