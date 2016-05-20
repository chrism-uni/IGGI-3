package controller.CurlyStraight.sampleRHEA.strategy;

import controller.CurlyStraight.sampleRHEA.search.GAIndividual;

/**
 * Created by dperez on 08/07/15.
 */
public interface ICrossover
{
    GAIndividual uniformCross(GAIndividual parentA, GAIndividual parentB);
}
