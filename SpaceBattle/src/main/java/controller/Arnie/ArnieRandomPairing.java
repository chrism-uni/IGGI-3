package controller.Arnie;

import controller.Arnie.ArnieGAIndividual;
import controller.Arnie.ArnieICoevPairing;
import controller.sampleRHEA.strategy.TournamentSelection;
import core.SimpleBattle;
import utils.StatSummary;

import java.util.Random;

/**
 * Created by dperez on 08/07/15.
 */
public class ArnieRandomPairing extends ArnieICoevPairing
{

    Random rnd;
    TournamentSelection ts;

    public ArnieRandomPairing(Random rnd)
    {
        this.rnd = rnd;
    }

    public ArnieRandomPairing(Random rnd, int groupSize)
    {
        this.GROUP_SIZE = groupSize;
        this.rnd = rnd;
    }


    @Override
    public double evaluate(SimpleBattle game, ArnieGAIndividual individual, ArnieGAIndividual[] otherPop) {

        assert GROUP_SIZE < otherPop.length; //we don't contemplate the whole population here.

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
        for (i = 0; i < GROUP_SIZE; ++i) {
            ArnieGAIndividual rival = otherPop[group[i]];

            //rival = new GAIndividual(Search.NUM_ACTIONS_INDIVIDUAL, -1, null);

            double fit = individual.evaluate(game, rival);
            ss.add(fit);
            rival.accumFitness(-fit);
        }

        double fit = ss.mean();
        individual.setFitness(fit);
        return fit;
    }
}
