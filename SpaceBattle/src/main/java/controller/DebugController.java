package controller;

import core.NeuroShip;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Draw debug stuff from the center of the ship rather than top left of the world.
 */
public abstract class DebugController implements RenderableBattleController {

    @Override
    public void render(Graphics2D g, NeuroShip s) {
        AffineTransform at = g.getTransform();
        g.translate(s.s.x, s.s.y);
        double rot = Math.atan2(s.d.y, s.d.x) + Math.PI / 2;
        g.rotate(rot);
        render(g);
        g.setTransform(at);
    }

    public abstract void render(Graphics2D g);

}
