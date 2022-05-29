package test.pong;

import java.awt.Color;

import rcr.lge.Canvas;
import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.PointD;
import rcr.lge.Size;

public class Ball extends Canvas {
    LittleGameEngine lge;
    private double initX;
    private double initY;
    private double speedX = -120;
    private double speedY = 120;

    public Ball(PointD position, Size size, String name) {
        super(position, size, name);
        lge = LittleGameEngine.getInstance();
        enableCollider(true);
        fill(Color.WHITE);

        initX = position.x;
        initY = position.y;
    }

    @Override
    public void onUpdate(double dt) {
        double dx = speedX * dt;
        double dy = speedY * dt;

        setPosition(getX() + dx, getY() + dy);
    }

    @Override
    public void onPostUpdate(double dt) {
        GameObject[] gobjs = lge.collidesWith(this);
        if (gobjs != null) {
            double x = getX();
            double y = getY();
            double dx = speedX * dt;
            double dy = speedY * dt;

            for (GameObject gobj : gobjs) {
                if (gobj.getTag().equals("wall-horizontal")) {
                    speedY = -speedY;
                    dy = -dy;
                }
                if (gobj.getTag().equals("paddle")) {
                    speedX = -speedX;
                    dx = -dx;
                }
                if (gobj.getTag().equals("wall-vertical")) {
                    x = initX;
                    y = initY;
                }
            }
            setPosition(x + dx, y + dy);
        }
    }
}
