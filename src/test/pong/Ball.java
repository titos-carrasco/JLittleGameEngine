package test.pong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

import rcr.lge.Canvas;
import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;

public class Ball extends Canvas {
    private int init_x;
    private int init_y;
    private double speed_x = 180;
    private double speed_y = -180;

    public Ball(Point position, Dimension size, String name) {
        super(position, size, name);
        SetOnEvents(LittleGameEngine.E_ON_UPDATE | LittleGameEngine.E_ON_COLLISION);
        UseColliders(true);
        Fill(Color.WHITE);

        init_x = position.x;
        init_y = position.y;
    }

    @Override
    public void OnUpdate(double dt) {
        double dx = speed_x * dt;
        double dy = speed_y * dt;

        SetPosition((int) (GetX() + dx), (int) (GetY() + dy));
    }

    @Override
    public void OnCollision(double dt, ArrayList<GameObject> gobjs) {
        int x = GetX();
        int y = GetY();
        double dx = speed_x * dt;
        double dy = speed_y * dt;

        for (GameObject gobj : gobjs) {
            if (gobj.GetTag().equals("wall-horizontal")) {
                speed_y = -speed_y;
                dy = -dy;
            }
            if (gobj.GetTag().equals("paddle")) {
                speed_x = -speed_x;
                dx = -dx;
            }
            if (gobj.GetTag().equals("wall-vertical")) {
                x = init_x;
                y = init_y;
            }
        }
        SetPosition((int) (x + dx), (int) (y + dy));
    }
}
