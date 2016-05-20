package controller.funGAController.strategy;

import controller.funGAController.search.GAIndividual;

/**
 * Created by dperez on 08/07/15.
 */
public interface ISelection
{
    GAIndividual getParent(GAIndividual[] pop, GAIndividual[] parents);
}
