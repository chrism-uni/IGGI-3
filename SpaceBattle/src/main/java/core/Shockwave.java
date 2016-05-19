package core;

import utils.Vector2d;

import java.awt.*;

/**
 * Created by chrismadge on 19/05/2016.
 */
public class Shockwave extends GameObject {
    int ttl = 0;
    int waves = 10;
    int maxRadius = 20;

    protected Shockwave(Vector2d s, Vector2d v, int ttl) {
        super(s, v);
        this.ttl = ttl;
        this.r = Math.pow(waves-1,2);
    }

    protected Shockwave(Vector2d s, Vector2d v) {
        this(s, v, 15);
    }

    @Override
    public void update() {
        ttl--;
    }

    @Override
    public void draw(Graphics2D g) {
        if (dead()) return;
        for(int i = 1; i <= waves; i++) {
            int radius = 20+(int)(Math.pow(i,2));
            float alpha = 0.5f-(i/(float)waves)/2; //0.5 going down to 0.1
            drawWave(g, new Color(1f, 0f, 0f, alpha), radius);
        }
    }

    private void drawWave(Graphics2D g, Color color, int r) {
        g.setColor(color);
        g.drawOval((int) (s.x-r), (int) (s.y-r), r * 2, r * 2);
    }

    @Override
    public GameObject copy() {
        return new Shockwave(s, v, ttl);
    }

    @Override
    public boolean dead() {
        return ttl <= 0;
    }
}
