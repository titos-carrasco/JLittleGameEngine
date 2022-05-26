package test.simple.demo03;

import java.awt.event.KeyEvent;

import rcr.lge.LittleGameEngine;
import rcr.lge.Position;
import rcr.lge.Rectangle;
import rcr.lge.Sprite;

public class MiHeroe extends Sprite {
    private LittleGameEngine lge;

    public MiHeroe() {
        super("heroe_right", new Position(550, 626), "Heroe");

        // acceso al motor de juegos
        lge = LittleGameEngine.getInstance();

        // sus atributos
        setBounds(new Rectangle(0, 0, 1920, 1056));
    }

    @Override
    public void onUpdate(double dt) {
        // velocity = pixeles por segundo
        double velocity = 240;
        double pixels = velocity * dt;

        // la posiciona actual del heroe
        double x = getX();
        double y = getY();

        // cambiamos sus coordenadas segun la tecla presionada
        if (lge.keyPressed(KeyEvent.VK_RIGHT)) {
            x = x + pixels;
            setImage("heroe_right");
        } else if (lge.keyPressed(KeyEvent.VK_LEFT)) {
            x = x - pixels;
            setImage("heroe_left");
        }

        if (lge.keyPressed(KeyEvent.VK_UP))
            y = y - pixels;
        else if (lge.keyPressed(KeyEvent.VK_DOWN))
            y = y + pixels;

        // lo posicionamos
        setPosition(x, y);
    }
}
