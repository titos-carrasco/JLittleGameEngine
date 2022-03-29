package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.UUID;

public class GameObject {
    protected Rectangle rect;
    protected Rectangle bounds = null;
    protected BufferedImage surface = null;
    protected int layer = -1;
    protected boolean use_colliders = false;
    protected String name;
    protected String tag = "";
    protected int on_events_enabled = 0x00;

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
        if (name == null)
            name = "__no_name__" + UUID.randomUUID().toString();
        rect = new Rectangle(x, y, width, height);
        this.name = name;
    }

    public Point GetPosition() {
        return rect.getLocation();
    }

    public Dimension GetSize() {
        return rect.getSize();
    }

    public String GetName() {
        return name;
    }

    public String GetTag() {
        return tag;
    }

    public void SetBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void SetPosition(Point position) {
        SetPosition(position.x, position.y);
    }

    public void SetPosition(int x, int y) {
        if (bounds == null)
            return;

        rect.x = x;
        rect.y = y;

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

    public void SetTag(String tag) {
        this.tag = tag;
    }

    public void UseColliders(boolean use_colliders) {
        this.use_colliders = use_colliders;
    }

    // manejo de eventos
    public void SetOnEvents(int on_events_enabled) {
        this.on_events_enabled = on_events_enabled;
    }

    public void OnDelete() {
    };

    public void OnStart() {
    };

    public void OnPreUpdate(double dt) {
    };

    public void OnUpdate(double dt) {
    };

    public void OnPostUpdate(double dt) {
    };

    public void OnCollision(double dt, ArrayList<GameObject> gobjs) {
    };

    public void OnPreRender(double dt) {
    };

    public void OnQuit() {
    };

}
