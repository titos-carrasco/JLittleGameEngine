package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map.Entry;

public class Sprite extends GameObject {
    HashMap<String, BufferedImage[]> surfaces;
    String iname;
    int idx;
    double elapsed = 0;

    public Sprite(String iname, Point position) {
        this(new String[] { iname }, position, null);
    }

    public Sprite(String iname, Point position, String name) {
        this(new String[] { iname }, position, name);
    }

    public Sprite(String[] inames, Point position) {
        this(inames, position, null);
    }

    public Sprite(String[] inames, Point position, String name) {
        super(position, new Dimension(0, 0), name);
        surfaces = new HashMap<String, BufferedImage[]>();

        for (String iname : inames)
            surfaces.put(iname, LittleGameEngine.getInstance().getImages(iname));

        Entry<String, BufferedImage[]> elem = surfaces.entrySet().iterator().next();
        this.iname = elem.getKey();
        this.idx = 0;
        this.surface = elem.getValue()[0];
        this.rect.setSize(this.surface.getWidth(), this.surface.getHeight());
    }

    public String getCurrentIName() {
        return iname;
    }

    public int getCurrentIdx() {
        return idx;
    }

    public void nextShape() {
        nextShape(0, 0);
    }

    public void nextShape(double dt) {
        nextShape(dt, 0);
    }

    public void nextShape(double dt, double delay) {
        elapsed = elapsed + dt;
        if (elapsed < delay)
            return;

        elapsed = 0;
        idx = idx + 1;
        if (idx >= surfaces.get(iname).length)
            idx = 0;

        surface = surfaces.get(iname)[idx];
        this.rect.setSize(this.surface.getWidth(), this.surface.getHeight());
    }

    public void setShape(String iname) {
        setShape(iname, 0);
    }

    public void setShape(String iname, int idx) {
        this.iname = iname;
        if (idx >= surfaces.get(iname).length)
            idx = 0;
        this.idx = idx;
        surface = surfaces.get(iname)[idx];
        this.rect.setSize(this.surface.getWidth(), this.surface.getHeight());
    }
}
