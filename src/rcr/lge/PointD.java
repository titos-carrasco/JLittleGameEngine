package rcr.lge;

/**
 * Una posicion (X, y) en un espacio de coordenadas 2D
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 *
 */

public class PointD {
    public double x, y;

    /**
     * Crea una instancia de una posicion
     *
     * @param x coordenada x del punto
     * @param y coordenada y del punto
     */
    public PointD(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
