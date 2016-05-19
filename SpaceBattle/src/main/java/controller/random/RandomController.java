package controller.random;

import core.Action;
import controller.BattleController;
import core.SimpleBattle;
import core.ActionMap;
import utils.ElapsedCpuTimer;

import java.awt.*;
import java.util.Random;

/**
 * Created by jwalto on 12/06/2015.
 */
public class RandomController implements BattleController {

    Random m_rnd;

    public RandomController(Random rnd)
    {
        m_rnd = rnd;
    }



    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId, ElapsedCpuTimer elapsedTimer) {
        int action = m_rnd.nextInt(ActionMap.ActionMap.length);
        return ActionMap.ActionMap[action];
    }


    public void draw(Graphics2D g)
    {

    }
}
