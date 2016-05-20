package controller.CurlyStraight.sampleRHEA.strategy;

import controller.CurlyStraight.sampleRHEA.search.GAIndividual;

/**
 * Created by dperez on 19/01/16.
 */
public class NullOpponentGenerator implements OpponentGenerator {

    GAIndividual nullOpponent;

    public NullOpponentGenerator(int numActions)
    {
        nullOpponent = new GAIndividual(numActions, -1, null);
    }


    @Override
    public GAIndividual getOpponent(int numActions) {
        return nullOpponent;
    }
}
