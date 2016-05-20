package controller.funGAController.strategy;

import controller.funGAController.strategy.*;
import controller.funGAController.strategy.ICoevPairing;
import core.SimpleBattle;
import controller.funGAController.search.GAIndividual;
import utils.StatSummary;
import controller.funGAController.search.Search;

import java.util.Random;

/**
 * Created by dperez on 08/07/15.
 */
public class RandomPairing extends ICoevPairing
{

    Random rnd;
    TournamentSelection ts;

    public RandomPairing(Random rnd)
    {
        this.rnd = rnd;
    }

    public RandomPairing(Random rnd, int groupSize)
    {
        this.GROUP_SIZE = groupSize;
        this.rnd = rnd;
    }


    @Override
    public double evaluate(SimpleBattle game, GAIndividual individual, GAIndividual[] otherPop) {

        assert GROUP_SIZE < otherPop.length; //we don't contemplate the whole population here.

        // ADDED
        /*for (int i = 0; i < otherPop.length; i++) {
            otherPop[i].evaluateRival(game, individual);
        }*/

        /*for (int i = 0; i < otherPop.length; i++) {
            for (int j = i + 1; j < otherPop.length; j++) {
                if (otherPop[i].getFitness() < otherPop[j].getFitness()) {
                    GAIndividual gcache = otherPop[i];
                    otherPop[i] = otherPop[j];
                    otherPop[j] = gcache;
                }
            }
        }*/



        //1. Select the subgroup of individuals
        int[] group = new int[GROUP_SIZE];
        for (int i = 0; i < GROUP_SIZE; ++i)
            group[i] = -1;

        int i = 0;
        while (group[GROUP_SIZE - 1] == -1) {
            int part = (int) (rnd.nextFloat() * otherPop.length);
            boolean valid = true;
            for (int k = 0; valid && k < i; ++k) {
                valid = (part != group[k]);                 //Check it is not in the group already.
            }

            if (valid) {
                group[i++] = part;
            }
        }

        //2. Go through them, evaluate and average the fitness
        StatSummary ss = new StatSummary();
        //groupSize = 1;
        for ( i = 0; i < GROUP_SIZE; ++i) {
            GAIndividual rival = otherPop[i];

            //rival = new GAIndividual(Search.NUM_ACTIONS_INDIVIDUAL, -1, null);

            double fit = individual.evaluateRival(game, rival);
            ss.add(fit);
            rival.accumFitness(-fit);
        }

        double fit = ss.mean();
        individual.setFitness(fit);
        return fit;
    }
}
