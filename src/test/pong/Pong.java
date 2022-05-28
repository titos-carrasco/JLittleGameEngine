package test.pong;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;
import rcr.lge.Position;
import rcr.lge.Size;

public class Pong implements IEvents {
    private LittleGameEngine lge;
    private int paddleSpeed = 240;

    public Pong() {
        // creamos el juego
        Size winSize = new Size(640, 640);

        lge = new LittleGameEngine(winSize, "Ping", new Color(0x000000));
        lge.setOnMainUpdate(this);
        // lge.showColliders(new Color(255, 0, 0));

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "../resources");

        lge.loadTTFont("monospace.plain.16", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Position(0, 0), new Size(640, 20), "infobar");
        lge.addGObjectGUI(infobar);

        // el campo de juego
        Canvas field = new Canvas(new Position(24, 80), new Size(592, 526), "field");
        field.fill(new Color(0, 0, 100));
        lge.addGObject(field, 0);

        // los bordes
        Canvas wall = new Canvas(new Position(0, 76), new Size(640, 4));
        wall.fill(Color.WHITE);
        wall.setTag("wall-horizontal");
        wall.enableCollider(true);
        lge.addGObject(wall, 1);

        wall = new Canvas(new Position(0, 606), new Size(640, 4));
        wall.fill(Color.WHITE);
        wall.setTag("wall-horizontal");
        wall.enableCollider(true);
        lge.addGObject(wall, 1);

        wall = new Canvas(new Position(20, 80), new Size(4, 526));
        wall.fill(Color.WHITE);
        wall.setTag("wall-vertical");
        wall.enableCollider(true);
        lge.addGObject(wall, 1);

        wall = new Canvas(new Position(616, 80), new Size(4, 526));
        wall.fill(Color.WHITE);
        wall.setTag("wall-vertical");
        wall.enableCollider(true);
        lge.addGObject(wall, 1);

        // los actores
        Ball ball = new Ball(new Position(320, 400), new Size(8, 8), "ball");
        lge.addGObject(ball, 1);

        Canvas paddle = new Canvas(new Position(90, 270), new Size(8, 60), "user-paddle");
        paddle.fill(Color.WHITE);
        paddle.setTag("paddle");
        paddle.enableCollider(true);
        paddle.setBounds(field.getRectangle());
        lge.addGObject(paddle, 1);

        paddle = new Canvas(new Position(540, 270), new Size(8, 60), "system-paddle");
        paddle.fill(Color.WHITE);
        paddle.setTag("paddle");
        paddle.enableCollider(true);
        paddle.setBounds(field.getRectangle());
        lge.addGObject(paddle, 1);
    }

    @Override
    public void onMainUpdate(double dt) {
        // abortamos con la tecla Escape
        if (lge.keyPressed(KeyEvent.VK_ESCAPE))
            lge.quit();

        // mostramos la info
        Point mousePosition = lge.getMousePosition();
        boolean[] mouseButtons = lge.getMouseButtons();

        String info = String.format("FPS: %07.2f - gObjs: %03d - Mouse: (%3d,%3d) (%d,%d,%d)", lge.getFPS(),
                lge.getCountGObjects(), mousePosition.x, mousePosition.y, mouseButtons[0] ? 1 : 0,
                mouseButtons[1] ? 1 : 0, mouseButtons[2] ? 1 : 0);
        Canvas infobar = (Canvas) lge.getGObject("infobar");
        infobar.fill(new Color(0x80808080, true));
        infobar.drawText(info, new Position(50, 0), "monospace.plain.16", Color.WHITE);

        // user paddle
        Canvas userPaddle = (Canvas) lge.getGObject("user-paddle");
        double speed = paddleSpeed * dt;
        double x = userPaddle.getX();
        double y = userPaddle.getY();

        if (lge.keyPressed(KeyEvent.VK_UP))
            userPaddle.setPosition(x, y - speed);
        else if (lge.keyPressed(KeyEvent.VK_DOWN))
            userPaddle.setPosition(x, y + speed);

        // la pelota
        Ball ball = (Ball) lge.getGObject("ball");
        // double bx = ball.getX();
        double by = ball.getY();

        // system paddle
        Canvas systemPaddle = (Canvas) lge.getGObject("system-paddle");
        double px = systemPaddle.getX();
        double py = systemPaddle.getY();
        // int pw = systemPaddle.getWidth();
        int ph = systemPaddle.getHeight();

        if (py + ph / 2.0 < by)
            py = py + speed;
        else if (py + ph / 2.0 > by)
            py = py - speed;
        systemPaddle.setPosition(px, py);
    }

    // main loop
    public void Run(int fps) {
        lge.run(fps);
    }

    // show time
    public static void main(String[] args) {
        Pong game = new Pong();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }
}
