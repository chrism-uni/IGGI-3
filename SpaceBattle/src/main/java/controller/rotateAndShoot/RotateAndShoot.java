package controller.rotateAndShoot;

import core.Action;
import core.GameState;
import controller.BattleController;
import core.NeuroShip;
import core.SimpleBattle;
import utils.ElapsedCpuTimer;

import java.awt.*;

/**
 * Created by simonlucas on 30/05/15.
 */
public class RotateAndShoot implements BattleController {

    NeuroShip ship;

    Action action;

    public RotateAndShoot() {
        action = new Action();
    }

    public Action action(GameState game) {
        // action.thrust = 2.0;
        action.shoot = true;
        action.turn = 1;

        return action;
    }

    public void setVehicle(NeuroShip ship) {
        // just in case the ship is needed ...
        this.ship = ship;
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId, ElapsedCpuTimer elapsedTimer) {
        return new Action(0, 1, true);
    }


    public void draw(Graphics2D g)
    {

    }
}
