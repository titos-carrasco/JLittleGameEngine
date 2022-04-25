package test.particles;

public class Particle {

    protected double x, y, vx, vy, m;

    public Particle(double x, double y, double vx, double vy, double m) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.m = m;
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
        y = y - vy * dt;
    }
}
