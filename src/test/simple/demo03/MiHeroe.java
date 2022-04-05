package test.simple.demo03;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class MiHeroe extends Sprite {
    private LittleGameEngine lge;

    public MiHeroe() {
        super(new String[] { "heroe_right", "heroe_left" }, new Point(550, 346), "Heroe");

        // acceso al motor de juegos
        lge = GetLGE();

        // sus atributos
        SetOnEvents(LittleGameEngine.E_ON_UPDATE);
        SetShape("heroe_right");
        SetBounds(new Rectangle(0, 0, 1920, 1056));
    }

    @Override
    public void OnUpdate(double dt) {
        // velocity = pixeles por segundo
        int velocity = 240;
        double pixels = velocity * dt;
        if (pixels < 1)
            pixels = 1;

        // la posiciona actual del heroe
        int x = GetX();
        int y = GetY();

        // cambiamos sus coordenadas segun la tecla presionada
        if (lge.KeyPressed(KeyEvent.VK_RIGHT)) {
            x = (int) (x + pixels);
            SetShape("heroe_right");
        } else if (lge.KeyPressed(KeyEvent.VK_LEFT)) {
            x = (int) (x - pixels);
            SetShape("heroe_left");
        }

        if (lge.KeyPressed(KeyEvent.VK_UP))
            y = (int) (y + pixels);
        else if (lge.KeyPressed(KeyEvent.VK_DOWN))
            y = (int) (y - pixels);

        // lo posicionamos
        SetPosition(x, y);
    }
}
