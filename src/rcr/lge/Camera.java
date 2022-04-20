package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;

/**
 * La camara de Little Game Engine
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 *
 */
public class Camera extends GameObject {
    GameObject target;
    boolean targetInCenter;

    /**
     * Crea la camara en la posicion y dimensiones dadas. Esta clase es privada al
     * motor de juegos
     *
     * @param position coordenadas (x, y) de la posicion inicial de la camara
     * @param size     dimension (width, height) de la camara
     */
    Camera(Point position, Dimension size) {
        super(position, size, "__LGE_CAMERA__");
        target = null;
        targetInCenter = true;
    }

    /**
     * Mueve la camara segun se desplace su objetivo
     */
    void followTarget() {
        // nadie a quien seguir
        if (target == null)
            return;

        // la posicion del que seguimos
        int x = target.rect.x;
        int y = target.rect.y;

        // el centro de la camara en el centro del gobj
        if (targetInCenter) {
            x = x + target.rect.width / 2;
            y = y + target.rect.height / 2;
        }

        setPosition(x - rect.width / 2, y - rect.height / 2);
    }

}
