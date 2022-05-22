package test.birds;

import rcr.lge.LittleGameEngine;
import rcr.lge.Position;
import rcr.lge.Sprite;

public class Bird extends Sprite {

    public Bird(String inames, Position position) {
        super(inames, position);
        setOnEvents(LittleGameEngine.E_ON_UPDATE);
    }

    @Override
    public void onUpdate(double dt) {
        nextImage(dt, 0.060);
    }
}
