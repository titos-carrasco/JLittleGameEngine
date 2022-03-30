package test.particles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;

public class Particles implements IEvents {
    private LittleGameEngine lge;
    private Canvas panel;
    int num_particles = 500;
    Particle[] particles;

    public Particles() {
        // la ruta a los recursos del juego
        String resource_dir = getClass().getResource("../resources").getPath();

        // creamos el juego
        Dimension win_size = new Dimension(800, 600);

        lge = LittleGameEngine.Init(win_size, "Particles", new Color(0xFFFFFF));
        lge.ShowColliders(new Color(0xFF0000));
        lge.SetOnMainUpdate(this);

        // cargamos los recursos que usaremos
        lge.LoadTTFFont("monospace.plain.16", resource_dir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 580), new Dimension(800, 20), "infobar");
        lge.AddGObjectGUI(infobar);

        // un canvas para plotear
        panel = new Canvas(new Point(0, 0), new Dimension(800, 600), "Panel");
        panel.Fill(Color.WHITE);
        lge.AddGObject(panel, 1);

        // las particulas
        num_particles = 500;
        particles = new Particle[num_particles];
        for (int i = 0; i < num_particles; i++)
            particles[i] = new Particle();
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
        infobar.Fill(new Color(0x10202020, true));
        infobar.DrawText(info, new Point(140, 5), "monospace.plain.16", Color.BLACK);

        // las particulas
        for (int i = 0; i < num_particles; i++) {
            Particle particle = particles[i];
            particle.Update(dt);
        }

        panel.Fill(Color.WHITE);
        for (int i = 0; i < num_particles; i++) {
            Particle particle = particles[i];
            int x = (int) Math.round(particle.x);
            int y = (int) Math.round(particle.y);
            int r = (int) Math.round(particle.m * 10);
            panel.DrawRectangle(new Point(x, y), new Dimension(r, r), Color.BLACK, false);
        }
    }

    // main loop
    public void Run(int fps) {
        lge.Run(fps);
    }

    // show time
    public static void main(String[] args) {
        Particles game = new Particles();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
