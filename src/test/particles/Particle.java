package test.particles;

public class Particle {

    protected double x, y, vx, vy, m;

    public Particle() {
        x = 100 + Math.random() * 600;
        y = 300 + Math.random() * 200;
        vx = -60 + Math.random() * 120;
        vy = -60 + Math.random() * 240;
        m = 0.1 + Math.random();
    }

    public double ComputeYForce() {
        // return m * -9.81;
        return m * -60;
    }

    public void Update(double dt) {
        double fx = 0;
        double fy = ComputeYForce();
        double ax = fx / m;
        double ay = fy / m;
        vx = vx + ax * dt;
        vy = vy + ay * dt;
        x = x + vx * dt;
        y = y + vy * dt;
    }
}
