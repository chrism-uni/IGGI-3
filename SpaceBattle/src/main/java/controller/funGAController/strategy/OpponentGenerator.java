package controller.funGAController.strategy;

import controller.funGAController.search.GAIndividual;

/**
 * Created by dperez on 19/01/16.
 */
public interface OpponentGenerator
{
    public GAIndividual getOpponent(int numActions);

}
