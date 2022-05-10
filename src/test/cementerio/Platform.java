package test.cementerio;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Platform extends Sprite {
    private LittleGameEngine lge;
    char dir;
    private int pixels;
    private int distance;
    private int travel = 0;

    public Platform(int x, int y, char dir, int distance, int speed) {
        super("platform", new Point(x, y));

        // acceso a LGE
        lge = LittleGameEngine.getInstance();

        // los eventos que recibiremos
        setOnEvents(LittleGameEngine.E_ON_UPDATE);
        setCollider(new Rectangle(new Point(0, 0), new Dimension(getWidth(), 1)));
        enableCollider(true);
        setTag("plataforma");

        // mis atributos
        this.dir = dir;
        this.pixels = speed;
        this.distance = distance;
    }

    public char getDir() {
        return dir;
    }

    public int getSpeed() {
        return pixels;
    }

    @Override
    public void onUpdate(double dt) {
        Point position = getPosition();
        int x = position.x;
        int y = position.y;

        if (dir == 'R')
            x = x + pixels;
        else if (dir == 'L')
            x = x - pixels;
        else if (dir == 'D')
            y = y + pixels;
        else if (dir == 'U')
            y = y - pixels;

        setPosition(x, y);

        travel = travel + pixels;
        if (travel > distance) {
            travel = 0;
            if (dir == 'R')
                dir = 'L';
            else if (dir == 'L')
                dir = 'R';
            else if (dir == 'D')
                dir = 'U';
            else if (dir == 'U')
                dir = 'D';
        }
    }
}
