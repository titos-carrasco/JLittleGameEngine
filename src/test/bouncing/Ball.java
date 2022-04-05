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
        lge = GetLGE();

        this.vx = vx;
        this.vy = vy;
        g = 240;
        e = 0.8;
        UseColliders(true);
        SetOnEvents(LittleGameEngine.E_ON_UPDATE);
        SetOnEvents(LittleGameEngine.E_ON_COLLISION);

        Color fill_color = new Color(0, 255, 0, 64);
        Fill(fill_color);
    }

    @Override
    public void OnUpdate(double dt) {
        double x = GetX() + vx * dt;
        double y = GetY() + vy * dt;

        if (x < 0) {
            lge.DelGObject(this);
            return;
        }

        vy = vy - g * dt;
        SetPosition((int) x, (int) y);
    }

    @Override
    public void OnCollision(double dt, GameObject[] gobjs) {
        for (GameObject gobj : gobjs) {
            if (gobj.GetTag().equals("ground")) {
                double x = GetX();
                double y = gobj.GetY() + gobj.GetHeight();
                SetPosition((int) x, (int) y);

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
