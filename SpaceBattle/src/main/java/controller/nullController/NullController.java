package controller.nullController;

import core.Action;
import controller.BattleController;
import core.SimpleBattle;
import utils.ElapsedCpuTimer;

import java.awt.*;

/**
 * Created by jwalto on 12/06/2015.
 */
public class NullController implements BattleController {

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId, ElapsedCpuTimer elapsedTimer) {
        return new Action(0, 0, false);
    }

    public void draw(Graphics2D g)
    {

    }
}
