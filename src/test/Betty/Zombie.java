package test.Betty;

import java.awt.Point;

import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Zombie extends Sprite {
    private LittleGameEngine lge;
    private char dir;
    private boolean active;

    public Zombie(String name) {
        super("zombie", new Point(0, 0), name);

        // acceso al motor de juegos
        lge = LittleGameEngine.GetLGE();

        SetOnEvents(LittleGameEngine.E_ON_UPDATE);
        SetShape("zombie", 0);
        SetTag("zombie");
        dir = 'R';
        active = false;
    }

    public void SetActive(boolean active) {
        this.active = active;
    }

    @Override
    public void OnUpdate(double dt) {
        if (!active)
            return;

        // velocity = pixeles por segundo
        // double velocity = 120;
        // int pixels = (int)(velocity*dt);
        int pixels = 2;

        // las coordenadas de Betty
        Betty betty = (Betty) lge.GetGObject("Betty");
        int bx = betty.GetX();
        int by = betty.GetY();

        // nuestra posicion actual
        int x = GetX();
        int y = GetY();
        int xori = x;
        int yori = y;

        // nuevas coordenadas
        String orden = "";

        boolean abajo = y < by;
        boolean arriba = y > by;
        boolean izquierda = x < bx;
        boolean derecha = x > bx;

        if (dir == 'R') {
            if (abajo && izquierda)
                orden = "URDL";
            else if (abajo && derecha)
                orden = "UDRL";
            else if (arriba && izquierda)
                orden = "DRUL";
            else if (arriba && derecha)
                orden = "DURL";
            else if (arriba)
                orden = "DRUL";
            else if (abajo)
                orden = "URDL";
            else if (izquierda)
                orden = "RUDL";
            else if (derecha)
                orden = "UDRL";
        } else if (dir == 'L') {
            if (abajo && izquierda)
                orden = "UDLR";
            else if (abajo && derecha)
                orden = "LUDR";
            else if (arriba && izquierda)
                orden = "DULR";
            else if (arriba && derecha)
                orden = "DLUR";
            else if (arriba)
                orden = "DLUR";
            else if (abajo)
                orden = "ULDR";
            else if (izquierda)
                orden = "LUDR";
            else if (derecha)
                orden = "UDLR";
        } else if (dir == 'U') {
            if (abajo && izquierda)
                orden = "URLD";
            else if (abajo && derecha)
                orden = "ULRD";
            else if (arriba && izquierda)
                orden = "RLUD";
            else if (arriba && derecha)
                orden = "LRUD";
            else if (arriba)
                orden = "LRUD";
            else if (abajo)
                orden = "ULRD";
            else if (izquierda)
                orden = "RULD";
            else if (derecha)
                orden = "LURD";
        } else if (dir == 'D') {
            if (abajo && izquierda)
                orden = "RLDU";
            else if (abajo && derecha)
                orden = "LRDU";
            else if (arriba && izquierda)
                orden = "RDLU";
            else if (arriba && derecha)
                orden = "LDRU";
            else if (arriba)
                orden = "DLRU";
            else if (abajo)
                orden = "LRDU";
            else if (izquierda)
                orden = "RDLU";
            else if (derecha)
                orden = "LDRU";
        }

        for (int i = 0; i < orden.length(); i++) {
            char c = orden.charAt(i);

            if (c == 'R')
                x = x + pixels;
            else if (c == 'L')
                x = x - pixels;
            else if (c == 'U')
                y = y + pixels;
            else if (c == 'D')
                y = y - pixels;
            SetPosition(x, y);

            // collisions = Engine.GetCollisions( self )
            // bloqueos = [ gobj for gobj in collisions if gobj.GetTag() == "muro" or
            // gobj.GetTag() == "zombie" ]
            // if( len( bloqueos ) == 0 ):
            // self.dir = c
            // break
            SetPosition(xori, yori);
        }

        // tunel?
        x = GetX();
        y = GetY();
        // w, h = Engine.GetCamera().GetSize()
        // if( x < -16 ): x = w - 16
        // elif( x > w - 16 ): x = -16
        SetPosition(x, y);

        // siguiente imagen de la secuencia
        NextShape(dt, 0.100);
    }
}
