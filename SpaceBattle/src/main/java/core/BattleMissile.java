package core;

/**
 * Created by jliu on 13/05/16.
 */
import utils.Vector2d;
import java.awt.*;

import static core.Constants.*;

public class BattleMissile extends GameObject {

    int ttl;
    Color color;

    public BattleMissile(Vector2d s, Vector2d v, int id) {
        super(s, v);
        this.id = id;
        color = pColors[id];
        ttl = missileTTL;
        r = 4;
    }

    @Override
    public void update() {
        if (!dead()) {
            s.add(v);
            ttl--;
        }
    }

    @Override
    public BattleMissile copy() {
        BattleMissile copy = new BattleMissile(s, v, id);
        updateClone(copy);
        copy.ttl = ttl;
        copy.color = color;
        return copy;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) (s.x-r), (int) (s.y-r), (int) r * 2, (int) r * 2);
    }

    public boolean dead() {
        return ttl <= 0;
    }

    public void hit() {
        // kill it by setting ttl to zero
        ttl = 0;
    }

    public String toString() {
        return ttl + " :> " + s;
    }

}
