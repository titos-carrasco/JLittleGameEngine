package test.birds;

import rcr.lge.Position;
import rcr.lge.Sprite;

public class Bird extends Sprite {

    public Bird(String inames, Position position) {
        super(inames, position);
    }

    @Override
    public void onUpdate(double dt) {
        nextImage(dt, 0.060);
    }
}
