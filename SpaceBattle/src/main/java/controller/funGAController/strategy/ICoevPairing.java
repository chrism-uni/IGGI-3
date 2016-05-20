package controller.funGAController.strategy;

import core.SimpleBattle;
import controller.funGAController.search.GAIndividual;

/**
 * Created by dperez on 08/07/15.
 */
public abstract class ICoevPairing
{
    public static int GROUP_SIZE = 3;

    public abstract double evaluate(SimpleBattle game, GAIndividual individual, GAIndividual[] otherPop);

    public int getGroupSize() {
        return GROUP_SIZE;
    }

}
