package controller.sampleRHEA.strategy;

import core.ActionMap;
import controller.sampleRHEA.search.GAIndividual;

import java.util.Random;

/**
 * Created by dperez on 08/07/15.
 */
public class PMutation implements IMutation
{
    public static double MUT_PROB = 0.1;
    Random m_rnd;

    public PMutation(Random rnd)
    {
        m_rnd = rnd;
    }

    public PMutation(Random rnd, double mutp)
    {
        MUT_PROB = mutp;
        m_rnd = rnd;
    }

    @Override
    public void mutate(GAIndividual individual) {

        for (int i = 0; i < individual.m_genome.length; i++) {
            if(m_rnd.nextDouble() < MUT_PROB)
            {
                double mutProba = m_rnd.nextDouble();
                if(mutProba < 3/7)  //mutate thrust
                    individual.m_genome[i] = ActionMap.mutateThrust(individual.m_genome[i]);
                else if (mutProba < 6/7)  //mutate steering
                    individual.m_genome[i] = ActionMap.mutateSteer(individual.m_genome[i], m_rnd.nextDouble()>0.5);
                else //mutate shooting
                    individual.m_genome[i] = ActionMap.mutateShooting(individual.m_genome[i]);
            }

        }
    }

}
