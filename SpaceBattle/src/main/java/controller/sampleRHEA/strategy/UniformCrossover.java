package controller.sampleRHEA.strategy;

import controller.sampleRHEA.search.GAIndividual;

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
    public GAIndividual uniformCross(GAIndividual parentA, GAIndividual parentB) {

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
