package controller.CurlyStraight.sampleRHEA.strategy;

import controller.CurlyStraight.sampleRHEA.search.GAIndividual;

/**
 * Created by dperez on 19/01/16.
 */
public interface OpponentGenerator
{
    public GAIndividual getOpponent(int numActions);

}
