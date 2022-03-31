package test.pong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;

public class Pong implements IEvents {
    private LittleGameEngine lge;
    private int paddle_speed = 240;

    public Pong() {
        // creamos el juego
        Dimension win_size = new Dimension(640, 640);

        lge = LittleGameEngine.Init(win_size, "Ping", new Color(0x000000));
        lge.SetOnMainUpdate(this);
        lge.SetOnEvents(LittleGameEngine.E_ON_UPDATE | LittleGameEngine.E_ON_COLLISION);

        // cargamos los recursos que usaremos
        String resource_dir = lge.GetRealPath(this, "../resources");

        lge.LoadTTFFont("monospace.plain.16", resource_dir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 620), new Dimension(640, 20), "infobar");
        lge.AddGObjectGUI(infobar);

        // el campo de juego
        Canvas field = new Canvas(new Point(24, 34), new Dimension(592, 526), "field");
        field.Fill(new Color(0, 0, 100));
        lge.AddGObject(field, 0);

        // los bordes
        Canvas wall = new Canvas(new Point(0, 560), new Dimension(640, 4));
        wall.Fill(Color.WHITE);
        wall.SetTag("wall-horizontal");
        wall.UseColliders(true);
        lge.AddGObject(wall, 1);

        wall = new Canvas(new Point(0, 30), new Dimension(640, 4));
        wall.Fill(Color.WHITE);
        wall.SetTag("wall-horizontal");
        wall.UseColliders(true);
        lge.AddGObject(wall, 1);

        wall = new Canvas(new Point(20, 34), new Dimension(4, 526));
        wall.Fill(Color.WHITE);
        wall.SetTag("wall-vertical");
        wall.UseColliders(true);
        lge.AddGObject(wall, 1);

        wall = new Canvas(new Point(616, 34), new Dimension(4, 526));
        wall.Fill(Color.WHITE);
        wall.SetTag("wall-vertical");
        wall.UseColliders(true);
        lge.AddGObject(wall, 1);

        // los actores
        Ball ball = new Ball(new Point(320, 400), new Dimension(8, 8), "ball");
        lge.AddGObject(ball, 1);

        Canvas paddle = new Canvas(new Point(90, 270), new Dimension(8, 60), "user-paddle");
        paddle.Fill(Color.WHITE);
        paddle.SetTag("paddle");
        paddle.UseColliders(true);
        paddle.SetBounds(field.GetRectangle());
        lge.AddGObject(paddle, 1);

        paddle = new Canvas(new Point(540, 270), new Dimension(8, 60), "system-paddle");
        paddle.Fill(Color.WHITE);
        paddle.SetTag("paddle");
        paddle.UseColliders(true);
        paddle.SetBounds(field.GetRectangle());
        lge.AddGObject(paddle, 1);
    }

    @Override
    public void OnMainUpdate(double dt) {
        // abortamos con la tecla Escape
        if (lge.KeyPressed(KeyEvent.VK_ESCAPE))
            lge.Quit();

        // mostramos la info
        Point mouse_position = lge.GetMousePosition();
        boolean[] mouse_buttons = lge.GetMouseButtons();

        String info = String.format("FPS: %07.2f - gObjs: %03d - Mouse: (%3d,%3d) (%d,%d,%d)", 1.0 / lge.GetFPS(),
                lge.GetCountGObjects(), mouse_position.x, mouse_position.y, mouse_buttons[0] ? 1 : 0,
                mouse_buttons[1] ? 1 : 0, mouse_buttons[2] ? 1 : 0);
        Canvas infobar = (Canvas) lge.GetGObject("infobar");
        infobar.Fill(new Color(0x80808080, true));
        infobar.DrawText(info, new Point(50, 5), "monospace.plain.16", Color.WHITE);

        // user paddle
        Canvas user_paddle = (Canvas) lge.GetGObject("user-paddle");
        double speed = paddle_speed * dt;
        int x = user_paddle.GetX();
        int y = user_paddle.GetY();

        if (lge.KeyPressed(KeyEvent.VK_UP))
            user_paddle.SetPosition(x, (int) (y + speed));
        else if (lge.KeyPressed(KeyEvent.VK_DOWN))
            user_paddle.SetPosition(x, (int) (y - speed));

        // la pelota
        Ball ball = (Ball) lge.GetGObject("ball");
        //int bx = ball.GetX();
        int by = ball.GetY();

        // system paddle
        Canvas system_paddle = (Canvas) lge.GetGObject("system-paddle");
        int px = system_paddle.GetX();
        int py = system_paddle.GetY();
        //int pw = system_paddle.GetWidth();
        int ph = system_paddle.GetHeight();

        if (py + ph / 2 < by)
            py = (int)(py + speed);
        else if (py + ph / 2 > by)
            py = (int)(py - speed);
        system_paddle.SetPosition(px, py);
    }

    // main loop
    public void Run(int fps) {
        lge.Run(fps);
    }

    // show time
    public static void main(String[] args) {
        Pong game = new Pong();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }
}
