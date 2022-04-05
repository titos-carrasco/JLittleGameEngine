package test.Betty;

import java.awt.Dimension;
import java.awt.Point;

import rcr.lge.GameObject;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Zombie extends Sprite {
    private LittleGameEngine lge;

    private Dimension win_size;
    private char dir;
    private boolean active;

    public Zombie(String name, Dimension win_size) {
        super("zombie", new Point(0, 0), name);

        // acceso al motor de juegos
        lge = GetLGE();

        SetOnEvents(LittleGameEngine.E_ON_UPDATE);
        SetShape("zombie", 0);
        SetTag("zombie");
        UseColliders(true);
        active = true;
        this.win_size = win_size;

        // direccion inicial - Right, Down, Left, Up
        dir = "RDLU".charAt((int) (Math.random() * 4));
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

        // posicion respecto a Betty
        boolean abajo = y < by;
        boolean arriba = y > by;
        boolean izquierda = x < bx;
        boolean derecha = x > bx;

        // estrategia de movimiento
        String estrategia = "";

        if (dir == 'R') {
            if (abajo && izquierda)
                estrategia = "URDL";
            else if (abajo && derecha)
                estrategia = "UDRL";
            else if (arriba && izquierda)
                estrategia = "DRUL";
            else if (arriba && derecha)
                estrategia = "DURL";
            else if (arriba)
                estrategia = "DRUL";
            else if (abajo)
                estrategia = "URDL";
            else if (izquierda)
                estrategia = "RUDL";
            else if (derecha)
                estrategia = "UDRL";
        } else if (dir == 'L') {
            if (abajo && izquierda)
                estrategia = "UDLR";
            else if (abajo && derecha)
                estrategia = "LUDR";
            else if (arriba && izquierda)
                estrategia = "DULR";
            else if (arriba && derecha)
                estrategia = "DLUR";
            else if (arriba)
                estrategia = "DLUR";
            else if (abajo)
                estrategia = "ULDR";
            else if (izquierda)
                estrategia = "LUDR";
            else if (derecha)
                estrategia = "UDLR";
        } else if (dir == 'U') {
            if (abajo && izquierda)
                estrategia = "URLD";
            else if (abajo && derecha)
                estrategia = "ULRD";
            else if (arriba && izquierda)
                estrategia = "RLUD";
            else if (arriba && derecha)
                estrategia = "LRUD";
            else if (arriba)
                estrategia = "LRUD";
            else if (abajo)
                estrategia = "ULRD";
            else if (izquierda)
                estrategia = "RULD";
            else if (derecha)
                estrategia = "LURD";
        } else if (dir == 'D') {
            if (abajo && izquierda)
                estrategia = "RLDU";
            else if (abajo && derecha)
                estrategia = "LRDU";
            else if (arriba && izquierda)
                estrategia = "RDLU";
            else if (arriba && derecha)
                estrategia = "LDRU";
            else if (arriba)
                estrategia = "DLRU";
            else if (abajo)
                estrategia = "LRDU";
            else if (izquierda)
                estrategia = "RDLU";
            else if (derecha)
                estrategia = "LDRU";
        }

        // probamos cada movimiento de la estrategia
        for (int i = 0; i < estrategia.length(); i++) {
            char c = estrategia.charAt(i);
            int nx = x, ny = y;

            if (c == 'R')
                nx += pixels;
            else if (c == 'L')
                nx -= pixels;
            else if (c == 'U')
                ny += pixels;
            else if (c == 'D')
                ny -= pixels;

            // verificamos que no colisionemos con un muro u otro zombie
            SetPosition(nx, ny);
            GameObject[] gobjs = lge.IntersectGObjects(this);
            boolean collision = false;
            for (GameObject gobj : gobjs) {
                String tag = gobj.GetTag();
                if (tag.equals("zombie") || tag.equals("muro")) {
                    collision = true;
                    break;
                }
            }

            if (!collision) {
                dir = c;

                // tunel?
                if (nx < -16)
                    nx = win_size.width - 16;
                else if (nx > win_size.width - 16)
                    nx = -16;
                SetPosition(nx, ny);
                break;
            }

            // otro intento
            SetPosition(x, y);
        }

        // siguiente imagen de la secuencia
        NextShape(dt, 0.1);
    }
}