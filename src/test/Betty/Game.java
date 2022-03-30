package test.Betty;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import rcr.lge.Canvas;
import rcr.lge.GameObject;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;
import rcr.lge.Sprite;

public class Game implements IEvents {
    private LittleGameEngine lge;

    public Game() {
        // la ruta a los recursos del juego
        String resource_dir = getClass().getResource("../resources").getPath();

        // creamos el juego
        Dimension win_size = new Dimension(608, 736);

        lge = LittleGameEngine.Init(win_size, "Colliders", new Color(0xFFFF00));
        lge.ShowColliders(new Color(0xFF0000));
        lge.SetOnMainUpdate(this);
        lge.SetOnEvents(LittleGameEngine.E_ON_UPDATE | LittleGameEngine.E_ON_COLLISION);

        // cargamos los recursos que usaremos
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

        // agregamos a Betty
        Betty betty = new Betty("Betty", win_size);
        betty.SetPosition(32 * 9, 32 * 13);
        betty.UseColliders(true);
        lge.AddGObject(betty, 1);

        // agregamos 3 zombies
        for (int i = 0; i < 3; i++) {
            Zombie zombie = new Zombie("Zombie-" + i);
            zombie.SetPosition(32 + 32 * 4 + 32 * (i * 4), 32 * 1);
            zombie.UseColliders(true);
            lge.AddGObject(zombie, 1);
        }

        // agregamos los muros para las colisiones
        // 1. fue creado con Tiled
        // 2 exportado desde Tiled como .png y editado para dejar sus contornos
        // 4. exportado desde Tiled como .csv para conocer las coordenadas de los muros
        try {
            File f = new File(resource_dir + "/images/Betty/Fondo.csv");
            FileReader fr;
            fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String line;
            int y = win_size.height - 32;
            while ((line = br.readLine()) != null) {
                int x = 0;
                for (String elem : line.split(",")) {
                    if (elem.equals("muro")) {
                        GameObject muro = new GameObject(new Point(x, y), new Dimension(32, 32));
                        muro.SetTag("muro");
                        muro.UseColliders(true);
                        lge.AddGObject(muro, 1);
                    }
                    x = x + 32;
                }
                y = y - 32;
            }
            br.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // comenzamos
        betty.SetAlive(true);
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
                lge.GetGObjects().length, mouse_position.x, mouse_position.y, mouse_buttons[0] ? 1 : 0,
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
