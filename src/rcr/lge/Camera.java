package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;

public class Camera extends GameObject {
    GameObject target;
    boolean target_center;

    Camera(Point position, Dimension size) {
        super(position, size, "__LGE_CAMERA__");
        target = null;
        target_center = true;
    }

    void FollowTarget() {
        // nadie a quien seguir
        if (target == null)
            return;

        // la posicion del que seguimos
        int x = target.rect.x;
        int y = target.rect.y;

        // el centro de la camara en el centro del gobj
        if (target_center) {
            x = x + target.rect.width / 2;
            y = y + target.rect.height / 2;
        }

        SetPosition(x - rect.width / 2, y - rect.height / 2);
    }

}
