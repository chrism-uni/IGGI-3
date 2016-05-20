package controller.Arnie;

import controller.Arnie.ArnieGAIndividual;
import core.SimpleBattle;

/**
 * Created by dperez on 08/07/15.
 */
public abstract class ArnieICoevPairing
{
    public static int GROUP_SIZE = 3;

    public abstract double evaluate(SimpleBattle game, ArnieGAIndividual individual, ArnieGAIndividual[] otherPop);

    public int getGroupSize() {
        return GROUP_SIZE;
    }

}
