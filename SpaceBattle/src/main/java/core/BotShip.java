package core;


import utils.Vector2d;

/**
 * Created by simon lucas on 09/06/15.
 */

import static core.Constants.*;

public class BotShip extends Ship {

    public BotShip(GameState game, Vector2d s, Vector2d v, Vector2d d) {
        super(game, new Vector2d(rand.nextDouble() * width, rand.nextDouble() * height, true), v, d);
        d.rotate(rand.nextDouble() * 2 * Math.PI);
        // better to have constructors that capture all of the state
        action = new Action();
    }

    public BotShip copy() {
        return new BotShip(game, s, v, d);
    }

    public void update() {
        action.thrust = 0;
        action.turn = 0.1;
        action.shoot = Constants.rand.nextDouble() < 0.1;
        update(action);
    }

    public void hit() {
        // do nothing - we are invincible
    }

}
