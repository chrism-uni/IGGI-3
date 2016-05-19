package core;

 import utils.Vector2d;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import static core.Constants.*;

public class AsteroidTwoFace extends Asteroid implements PolyContains {

    Path2D redFace, greenFace;
    AffineTransform at;

    public AsteroidTwoFace(GameState game, Vector2d s, Vector2d v, int index) {
        super(game, s, v, index);
        at = new AffineTransform();
        at.setToRotation(rotRate);
    }

    public boolean dead() {
        return dead;
    }

    public void setPolygon() {
        super.setPolygon();
        // this will set up the points, but now we make two paths

        int split = nPoints/2;


        redFace = new java.awt.geom.Path2D.Double();
        redFace.moveTo(px[0], py[0]);
        for (int i=1; i<=split; i++) {
            redFace.lineTo(px[i],py[i]);
        }
        redFace.closePath();

        greenFace = new java.awt.geom.Path2D.Double();
        greenFace.moveTo(px[split], py[split]);
        for (int i=split; i<nPoints; i++) {
            greenFace.lineTo(px[i],py[i]);
        }
        greenFace.lineTo(px[0], py[0]);
        greenFace.closePath();

        //    now create paths for drawing them

        //    System.out.println(Arrays.toString(px));
        //    System.out.println(Arrays.toString(py));
    }

    public void draw(Graphics2D g) {
        // store coordinate system
        AffineTransform at = g.getTransform();
        g.translate(s.x, s.y);
        // System.out.println("Drawing at " + s);
        // g.rotate(rot);
        // g.fillPolygon(px, py, px.length);
        g.setColor(Color.red);
        g.fill(redFace);

        g.setColor(Color.green);
        g.fill(greenFace);
        // restore original coordinate system
        g.setTransform(at);
    }

    public void update() {
        super.update();
        // rotate the paths
        redFace.transform(at);
        greenFace.transform(at);
    }

    public void hit() {
        System.out.println("Red hit = " + redHit);
        if (!redHit) {
            dead = true;
            game.asteroidTwoFaceDeath(this);
        } else {
            // shoot a missile back at the ship
            Missile m = new Missile(s, new Vector2d(0, 0, true));
            double releaseVelocity = 5.0; // Math.min(releaseVelocity, maxRelease);
            m.ttl = 60;
            Vector2d d = new Vector2d(true);
            d.set(game.ship.s);
            d.subtract(s);
            d.normalise();
            m.v.add(d, releaseVelocity);
            // make it clear the ship

            m.s.add(m.v, (r() + missileRadius) * 1.5 / m.v.mag());
            releaseVelocity = 0;
            game.add(m);

        }
    }

    boolean redHit = false;

    public boolean contains(Vector2d p) {
        if (redFace.contains(p.x-s.x, p.y-s.y))
            redHit = true;
        return redFace.contains(p.x-s.x, p.y-s.y) || greenFace.contains(p.x-s.x, p.y-s.y);
    }



}
