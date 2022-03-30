package test.bouncing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

import rcr.lge.Canvas;
import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;

public class Ball extends Canvas {

    private double vx, vy;
    private double g, e;

    public Ball() {
        super(new Point((int) (50 + Math.random() * 700), (int) (200 + Math.random() * 350)), new Dimension(20, 20));
        SetOnEvents(LittleGameEngine.E_ON_UPDATE | LittleGameEngine.E_ON_COLLISION);
        UseColliders(true);
        vx = -10 + Math.random() * 20;
        vy = 0;
        g = 240;
        e = 0.5;

        Color fill_color = new Color(0, 255, 0, 64);
        Fill(fill_color);
    }

    @Override
    public void OnUpdate(double dt) {
        int x = (int) (GetX() + vx * dt);
        int y = (int) (GetY() + vy * dt);
        vy = vy - g * dt;

        SetPosition(x, y);
    }

    @Override
    public void OnCollision(double dt, ArrayList<GameObject> gobjs) {
        for (GameObject gobj : gobjs) {
            if (gobj.GetTag().equals("ground")) {
                int x = GetX();
                int y = gobj.GetY() + gobj.GetHeight();
                SetPosition(x, y);

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
