package controller;

import core.Action;
import core.SimpleBattle;
import utils.ElapsedCpuTimer;

import java.awt.*;

/**
 * Created by simon lucas on 10/06/15.
 */

public interface BattleController {

    Action getAction(SimpleBattle gameStateCopy, int playerId, ElapsedCpuTimer elapsedTimer);

    void draw(Graphics2D g);
}