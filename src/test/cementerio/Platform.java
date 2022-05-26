package test.cementerio;

import rcr.lge.Position;
import rcr.lge.Rectangle;
import rcr.lge.Size;
import rcr.lge.Sprite;

public class Platform extends Sprite {
    // private LittleGameEngine lge;
    char dir;
    private double pixels;
    private double distance;
    private double travel = 0;

    public Platform(double x, double y, char dir, double distance, double speed) {
        super("platform", new Position(x, y));

        // acceso a LGE
        // lge = LittleGameEngine.getInstance();

        // los eventos que recibiremos
        setCollider(new Rectangle(new Position(0, 0), new Size(getWidth(), 1)));
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

    public double getSpeed() {
        return pixels;
    }

    @Override
    public void onUpdate(double dt) {
        Position position = getPosition();
        double x = position.x;
        double y = position.y;

        double d = pixels * dt;
        if (dir == 'R')
            x = x + d;
        else if (dir == 'L')
            x = x - d;
        else if (dir == 'D')
            y = y + d;
        else if (dir == 'U')
            y = y - d;

        setPosition(x, y);

        travel = travel + d;
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
