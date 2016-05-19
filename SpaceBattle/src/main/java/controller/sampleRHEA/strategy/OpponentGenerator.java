package controller.sampleRHEA.strategy;

import controller.sampleRHEA.search.GAIndividual;

/**
 * Created by dperez on 19/01/16.
 */
public interface OpponentGenerator
{
    public GAIndividual getOpponent(int numActions);

}
