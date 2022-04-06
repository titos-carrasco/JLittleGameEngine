package test.bouncing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import rcr.lge.Canvas;
import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;

public class Ball extends Canvas {
    private LittleGameEngine lge;

    private double vx, vy;
    private double g, e;

    public Ball(int x, int y, double vx, double vy) {
        super(new Point(x, y), new Dimension(20, 20));

        // acceso al motor de juegos
        lge = LittleGameEngine.getInstance();

        this.vx = vx;
        this.vy = vy;
        g = 240;
        e = 0.8;
        useColliders(true);
        setOnEvents(LittleGameEngine.E_ON_UPDATE);
        setOnEvents(LittleGameEngine.E_ON_COLLISION);

        Color fillColor = new Color(0, 255, 0, 64);
        fill(fillColor);
    }

    @Override
    public void onUpdate(double dt) {
        double x = getX() + vx * dt;
        double y = getY() + vy * dt;

        if (x < 0) {
            lge.delGObject(this);
            return;
        }

        vy = vy - g * dt;
        setPosition((int) x, (int) y);
    }

    @Override
    public void onCollision(double dt, GameObject[] gobjs) {
        for (GameObject gobj : gobjs) {
            if (gobj.getTag().equals("ground")) {
                double x = getX();
                double y = gobj.getY() + gobj.getHeight();
                setPosition((int) x, (int) y);

                vy = -vy * e;
                if (Math.abs(vy) < 30) {
                    vy = 0;
                    vx = 0;
                    g = 0;
                }
                break;
            }
        }

    }

}
