package test.cementerio;

import java.awt.event.KeyEvent;

import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.Position;
import rcr.lge.Rectangle;
import rcr.lge.Size;
import rcr.lge.Sprite;

public class Ninja extends Sprite {
    private LittleGameEngine lge;
    private Rectangle colisionador;
    private double vx = 120;
    private double vy = 0;
    private double vym = 500;
    private double g = 480;
    private double vsalto = 140;

    public Ninja(double x, double y) {
        super("ninja-idle-right", new Position(x, y));

        // acceso a LGE
        lge = LittleGameEngine.getInstance();

        // los eventos que recibiremos
        enableCollider(true);

        // el colisionador
        colisionador = new Rectangle(new Position(20, 5), new Size(15, getHeight() - 5));
        setCollider(colisionador);
    }

    public boolean fixPosition(double dx, double dy, double dt) {
        GameObject[] gobjs = lge.collidesWithGObjects(this);
        for (GameObject gobj : gobjs) {
            String tag = gobj.getTag();
            if (tag.equals("suelo")) {
                setPosition(getX(), gobj.getY() - getHeight());
                return true;
            } else if (tag.equals("plataforma")) {
                Platform p = (Platform) gobj;
                if (p.getDir() == 'L')
                    setPosition(getX() - p.getSpeed() * dt, p.getY() - getHeight() + 1);
                else if (p.getDir() == 'R')
                    setPosition(getX() + p.getSpeed() * dt, p.getY() - getHeight() + 1);
                else if (p.getDir() == 'U')
                    setPosition(getX(), getY() - p.getSpeed() * dt);
                else if (p.getDir() == 'D')
                    setPosition(getX(), getY() + p.getSpeed() * dt);
                return true;
            }
        }
        return false;
    }

    // despues de que todo fue actualizado
    @Override
    public void onPostUpdate(double dt) {
        // nuestra posicion actual
        Position position = getPosition();
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
        x = x + move_x * vx * dt;

        // siguiente imagen y su colisionador
        nextImage(dt, 0.04);
        setCollider(colisionador);

        // ahora el movimiento en Y
        y = y + vy * dt;
        vy = vy + g * dt;

        // nueva posicion
        setPosition(x, y);

        // estamos en un suelo?
        boolean onfloor = fixPosition(x - x0, y - y0, dt);

        // nos piden saltar
        if (onfloor) {
            if (lge.keyPressed(KeyEvent.VK_SPACE))
                vy = -vsalto;
            else
                vy = 0;
        }

        // limitamos a velocidad en Y
        if (vy > vym)
            vy = vym;
    }
}
