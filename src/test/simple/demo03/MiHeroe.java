package test.simple.demo03;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class MiHeroe extends Sprite {
    private LittleGameEngine lge;

    public MiHeroe() {
        super(new String[] { "heroe_right", "heroe_left" }, new Point(550, 626), "Heroe");

        // acceso al motor de juegos
        lge = LittleGameEngine.getInstance();

        // sus atributos
        setOnEvents(LittleGameEngine.E_ON_UPDATE);
        setShape("heroe_right");
        setBounds(new Rectangle(0, 0, 1920, 1056));
    }

    @Override
    public void onUpdate(double dt) {
        // velocity = pixeles por segundo
        int velocity = 240;
        double pixels = velocity * dt;
        if (pixels < 1)
            pixels = 1;

        // la posiciona actual del heroe
        int x = getX();
        int y = getY();

        // cambiamos sus coordenadas segun la tecla presionada
        if (lge.keyPressed(KeyEvent.VK_RIGHT)) {
            x = (int) (x + pixels);
            setShape("heroe_right");
        } else if (lge.keyPressed(KeyEvent.VK_LEFT)) {
            x = (int) (x - pixels);
            setShape("heroe_left");
        }

        if (lge.keyPressed(KeyEvent.VK_UP))
            y = (int) (y - pixels);
        else if (lge.keyPressed(KeyEvent.VK_DOWN))
            y = (int) (y + pixels);

        // lo posicionamos
        setPosition(x, y);
    }
}
