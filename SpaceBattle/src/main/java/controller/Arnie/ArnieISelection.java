package controller.Arnie;

import controller.Arnie.ArnieGAIndividual;

/**
 * Created by dperez on 08/07/15.
 */
public interface ArnieISelection
{
    ArnieGAIndividual getParent(ArnieGAIndividual[] pop, ArnieGAIndividual first);
}
