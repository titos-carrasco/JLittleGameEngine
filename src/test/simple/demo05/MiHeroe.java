package test.simple.demo05;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class MiHeroe extends Sprite {
    private LittleGameEngine lge;
    private int state;
    private Point last;

    public MiHeroe() {
        super(new String[] { "heroe_idle_right", "heroe_idle_left", "heroe_run_right", "heroe_run_left" },
                new Point(550, 346), "Heroe");

        // acceso al motor de juegos
        lge = LittleGameEngine.GetLGE();

        // sus atributos
        SetOnEvents(LittleGameEngine.E_ON_UPDATE | LittleGameEngine.E_ON_COLLISION);
        SetShape("heroe_idle_left", 0);
        state = -1;
        SetBounds(new Rectangle(0, 0, 1920, 1056));
        last = GetPosition();
    }

    @Override
    public void OnUpdate(double dt) {
        // velocity = pixeles por segundo
        int velocity = 240;
        int pixels = (int)(velocity * dt);
        if( pixels == 0 ) pixels = 1;

        // la posiciona actual del heroe
        int x = GetX();
        int y = GetY();
        last = new Point(x, y);

        // cambiamos sus coordenadas, orientacion e imagen segun la tecla presionada
        if (lge.KeyPressed(KeyEvent.VK_RIGHT)) {
            x = x + pixels;
            if (state != 2) {
                SetShape("heroe_run_right", 0);
                state = 2;
            }
        } else if (lge.KeyPressed(KeyEvent.VK_LEFT)) {
            x = x - pixels;
            if (state != -2) {
                SetShape("heroe_run_left", 0);
                state = -2;
            }
        } else if (state == 2) {
            if (state != 1) {
                SetShape("heroe_idle_right", 0);
                state = 1;
            }
        } else if (state == -2) {
            if (state != -1) {
                SetShape("heroe_idle_left", 0);
                state = -1;
            }
        }

        if (lge.KeyPressed(KeyEvent.VK_UP))
            y = y + pixels;
        else if (lge.KeyPressed(KeyEvent.VK_DOWN))
            y = y - pixels;

        // siguiente imagen de la secuencia
        NextShape(dt, 0.050);

        // lo posicionamos
        SetPosition(x, y);
    }

    @Override
    public void OnCollision(double dt, ArrayList<GameObject> gobjs) {
        lge.PlaySound("poing", false, 50);
        SetPosition(last);
    }

}
