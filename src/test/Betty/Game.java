package test.Betty;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URI;
import java.util.Scanner;

import rcr.lge.Canvas;
import rcr.lge.GameObject;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Game implements IEvents {
    private LittleGameEngine lge;

    private int[][] mapa;

    public Game() {
        // creamos el juego
        Dimension win_size = new Dimension(608, 736);

        lge = new LittleGameEngine(win_size, "Betty", new Color(0xFFFF00));
        lge.ShowColliders(new Color(0xFF0000));
        lge.SetOnMainUpdate(this);
        lge.SetOnEvents(LittleGameEngine.E_ON_UPDATE | LittleGameEngine.E_ON_COLLISION);

        // cargamos los recursos que usaremos
        String resource_dir = lge.GetRealPath(this, "../resources");

        lge.LoadImage("fondo", resource_dir + "/images/Betty/Fondo.png", false, false);
        lge.LoadImage("betty_idle", resource_dir + "/images/Betty/idle-0*.png", false, false);
        lge.LoadImage("betty_down", resource_dir + "/images/Betty/down-0*.png", false, false);
        lge.LoadImage("betty_up", resource_dir + "/images/Betty/up-0*.png", false, false);
        lge.LoadImage("betty_left", resource_dir + "/images/Betty/left-0*.png", false, false);
        lge.LoadImage("betty_right", resource_dir + "/images/Betty/right-0*.png", false, false);
        lge.LoadImage("zombie", resource_dir + "/images/Kenny/Zombie/zombie_walk*.png", false, false);
        lge.LoadTTFFont("monospace.plain.16", resource_dir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);
        lge.LoadTTFFont("cool.plain.30", resource_dir + "/fonts/backlash.ttf", Font.PLAIN, 30);

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new Point(0, 0), "fondo");
        lge.AddGObject(fondo, 0);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 714), new Dimension(640, 20), "infobar");
        lge.AddGObjectGUI(infobar);

        // cargamos el mapa en memoria
        try {
            String fname = new URI(resource_dir + "/images/Betty/Mapa.txt").getPath();
            mapa = new int[22][19];
            int x = 0, y = mapa.length - 1;
            Scanner scanner = new Scanner(new File(fname));
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");
                for (x = 0; x < line.length; x++)
                    mapa[y][x] = Integer.valueOf(line[x]);
                y--;
            }
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // agregamos a Betty
        Betty betty = new Betty("Betty", win_size);
        betty.SetPosition(32 * 9, 32 * 13);
        lge.AddGObject(betty, 1);

        // agregamos 3 zombies
        for (int i = 0; i < 3; i++) {
            Zombie zombie = new Zombie("Zombie-" + i, win_size);
            zombie.SetPosition(32 + 32 * 4 + 32 * (i * 4), 32 * 1);
            lge.AddGObject(zombie, 1);
        }

        // agregamos los muros para las colisiones (segun el mapa)
        for (int y = 0; y < mapa.length; y++)
            for (int x = 0; x < mapa[y].length; x++)
                if (mapa[y][x] == 1) {
                    GameObject muro = new GameObject(new Point(x * 32, y * 32),
                            new Dimension(32, 32));
                    muro.UseColliders(true);
                    lge.AddGObject(muro, 1);
                }

        // comenzamos
        betty.SetAlive(true);
        for (GameObject gobj : lge.GetGObjects("Zombie-*")) {
            Zombie zombie = (Zombie) gobj;
            zombie.SetActive(true);
        }
    }

    @Override
    public void OnMainUpdate(double dt) {
        // abortamos con la tecla Escape
        if (lge.KeyPressed(KeyEvent.VK_ESCAPE))
            lge.Quit();

        // mostramos la info
        Point mouse_position = lge.GetMousePosition();
        boolean[] mouse_buttons = lge.GetMouseButtons();

        String info = String.format("FPS: %07.2f - gObjs: %03d - Mouse: (%3d,%3d) (%d,%d,%d)", lge.GetFPS(),
                lge.GetCountGObjects(), mouse_position.x, mouse_position.y, mouse_buttons[0] ? 1 : 0,
                mouse_buttons[1] ? 1 : 0, mouse_buttons[2] ? 1 : 0);
        Canvas infobar = (Canvas) lge.GetGObject("infobar");
        infobar.Fill(new Color(0x80808080, true));
        infobar.DrawText(info, new Point(50, 5), "monospace.plain.16", Color.WHITE);
    }

    // main loop
    public void Run(int fps) {
        lge.Run(fps);
    }

    // show time
    public static void main(String[] args) {
        Game game = new Game();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
