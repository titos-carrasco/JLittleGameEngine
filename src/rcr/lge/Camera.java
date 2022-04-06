package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;

public class Camera extends GameObject {
    GameObject target;
    boolean targetInCenter;

    Camera(Point position, Dimension size) {
        super(position, size, "__LGE_CAMERA__");
        target = null;
        targetInCenter = true;
    }

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
