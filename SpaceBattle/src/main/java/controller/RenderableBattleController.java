package controller;

import core.NeuroShip;
import java.awt.*;

/**
 * Created by jwalto on 12/06/2015.
 */
public interface RenderableBattleController extends BattleController {

    public void render(Graphics2D g, NeuroShip s);
}
