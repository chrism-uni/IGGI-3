package controller.Arnie;

import controller.Arnie.ArnieGAIndividual;
import controller.Arnie.ArnieISelection;

import java.util.Random;

/**
 * Created by dperez on 08/07/15.
 */
public class ArnieTournamentSelection implements ArnieISelection
{
    public static int TOURNAMENT_SIZE = 3;
    Random rnd;

    public ArnieTournamentSelection(Random rnd, int tSize)
    {
        TOURNAMENT_SIZE = tSize;
        this.rnd = rnd;
    }

    public ArnieTournamentSelection(Random rnd)
    {
        this.rnd = rnd;
    }

    public ArnieGAIndividual getParent(ArnieGAIndividual[] pop, ArnieGAIndividual first) {
        ArnieGAIndividual best = null;
        int[] tour = new int[TOURNAMENT_SIZE];
        for (int i = 0; i < TOURNAMENT_SIZE; ++i)
            tour[i] = -1;

        int i = 0;
        while (tour[TOURNAMENT_SIZE - 1] == -1) {
            int part = (int) (rnd.nextFloat() * pop.length);
            boolean valid = pop[part] != first;  //Check it is not the same selected first.
            for (int k = 0; valid && k < i; ++k) {
                valid = (part != tour[k]);                 //Check it is not in the tournament already.
            }

            if (valid) {
                tour[i++] = part;
                if (best == null || (pop[part].getFitness() > best.getFitness()))
                    best = pop[part];
            }
        }

        return best;
    }

}
