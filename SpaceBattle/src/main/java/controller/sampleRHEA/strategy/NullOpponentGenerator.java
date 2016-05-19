package controller.sampleRHEA.strategy;

import controller.sampleRHEA.search.GAIndividual;
import controller.sampleRHEA.search.Search;

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
