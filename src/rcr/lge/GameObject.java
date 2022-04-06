package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.UUID;

public class GameObject {
    Rectangle rect;
    String name;
    BufferedImage surface = null;
    Rectangle bounds = null;
    String tag = "";
    boolean useColliders = false;
    int layer = -1;
    int onEventsEnabled = 0x00;

    public GameObject(Point origin, Dimension size) {
        this(origin.x, origin.y, size.width, size.height, null);
    }

    public GameObject(Point origin, Dimension size, String name) {
        this(origin.x, origin.y, size.width, size.height, name);
    }

    public GameObject(int x, int y, int width, int height) {
        this(x, y, width, height, null);
    }

    public GameObject(int x, int y, int width, int height, String name) {
        rect = new Rectangle(x, y, width, height);
        if (name == null)
            name = "__no_name__" + UUID.randomUUID().toString();
        this.name = name;
    }

    public Point getPosition() {
        return new Point(rect.getLocation());
    }

    public int getX() {
        return rect.x;
    }

    public int getY() {
        return rect.y;
    }

    public Dimension getSize() {
        return new Dimension(rect.getSize());
    }

    public int getWidth() {
        return rect.width;
    }

    public int getHeight() {
        return rect.height;
    }

    public Rectangle getRectangle() {
        return new Rectangle(rect);
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = new Rectangle(bounds);
    }

    public void setPosition(Point position) {
        setPosition(position.x, position.y);
    }

    public void setPosition(int x, int y) {
        rect.x = x;
        rect.y = y;

        if (bounds == null)
            return;

        if (rect.width <= bounds.width && rect.height <= bounds.height) {
            if (rect.x < bounds.x)
                rect.x = bounds.x;
            else if (rect.x + rect.width >= bounds.x + bounds.width)
                rect.x = bounds.x + bounds.width - rect.width;
            if (rect.y < bounds.y)
                rect.y = bounds.y;
            else if (rect.y + rect.height >= bounds.y + bounds.height)
                rect.y = bounds.y + bounds.height - rect.height;
        }
    }

    public void setTag(String tag) {
        this.tag = new String(tag);
    }

    public void useColliders(boolean useColliders) {
        this.useColliders = useColliders;
    }

    // manejo de eventos
    public void setOnEvents(int onEventsEnabled) {
        this.onEventsEnabled |= onEventsEnabled;
    }

    public void onDelete() {
    };

    public void onStart() {
    };

    public void onPreUpdate(double dt) {
    };

    public void onUpdate(double dt) {
    };

    public void onPostUpdate(double dt) {
    };

    public void onCollision(double dt, GameObject[] gobjs) {
    };

    public void onPreRender(double dt) {
    };

    public void onQuit() {
    };

}
