package test.simple.demo03;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class MiHeroe extends Sprite {
    private LittleGameEngine lge;
    private int heading;

    public MiHeroe() {
        super(new String[] { "heroe_right", "heroe_left" }, new Point(550, 346), "Heroe");

        // acceso al motor de juegos
        lge = LittleGameEngine.GetLGE();

        // sus atributos
        SetOnEvents(LittleGameEngine.E_ON_UPDATE);
        SetShape("heroe_right", 0);
        heading = 1;
        SetBounds(new Rectangle(0, 0, 1920, 1056));
    }

    @Override
    public void OnUpdate(double dt) {
        // velocity = pixeles por segundo
        int velocity = 240;
        double pixels = velocity * dt;

        // la posiciona actual del heroe
        int x = GetX();
        int y = GetY();

        // cambiamos sus coordenadas segun la tecla presionada
        if (lge.KeyPressed(KeyEvent.VK_RIGHT)) {
            x = (int) (x + pixels);
            if (heading != 1) {
                SetShape("heroe_right", 0);
                heading = 1;
            }
        } else if (lge.KeyPressed(KeyEvent.VK_LEFT)) {
            x = (int) (x - pixels);
            if (heading != -1) {
                SetShape("heroe_left", 0);
                heading = -1;
            }
        }

        if (lge.KeyPressed(KeyEvent.VK_UP))
            y = (int) (y + pixels);
        else if (lge.KeyPressed(KeyEvent.VK_DOWN))
            y = (int) (y - pixels);

        // lo posicionamos
        SetPosition(x, y);
    }
}
