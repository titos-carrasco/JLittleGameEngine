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
        surface = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    }

    public void Fill(Color color) {
        Graphics2D g2d = surface.createGraphics();
        g2d.setBackground(color);
        g2d.clearRect(0, 0, rect.width, rect.height);
    }

    public void DrawText(String text, Point position, String fname, Color color) {
        int x = position.x;
        int y = position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font f = LittleGameEngine.GetLGE().GetFont(fname);

        g2d.setColor(color);
        g2d.setFont(f);
        g2d.drawString(text, x, rect.height - y);
        g2d.dispose();
    }

    public void DrawPoint(Point position, Color color) {
        Point p = new Point(position.x, 0);
        p.y = rect.height - position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setColor(color);
        g2d.drawLine(p.x, p.y, p.x, p.y);
        g2d.dispose();
    }

    public void DrawCircle(Point position, int radius, Color color, boolean thickness) {
        Point p = new Point(position.x, 0);
        p.y = rect.height - position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setColor(color);
        if (thickness)
            g2d.drawOval(p.x, p.y, radius, radius);
        else
            g2d.fillOval(p.x, p.y, radius, radius);
        g2d.dispose();
    }

    public void DrawRectangle(Point position, Dimension size, Color color, boolean thickness) {
        int y = rect.height - size.height - position.y;
        Graphics2D g2d = surface.createGraphics();
        g2d.setColor(color);
        if (thickness)
            g2d.drawRect(position.x, y, size.width - 1, size.height - 1);
        else
            g2d.fillRect(position.x, y, size.width, size.height);
        g2d.dispose();
    }

    public void DrawSurface(Point position, BufferedImage surface) {
        // x, y = position
        // w, h = self.GetSize()
        // self._surface.blit( surface, (x, h - surface.get_height() - y) )
    }

}
