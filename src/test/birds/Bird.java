package test.birds;

import java.awt.Point;

import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Bird extends Sprite {

    public Bird(String inames, Point position) {
        super(inames, position);
        SetOnEvents(LittleGameEngine.E_ON_UPDATE);
    }

    @Override
    public void OnUpdate(double dt) {
        NextShape(dt, 0.060);
    }
}
