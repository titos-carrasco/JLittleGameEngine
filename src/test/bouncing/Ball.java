package test.bouncing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

import rcr.lge.Canvas;
import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;

public class Ball extends Canvas {

    Color fill_color;
    private double vx, vy;
    private double g, e;

    public Ball() {
        super(new Point((int) (50 + Math.random() * 700), (int) (200 + Math.random() * 350)), new Dimension(20, 20));
        SetOnEvents(LittleGameEngine.E_ON_UPDATE | LittleGameEngine.E_ON_COLLISION);
        UseColliders(true);
        fill_color = new Color(0, 255, 0, 64);
        vx = -10 + Math.random() * 20;
        vy = 0;
        g = 240;
        e = 0.5;
    }

    @Override
    public void OnUpdate(double dt) {
        Point position = GetPosition();

        position.x = (int) (position.x + vx * dt);
        position.y = (int) (position.y + vy * dt);
        vy = vy - g * dt;

        SetPosition(position);
        Fill(fill_color);
    }

    @Override
    public void OnCollision(double dt, ArrayList<GameObject> gobjs) {
        Point position = GetPosition();
        for (GameObject gobj : gobjs) {
            if (gobj.GetTag() == "ground") {
                Point p = gobj.GetPosition();
                Dimension d = gobj.GetSize();

                p.x = position.x;
                p.y = p.y + d.height;
                SetPosition(p);

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
