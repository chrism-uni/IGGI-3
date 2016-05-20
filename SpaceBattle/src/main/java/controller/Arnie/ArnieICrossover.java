package controller.Arnie;

import controller.Arnie.ArnieGAIndividual;

/**
 * Created by dperez on 08/07/15.
 */
public interface ArnieICrossover
{
    ArnieGAIndividual uniformCross(ArnieGAIndividual parentA, ArnieGAIndividual parentB);
}
