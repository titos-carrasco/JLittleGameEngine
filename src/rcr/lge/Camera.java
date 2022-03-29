package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;

public class Camera extends GameObject {
    protected GameObject target;
    protected boolean target_center;

    protected Camera(Point position, Dimension size) {
        super(position, size, "__LGE_CAMERA__");
        target = null;
        target_center = true;
    }

    protected void FollowTarget() {
        // nadie a quien seguir
        if (target == null)
            return;

        // la posiciond del que seguimos
        Point position = target.GetPosition();
        int x = position.x;
        int y = position.y;

        // el centro de la camara en el centro del gobj
        if (target_center) {
            Dimension size = target.rect.getSize();
            x = x + size.width / 2;
            y = y + size.height / 2;
        }

        SetPosition(new Point(x - rect.width / 2, y - rect.height / 2));
    }

}
