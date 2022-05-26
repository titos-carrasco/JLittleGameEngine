package rcr.lge;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * GameObject para trazar formas en Little Game Engine
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 *
 */
public class Canvas extends GameObject {

    /**
     * Crea un canvas, para dibujar, en la posicion y dimensiones dadas
     *
     * @param origin posicion (x, y) del canvas
     * @param size   dimension (width, height) del canvas
     */
    public Canvas(Position origin, Size size) {
        this(origin, size, null);
    }

    /**
     * Crea un canvas, para dibujar, en la posicion y dimensiones dadas
     *
     * @param origin posicion (x, y) del canvas
     * @param size   dimension (width, height) del canvas
     * @param name   nombre para esta GameObject
     */
    public Canvas(Position origin, Size size, String name) {
        super(origin, size, name);
        surface = LittleGameEngine.getInstance().createTranslucentImage(size.width, size.height);
    }

    /**
     * Colorea el canvas con el color especificado
     *
     * @param color el color de relleno
     */
    public void fill(Color color) {
        Graphics2D g2d = surface.createGraphics();
        g2d.setBackground(color);
        g2d.clearRect(0, 0, rect.width, rect.height);
        g2d.dispose();
    }

    /**
     *
     * Traza un texto en este canvas en la posicion, tipo de letra y color
     * especificados
     *
     * @param text     el texto a trazar
     * @param position coordenada (x, y) en donde se trazara el texto dentro del
     *                 canvas (linea base del texto)
     * @param fname    nombre del font (cargado con LoadFont) a utilizar para trazar
     *                 el texto
     * @param color    color a utilizar (r,g,b) para trazar el texto
     */
    public void drawText(String text, Position position, String fname, Color color) {
        double x = position.x;
        double y = position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font f = LittleGameEngine.getInstance().getFont(fname);

        g2d.setColor(color);
        g2d.setFont(f);
        g2d.drawString(text, (int) x, (int) y);
        g2d.dispose();
    }

    /**
     * Traza un punto en este canvas en la posicion y color especificados
     *
     * @param position coordenada (x, y) en donde se trazara el punto dentro del
     *                 canvas
     * @param color    color a utilizar (r,g,b) para trazar el punto
     */
    public void drawPoint(Position position, Color color) {
        double x = position.x;
        double y = position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setColor(color);
        g2d.drawLine((int) x, (int) y, (int) x, (int) y);
        g2d.dispose();
    }

    /**
     * Traza un circulo en este canvas en la posicion, de radio y color especificado
     *
     * @param position  coordenada (x, y) en donde se trazara el circulo dentro del
     *                  canvas
     * @param radius    radio del circulo a trazar
     * @param color     color a utilizar (r,g,b) para trazar el circulo
     * @param thickness si es verdadero se mostrara el borde del circulo
     */
    public void drawCircle(Position position, double radius, Color color, boolean thickness) {
        double x = position.x;
        double y = position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setColor(color);
        if (thickness)
            g2d.drawOval((int) x, (int) y, (int) radius, (int) radius);
        else
            g2d.fillOval((int) x, (int) y, (int) radius, (int) radius);
        g2d.dispose();
    }

    /**
     * Traza un rectangulo en este canvas en la posicion, dimensiones y color
     * especificado
     *
     * @param position  coordenada (x, y) en donde se trazara el circulo dentro del
     *                  canvas
     * @param size      dimension (width, height) del rectangulo
     * @param color     color a utilizar (r,g,b) para trazar el rectangulo
     * @param thickness si es True se mostrara el borde del rectangulo
     */
    public void drawRectangle(Position position, Size size, Color color, boolean thickness) {
        double x = position.x;
        double y = position.y;

        Graphics2D g2d = surface.createGraphics();
        g2d.setColor(color);
        if (thickness)
            g2d.drawRect((int) x, (int) y, (int) (size.width - 1.0), (int) (size.height - 1.0));
        else
            g2d.fillRect((int) x, (int) y, size.width, size.height);
        g2d.dispose();
    }

    /**
     * Traza una superficie en este canvas en la posicion dada
     *
     * @param position coordenada (x, y) en donde se trazara la superfice dentro del
     *                 canvas
     * @param surface  superficie (imagen) a trazar
     */
    public void drawSurface(Position position, BufferedImage surface) {
        double x = position.x;
        double y = position.y;

        Graphics2D g2d = this.surface.createGraphics();
        g2d.drawImage(surface, (int) x, (int) y, null);
        g2d.dispose();
    }

}
