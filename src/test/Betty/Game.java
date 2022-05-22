package test.Betty;

import java.awt.Color;
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
import rcr.lge.Position;
import rcr.lge.Size;
import rcr.lge.Sprite;

public class Game implements IEvents {
    private LittleGameEngine lge;

    private int[][] mapa;

    public Game() {
        // creamos el juego
        Size winSize = new Size(608, 736);

        lge = new LittleGameEngine(winSize, "Betty", new Color(0xFFFFFF));
        lge.setOnMainUpdate(this);
        // lge.showColliders(new Color(0xFF0000));

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "../resources");

        lge.loadImage("fondo", resourceDir + "/images/Betty/Fondo.png", false, false);
        lge.loadImage("betty_idle", resourceDir + "/images/Betty/idle-0*.png", false, false);
        lge.loadImage("betty_down", resourceDir + "/images/Betty/down-0*.png", false, false);
        lge.loadImage("betty_up", resourceDir + "/images/Betty/up-0*.png", false, false);
        lge.loadImage("betty_left", resourceDir + "/images/Betty/left-0*.png", false, false);
        lge.loadImage("betty_right", resourceDir + "/images/Betty/right-0*.png", false, false);
        lge.loadImage("zombie", resourceDir + "/images/Kenny/Zombie/zombie_walk*.png", false, false);
        lge.loadTTFFont("monospace.plain.16", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);
        lge.loadTTFFont("cool.plain.30", resourceDir + "/fonts/backlash.ttf", Font.PLAIN, 30);

        // agregamos el fondo
        Sprite fondo = new Sprite("fondo", new Position(0, 0), "fondo");
        lge.addGObject(fondo, 0);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Position(0, 0), new Size(640, 20), "infobar");
        lge.addGObjectGUI(infobar);

        // cargamos el mapa en memoria
        try {
            String fname = new URI(resourceDir + "/images/Betty/Mapa.txt").getPath();
            mapa = new int[22][19];
            int x = 0, y = 0;
            Scanner scanner = new Scanner(new File(fname));
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");
                for (x = 0; x < line.length; x++)
                    mapa[y][x] = Integer.valueOf(line[x]);
                y++;
            }
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // agregamos a Betty
        Betty betty = new Betty("Betty", winSize);
        betty.setPosition(32 * 9, 32 * 9);
        lge.addGObject(betty, 1);

        // agregamos 3 zombies
        for (int i = 0; i < 3; i++) {
            Zombie zombie = new Zombie("Zombie-" + i, winSize);
            zombie.setPosition(32 + 32 * 4 + 32 * (i * 4), 32 * 21);
            lge.addGObject(zombie, 1);
        }

        // agregamos los muros para las colisiones (segun el mapa)
        for (int y = 0; y < mapa.length; y++)
            for (int x = 0; x < mapa[y].length; x++)
                if (mapa[y][x] == 1) {
                    GameObject muro = new GameObject(new Position(x * 32, 32 + y * 32), new Size(32, 32));
                    muro.enableCollider(true);
                    muro.setTag("muro");
                    lge.addGObject(muro, 1);
                }
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
        infobar.drawText(info, new Position(50, 16), "monospace.plain.16", Color.WHITE);
    }

    // main loop
    public void Run(int fps) {
        lge.run(fps);
    }

    // show time
    public static void main(String[] args) {
        Game game = new Game();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
