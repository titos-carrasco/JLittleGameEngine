package test.particles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;

import rcr.lge.Canvas;
import rcr.lge.IEvents;
import rcr.lge.LittleGameEngine;
import rcr.lge.Position;
import rcr.lge.Size;

public class Particles implements IEvents {
    private LittleGameEngine lge;
    private Canvas panel;
    int numParticles = 500;
    Particle[] particles;

    public Particles() {
        // creamos el juego
        Size winSize = new Size(800, 440);

        lge = new LittleGameEngine(winSize, "Particles", new Color(0xFFFFFF));
        lge.setOnMainUpdate(this);

        // cargamos los recursos que usaremos
        String resourceDir = lge.getRealPath(this, "../resources");

        lge.loadTTFFont("monospace.plain.16", resourceDir + "/fonts/FreeMono.ttf", Font.PLAIN, 16);

        // agregamos la barra de info
        Canvas infobar = new Canvas(new Position(0, 0), new Size(800, 20), "infobar");
        lge.addGObjectGUI(infobar);

        // un canvas para plotear
        panel = new Canvas(new Position(0, 0), new Size(800, 600), "Panel");
        panel.fill(Color.WHITE);
        lge.addGObject(panel, 1);

        // las particulas
        numParticles = 500;
        particles = new Particle[numParticles];
        for (int i = 0; i < numParticles; i++) {
            double x = 300 + Math.random() * 200;
            double y = 100 + Math.random() * 100;
            double vx = -120 + Math.random() * 240;
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
        infobar.drawText(info, new Position(140, 0), "monospace.plain.16", Color.BLACK);

        // las particulas
        for (int i = 0; i < numParticles; i++) {
            Particle particle = particles[i];
            particle.Update(dt);
        }

        panel.fill(Color.WHITE);
        for (int i = 0; i < numParticles; i++) {
            Particle particle = particles[i];
            double x = particle.x;
            double y = particle.y;
            int r = (int) (particle.m * 5);
            panel.drawRectangle(new Position(x, y), new Size(r, r), Color.BLACK, false);
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
