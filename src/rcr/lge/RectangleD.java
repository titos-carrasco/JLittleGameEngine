package rcr.lge;

/**
 * Clase para representar un rectangulo
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 *
 */
public class RectangleD {
    public double x, y;
    public int width, height;

    /**
     * Crea un rectangulo en origen y dimensiones especificadas
     *
     * @param origin coordenadas (x, y) del origen del rectangulo
     * @param size   dimension (width, height) del rectangulo
     */
    public RectangleD(PointD origin, Size size) {
        x = origin.x;
        y = origin.y;
        width = size.width;
        height = size.height;
    }

    /**
     * Crea un rectangulo en origen y dimensiones especificadas
     *
     * @param x      coordenada x de su origen
     * @param y      coordenada y de su origen
     * @param width  su ancho
     * @param height su alto
     */
    public RectangleD(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Crea un rectangulo a partir de otro
     *
     * @param rect el rectangulo a tomar como base
     */
    public RectangleD(RectangleD rect) {
        x = rect.x;
        y = rect.y;
        width = rect.width;
        height = rect.height;
    }

    /**
     * Retorna una copia de este rectangulo
     *
     * @return la copia
     */
    public RectangleD copy() {
        return new RectangleD(x, y, width, height);
    }

    /**
     * Retorna las coordenadas del origen de este rectangulo
     *
     * @return las coordenadas de su origen
     */
    public PointD getOrigin() {
        return new PointD(x, y);
    }

    /**
     * Retorna la dimension de este rectangulo
     *
     * @return la dimensin (width, height) de este rectangulo
     */
    public Size getSize() {
        return new Size(width, height);
    }

    /**
     * Establece el origen de este rectangulo
     *
     * @param x la corodenada x de su origen
     * @param y la coordenada y de su origen
     */
    public void setOrigin(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Establece las dimensiones de este rectangulo
     *
     * @param width  el ancho
     * @param height el alto
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Determina si el rectangulo intersecta a otro
     *
     * @param rect el rectangulo sobre el cual determinar la interseccion
     * @return Verdadero si intersectan
     */
    public boolean intersects(RectangleD rect) {
        double sx1 = x;
        double sx2 = x + width - 1;
        double sy1 = y;
        double sy2 = y + height - 1;

        double rx1 = rect.x;
        double rx2 = rect.x + rect.width - 1;
        double ry1 = rect.y;
        double ry2 = rect.y + rect.height - 1;

        return rx1 <= sx2 && sx1 <= rx2 && ry1 <= sy2 && sy1 <= ry2;
    }

    /**
     * Determina si la posicion dada se encuentran dentro de este rectangulo
     *
     * @param p el punto a verificar
     * @return verdadero si el punto esta dentro del rectangulo
     */
    public boolean contains(PointD p) {
        return contains(p.x, p.y);
    }

    /**
     * Determina si las coordenadas dadas se encuentran dentro de este rectangulo
     *
     * @param x la coordenada x
     * @param y la coordenada y
     * @return verdadero si (x,y) se encuentran en el rectangulo
     */
    public boolean contains(double x, double y) {
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
    }
}
