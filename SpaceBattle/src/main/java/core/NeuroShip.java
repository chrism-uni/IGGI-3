package core;


import utils.Vector2d;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import static core.Constants.*;

public class NeuroShip extends GameObject {

    // define the shape of the ship
    static int[] xp = {-2, 0, 2, 0};
    static int[] yp = {2, -2, 2, 0};

    // this is the thrust poly that will be drawn when the ship
    // is thrusting
    static int[] xpThrust = {-2, 0, 2, 0};
    static int[] ypThrust = {2, 3, 2, 0};
    public static double scale = 5;

    // define how quickly the ship will rotate
    static double steerStep = 10 * Math.PI / 180;
    static double maxSpeed = 3;

    // this is the friction that makes the ship slow down over time
    static double loss = 0.99;

    double releaseVelocity = 0;
    double minVelocity = 2;
    public static double maxRelease = 10;
    Color color = Color.white;
    boolean thrusting = false;

    static double gravity = 0.0;

    // position and velocity
    public Vector2d d;

    // played id (used for drawing)
    int playerID;

    public static double MIN_FORCE = 5.0;
    public static double MAX_FORCE = 10.0;

    public NeuroShip(Vector2d s, Vector2d v, Vector2d d, int playerID) {
        super(new Vector2d(s, true), new Vector2d(v, true));
        this.d = new Vector2d(d, true);
        this.playerID = playerID;
    }

    public NeuroShip copy() {
        NeuroShip ship = new NeuroShip(s, v, d, playerID);
        ship.releaseVelocity = releaseVelocity;
        return ship;
    }

    public double r() {
        return scale * 2.4;
    }

    public void addForceRotate(double min_force, double max_force, Vector2d dir)
    {
        addRepulsiveForce(min_force,  max_force, dir);
        //Random rotation:
        int rotation = (int) (Math.random()*3);
        double turnAngle = rotation == 0? -1 : rotation == 1 ? 1 : 0;
        d.rotate(turnAngle * steerStep * (Math.random() ));
    }

    public void addInverseForce(double min_force, double max_force)
    {
        Vector2d force = new Vector2d(-d.x/d.mag(), -d.y/d.mag(), true);

        //Random strength (between 1 and 5, for instance)
        double strength = min_force + Math.random()*(max_force-min_force);
        force.multiply(strength);
        //System.out.println("force: x=" + force.x + " y=" + force.y);

        v.add(force);
    }


    public void addRepulsiveForce(double min_force, double max_force, Vector2d dir)
    {
        double strength = min_force + Math.random()*(max_force-min_force);
        //System.out.println(" ->" + strength);
        v.add(new Vector2d(strength * dir.x, strength* dir.y, true));
    }


    public void reset() {
        s.set(width / 2, height / 2);
        v.zero();
        d.set(0, -1);
        dead = false;
        // System.out.println("Reset the ship ");
    }

    private static double clamp(double v, double min, double max) {
        if (v > max) {
            return max;
        }

        if (v < min) {
            return min;
        }

        return v;
    }

    public NeuroShip update(Action action) {

        // what if this is always on?

        // action has fields to specify thrust, turn and shooting

        // action.thrust = 1;

        if (action.thrust > 0) {
            thrusting = true;
        } else {
            thrusting = false;
        }

        //prevent people from cheating
        double thrustSpeed = clamp(action.thrust, 0, 1);
        double turnAngle = clamp(action.turn, -1, 1);

        d.rotate(turnAngle * steerStep);
        v.add(d, thrustSpeed * t * 0.3 / 2);
        v.y += gravity;
        // v.x = 0.5;
        v.multiply(loss);

        // This is fairly basic, but it'll do for now...
        v.x = clamp(v.x, -maxSpeed, maxSpeed);
        v.y = clamp(v.y, -maxSpeed, maxSpeed);

        s.add(v);

        return this;
    }

    public double dotTo(NeuroShip other)
    {
        Vector2d diff = Vector2d.subtract(other.s,this.s);
        Vector2d front = new Vector2d(this.d, true);
        front.normalise();
        diff.normalise();

        return diff.dot(front);
    }


    public double dotDirections(NeuroShip other)
    {
        Vector2d thisFront = new Vector2d(this.d, true);
        Vector2d otherFront = new Vector2d(other.d, true);
        thisFront.normalise();
        otherFront.normalise();

        return thisFront.dot(otherFront);
    }

    public double distTo(NeuroShip other)
    {
        Vector2d diff = Vector2d.subtract(other.s,this.s);
        return diff.mag();
    }

    /**
     private void tryMissileLaunch() {
     // System.out.println("Trying a missile launch");
     if (releaseVelocity > maxRelease) {
     releaseVelocity = Math.max(releaseVelocity, missileMinVelocity * 2);
     Missile m = new Missile(s, new Vector2d(0, 0, true));
     releaseVelocity = Math.min(releaseVelocity, maxRelease);
     m.v.add(d, releaseVelocity);
     // make it clear the ship
     m.s.add(m.v, (r() + missileRadius) * 1.5 / m.v.mag());
     releaseVelocity = 0;
     // System.out.println("Fired: " + m);
     // sounds.fire();
     } else {
     // System.out.println("Failed!");
     }
     }
     **/
    public String toString() {
        return s + "\t " + v;
    }

    @Override
    public void update() {
        throw new IllegalArgumentException("You shouldn't be calling this...");
    }

    public void draw(Graphics2D g) {
        color = playerID == 0 ? Color.green : Color.blue;
        AffineTransform at = g.getTransform();
        g.translate(s.x, s.y);
        double rot = Math.atan2(d.y, d.x) + Math.PI / 2;
        g.rotate(rot);
        g.scale(scale, scale);
        g.setColor(color);
        g.fillPolygon(xp, yp, xp.length);
        if (thrusting) {
            g.setColor(Color.red);
            g.fillPolygon(xpThrust, ypThrust, xpThrust.length);
        }
        g.setTransform(at);
    }

    public void hit() {
        // super.hit();
        // System.out.println("Ship destroyed");
        dead = true;
        // sounds.play(sounds.bangLarge);
    }

    public boolean dead() {
        return dead;
    }

    public Rectangle2D getBound() {
        return new Rectangle2D.Double(s.x,s.y,Double.valueOf(xp[2]-xp[0]),Double.valueOf(yp[0]-yp[1]));
    }
    @Override
    public int getId() {
        return this.playerID;
    }
}
