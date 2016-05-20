package controller.Arnie;

import controller.Arnie.ArnieGAIndividual;
import controller.Arnie.ArnieICrossover;

import java.util.Random;

/**
 * Created by dperez on 08/07/15.
 */
public class ArnieUniformCrossover implements ArnieICrossover {

    Random rnd;

    public ArnieUniformCrossover(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public ArnieGAIndividual uniformCross(ArnieGAIndividual parentA, ArnieGAIndividual parentB) {

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

        return new ArnieGAIndividual(newInd, parentA.playerID, parentA.search);
    }
}
