package controller.Arnie;

import controller.Arnie.ArnieGAIndividual;
import core.SimpleBattle;
import utils.ElapsedCpuTimer;

import java.util.Random;

/**
 * Created by dperez on 08/07/15.
 */
public abstract class ArnieSearch {

    /**
     * Number of macro-actions that form the random path.
     */
    public static int NUM_ACTIONS_INDIVIDUAL = 30; //8
    /**
     * Number of single actions that form a macro action.
     */
    public static int MACRO_ACTION_LENGTH = 2; //15
    /**
     * Total number of evaluaitons can be used
     */
    public static int NUM_EVALS = 1500;

    public static int NUM_ITERS = 500;

    /**
     * Control type:
     * 0, using real time control
     * 1, using total evaluation number
     * 2, using iteration number (not recommended)
     */
    public static int CONTROL_TYPE = 0;

    /**
     * ID of ths player.
     */
    public int playerID;
    /**
     * Best individual (route) found in the current search step.
     */
    public int[] m_bestRandomPath;
    /**
     * Best heuristic cost of the best individual
     */
    public double m_bestFitnessFound;
    /**
     * Next generated individual to be evaluated
     */
    public int[] m_currentRandomPath;
    /**
     * Random number generator
     */
    public Random m_rnd;


    /**
     * Hitmap, for drawing.
     */
    public int[][] hitMapOwn = null;
    public int[][] hitMapOpp = null;


    public ArnieSearch(Random rnd) {
        m_rnd = rnd;
    }

    abstract public void init(SimpleBattle gameState, int playerID);

    abstract public int run(SimpleBattle a_gameState, ElapsedCpuTimer elapsedTimer);

    abstract public double scoreGame(SimpleBattle game);


    private void printGenome(int []path)
    {
        for(int p : path)
        {
            System.out.print(p);
        }
        System.out.println();
    }


    protected void sortPopulationByFitness(ArnieGAIndividual[] population) {
        for (int i = 0; i < population.length; i++) {
            for (int j = i + 1; j < population.length; j++) {
                if (population[i].getFitness() < population[j].getFitness()) {
                    ArnieGAIndividual gcache = population[i];
                    population[i] = population[j];
                    population[j] = gcache;
                }
            }
        }
    }
}
