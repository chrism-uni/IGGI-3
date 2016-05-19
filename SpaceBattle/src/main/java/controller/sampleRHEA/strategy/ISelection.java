package controller.sampleRHEA.strategy;

import controller.sampleRHEA.search.GAIndividual;

/**
 * Created by dperez on 08/07/15.
 */
public interface ISelection
{
    GAIndividual getParent(GAIndividual[] pop, GAIndividual first);
}
