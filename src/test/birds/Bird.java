package test.birds;

import rcr.lge.PointD;
import rcr.lge.Sprite;

public class Bird extends Sprite {

    public Bird(String inames, PointD position) {
        super(inames, position);
    }

    @Override
    public void onUpdate(double dt) {
        nextImage(dt, 0.060);
    }
}
