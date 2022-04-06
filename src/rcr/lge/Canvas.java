package rcr.lge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Canvas extends GameObject {

    public Canvas(Point origin, Dimension size) {
        this(origin, size, null);
    }

    public Canvas(Point origin, Dimension size, String name) {
        super(origin, size, name);
        surface = LittleGameEngine.getInstance().createTranslucentImage(size.width, size.height);
    }

    public void fill(Color color) {
        Graphics2D g2d = surface.createGraphics();
        g2d.setBackground(color);
        g2d.clearRect(0, 0, rect.width, rect.height);
    }

    public void drawText(String text, Point position, String fname, Color color) {
        int x = position.x;
        int y = rect.height - position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font f = LittleGameEngine.getInstance().getFont(fname);

        g2d.setColor(color);
        g2d.setFont(f);
        g2d.drawString(text, x, y);
        g2d.dispose();
    }

    public void drawPoint(Point position, Color color) {
        int x = position.x;
        int y = rect.height - position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setColor(color);
        g2d.drawLine(x, y, x, y);
        g2d.dispose();
    }

    public void drawCircle(Point position, int radius, Color color, boolean thickness) {
        int x = position.x;
        int y = rect.height - position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setColor(color);
        if (thickness)
            g2d.drawOval(x, y, radius, radius);
        else
            g2d.fillOval(x, y, radius, radius);
        g2d.dispose();
    }

    public void drawRectangle(Point position, Dimension size, Color color, boolean thickness) {
        int x = position.x;
        int y = rect.height - size.height - position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setColor(color);
        if (thickness)
            g2d.drawRect(x, y, size.width - 1, size.height - 1);
        else
            g2d.fillRect(x, y, size.width, size.height);
        g2d.dispose();
    }

    public void drawSurface(Point position, BufferedImage surface) {
        int x = position.x;
        int y = rect.height - surface.getHeight() - position.y;

        Graphics2D g2d = this.surface.createGraphics();
        g2d.drawImage(surface, x, y, null);
        g2d.dispose();
    }

}
