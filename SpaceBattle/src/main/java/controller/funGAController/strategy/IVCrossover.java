package controller.funGAController.strategy;

import controller.funGAController.search.GAIndividual;

import java.util.Random;

/**
 * Created by bdtrev on 19/05/2016.
 */
public class IVCrossover implements ICrossover {
    Random rnd;

    public IVCrossover(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public GAIndividual uniformCross(GAIndividual[] parents) {

        GAIndividual parentA, parentB;
        parentA = parents[0];
        parentB = parents[1];

        int[] newInd = new int[parentA.m_genome.length];

        int randomInt = rnd.nextInt(parentA.m_genome.length);

        for(int i = 0; i < parentA.m_genome.length; ++i) {
            if (randomInt == i) {
                    if (rnd.nextFloat() < 0.5f) {
                        newInd[i] = parentA.m_genome[rnd.nextInt(parentA.m_genome.length)];
                    } else {
                        newInd[i] = parentA.m_genome[rnd.nextInt(parentB.m_genome.length)];
                    }
            } else {
                if (parentA.m_genome[i] != parentB.m_genome[i]) {
                    if (rnd.nextFloat() < 0.5f) {
                        newInd[i] = parentA.m_genome[i];
                    } else {
                        newInd[i] = parentB.m_genome[i];
                    }
                } else {
                    newInd[i] = parentA.m_genome[i];
                }
            }
        }


        return new GAIndividual(newInd, parentA.playerID, parentA.search);
    }
}