package controller.funGAController.strategy;

import controller.funGAController.search.GAIndividual;
import controller.funGAController.strategy.ICrossover;

import java.util.Random;

/**
 * Created by dperez on 08/07/15.
 */
public class UniformCrossover implements ICrossover {

    Random rnd;

    public UniformCrossover(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public GAIndividual uniformCross(GAIndividual[] parents) {
        GAIndividual parentA, parentB;
        parentA = parents[0];
        parentB = parents[1];

        int[] newInd = new int[parentA.m_genome.length];

        for(int i = 0; i < parentA.m_genome.length; ++i)
        {
            if(rnd.nextFloat() < 0.5f)
            {
                newInd[i] = parentA.m_genome[i];
            }else{
                newInd[i] = parentB.m_genome[i];
            }
        }

        return new GAIndividual(newInd, parentA.playerID, parentA.search);
    }
}
