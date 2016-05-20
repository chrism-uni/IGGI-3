package controller.CurlyStraight.sampleRHEA.strategy;

import controller.CurlyStraight.sampleRHEA.search.GAIndividual;
import core.SimpleBattle;

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
