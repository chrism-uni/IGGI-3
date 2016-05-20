package controller.funGAController.strategy;

import controller.funGAController.search.GAIndividual;

import java.util.Random;

/**
 * Created by bdtrev on 20/05/2016.
 */
public class TripleParentCrossover implements ICrossover {

    Random rnd;

    public TripleParentCrossover(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public GAIndividual uniformCross(GAIndividual[] parents) {
        GAIndividual parentA, parentB, parentC;
        parentA = parents[0];
        parentB = parents[1];
        parentC = parents[2];

        int[] newInd = new int[parentA.m_genome.length];

        for(int i = 0; i < parentA.m_genome.length; ++i) {
            if (parentA.m_genome[i] != parentB.m_genome[i]) {
                newInd[i] = parentC.m_genome[i];
            } else {
                newInd[i] = parentA.m_genome[i];
            }
        }

        return new GAIndividual(newInd, parentA.playerID, parentA.search);
    }
}
