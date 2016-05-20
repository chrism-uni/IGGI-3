package controller.funGAController.strategy;

import controller.funGAController.search.GAIndividual;
import controller.funGAController.search.Search;
import controller.funGAController.strategy.*;
import controller.funGAController.strategy.OpponentGenerator;

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
