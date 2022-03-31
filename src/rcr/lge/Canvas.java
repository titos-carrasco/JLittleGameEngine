package rcr.lge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Canvas extends GameObject {
    private LittleGameEngine lge;

    public Canvas(Point origin, Dimension size) {
        this(origin, size, null);
    }

    public Canvas(Point origin, Dimension size, String name) {
        super(origin, size, name);
        lge = LittleGameEngine.GetLGE();
        surface = lge.CreateTranslucentImage(size.width, size.height);
    }

    public void Fill(Color color) {
        Graphics2D g2d = surface.createGraphics();
        g2d.setBackground(color);
        g2d.clearRect(0, 0, rect.width, rect.height);
    }

    public void DrawText(String text, Point position, String fname, Color color) {
        int x = position.x;
        int y = rect.height - position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font f = LittleGameEngine.GetLGE().GetFont(fname);

        g2d.setColor(color);
        g2d.setFont(f);
        g2d.drawString(text, x, y);
        g2d.dispose();
    }

    public void DrawPoint(Point position, Color color) {
        int x = position.x;
        int y = rect.height - position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setColor(color);
        g2d.drawLine(x, y, x, y);
        g2d.dispose();
    }

    public void DrawCircle(Point position, int radius, Color color, boolean thickness) {
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

    public void DrawRectangle(Point position, Dimension size, Color color, boolean thickness) {
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

    public void DrawSurface(Point position, BufferedImage surface) {
        int x = position.x;
        int y = rect.height - surface.getHeight() - position.y;

        Graphics2D g2d = this.surface.createGraphics();
        g2d.drawImage(surface, x, y, null);
        g2d.dispose();
    }

}
