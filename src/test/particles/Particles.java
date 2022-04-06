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
    int numParticles = 500;
    Particle[] particles;

    public Particles() {
        // creamos el juego
        Dimension winSize = new Dimension(800, 600);

        lge = new LittleGameEngine(winSize, "Particles", new Color(0xFFFFFF));
        lge.setOnMainUpdate(this);

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "../resources");

        lge.loadTTFFont("monospace.plain.16", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Point(0, 580), new Dimension(800, 20), "infobar");
        lge.addGObjectGUI(infobar);

        // un canvas para plotear
        panel = new Canvas(new Point(0, 0), new Dimension(800, 600), "Panel");
        panel.fill(Color.WHITE);
        lge.addGObject(panel, 1);

        // las particulas
        numParticles = 500;
        particles = new Particle[numParticles];
        for (int i = 0; i < numParticles; i++) {
            double x = (int) (100 + Math.random() * 600);
            double y = (int) (300 + Math.random() * 200);
            double vx = -60 + Math.random() * 120;
            double vy = -120 + Math.random() * 240;
            double m = 0.1 + Math.random();
            particles[i] = new Particle(x, y, vx, vy, m);
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
        infobar.fill(new Color(0x10202020, true));
        infobar.drawText(info, new Point(140, 5), "monospace.plain.16", Color.BLACK);

        // las particulas
        for (int i = 0; i < numParticles; i++) {
            Particle particle = particles[i];
            particle.Update(dt);
        }

        panel.fill(Color.WHITE);
        for (int i = 0; i < numParticles; i++) {
            Particle particle = particles[i];
            int x = (int) Math.round(particle.x);
            int y = (int) Math.round(particle.y);
            int r = (int) Math.round(particle.m * 10);
            panel.drawRectangle(new Point(x, y), new Dimension(r, r), Color.BLACK, false);
        }
    }

    // main loop
    public void Run(int fps) {
        lge.run(fps);
    }

    // show time
    public static void main(String[] args) {
        Particles game = new Particles();
        game.Run(60);
        System.out.println("Eso es todo!!!");
    }

}
