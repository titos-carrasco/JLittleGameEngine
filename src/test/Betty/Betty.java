package test.Betty;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Betty extends Sprite {
    private LittleGameEngine lge;

    private boolean alive;
    private Dimension win_size;
    private Point last;

    public Betty(String name, Dimension win_size) {
        super(new String[] { "betty_idle", "betty_down", "betty_up", "betty_left", "betty_right" }, new Point(0, 0),
                name);

        // acceso al motor de juegos
        lge = LittleGameEngine.GetLGE();

        SetOnEvents(LittleGameEngine.E_ON_UPDATE | LittleGameEngine.E_ON_COLLISION);
        SetShape("betty_idle", 0);
        SetTag("Betty");
        alive = false;
        this.win_size = win_size;
    }

    public boolean IsAlive() {
        return alive;
    }

    public void SetAlive( boolean alive) {
        this.alive = alive;
        SetShape("betty_idle", 0);
    }

    @Override
    public void OnUpdate(double dt) {
        // solo si estoy viva
        if (!alive)
            return;

        // velocity = pixeles por segundo
        // double velocity = 120;
        // int pixels = (int)velocity*dt;
        int pixels = 2;

        // nuestra posicion actual y tamano
        int x = GetX();
        int y = GetY();
        last = new Point( x, y );

        // cambiamos sus coordenadas e imagen segun la tecla presionada
        int idx = GetCurrentIdx();
        if (lge.KeyPressed(KeyEvent.VK_RIGHT)) {
            SetShape("betty_right", idx);
            x = x + pixels;
        } else if (lge.KeyPressed(KeyEvent.VK_LEFT)) {
            SetShape("betty_left", idx);
            x = x - pixels;
        } else if (lge.KeyPressed(KeyEvent.VK_UP)) {
            SetShape("betty_up", idx);
            y = y + pixels;
        } else if (lge.KeyPressed(KeyEvent.VK_DOWN)) {
            SetShape("betty_down", idx);
            y = y - pixels;
        } else {
            SetShape("betty_idle", idx);
        }

        // tunel?
        if( x < -16 ) x = win_size.width - 16;
        else if( x > win_size.width - 16 ) x = -16;

        // siguiente imagen de la secuencia
        NextShape(dt, 0.009);
        SetPosition(x, y);
    }

    @Override
    public void OnCollision(double dt, ArrayList<GameObject> gobjs) {
        SetPosition( last );
    }
}
