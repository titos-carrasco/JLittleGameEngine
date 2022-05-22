package test.bouncing;

import java.awt.Color;

import rcr.lge.Canvas;
import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.Position;
import rcr.lge.Size;

public class Ball extends Canvas {
    private LittleGameEngine lge;

    private double vx, vy;
    private double g, e;
    private GameObject ground;

    public Ball(double x, double y, double vx, double vy) {
        super(new Position(x, y), new Size(20, 20));

        // acceso al motor de juegos
        lge = LittleGameEngine.getInstance();

        this.vx = vx;
        this.vy = vy;
        g = 240;
        e = 0.4;
        enableCollider(true);
        setOnEvents(LittleGameEngine.E_ON_UPDATE);
        setOnEvents(LittleGameEngine.E_ON_POST_UPDATE);
        ground = lge.getGObject("ground");

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

        vy = vy + g * dt;
        setPosition(x, y);
    }

    @Override
    public void onPostUpdate(double dt) {
        if (collidesWith(ground)) {
            double x = getX();
            double y = ground.getY() - getHeight();
            setPosition(x, y);

            vy = -vy * e;
            if (Math.abs(vy) < 50) {
                vy = 0;
                vx = 0;
                g = 0;
            }
        }

    }

}
