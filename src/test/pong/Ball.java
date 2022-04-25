package test.pong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import rcr.lge.Canvas;
import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;

public class Ball extends Canvas {
    private int initX;
    private int initY;
    private double speedX = 180;
    private double speedY = 180;

    public Ball(Point position, Dimension size, String name) {
        super(position, size, name);
        setOnEvents(LittleGameEngine.E_ON_UPDATE);
        setOnEvents(LittleGameEngine.E_ON_COLLISION);
        enableCollider(true);
        fill(Color.WHITE);

        initX = position.x;
        initY = position.y;
    }

    @Override
    public void onUpdate(double dt) {
        double dx = speedX * dt;
        double dy = speedY * dt;

        setPosition((int) (getX() + dx), (int) (getY() + dy));
    }

    @Override
    public void onCollision(double dt, GameObject[] gobjs) {
        int x = getX();
        int y = getY();
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
        setPosition((int) (x + dx), (int) (y + dy));
    }
}
