package controller.funGAController.search;

import controller.funGAController.search.Search;
import core.SimpleBattle;
import core.ActionMap;
import utils.ElapsedCpuTimer;
import java.util.Random;

/**
 * PTSP-Competition
 * Random Search engine. Creates random paths and determines which one is the best to execute according to an heuristic.
 * It keeps looking during MACRO_ACTION_LENGTH time steps. After that point, the search is reset.
 * Created by Diego Perez, University of Essex.
 * Date: 17/10/12
 */
public class RandomSearch extends Search {

    /**
     * Current game state
     */
    public static SimpleBattle m_currentGameState;

    /**
     * Game state used to roll actions and evaluate  the current path.
     */
    public static SimpleBattle m_futureGameState;


    /** NOW, SOME PARAMETERS **/


    /**
     * Constructor of the random search engine.
     */
    public RandomSearch(Random rnd)
    {
        super(rnd);
    }

    /**
     * Initializes the random search engine. This function is also called to reset it.
     */
    @Override
    public void init(SimpleBattle game, int playerId)
    {
        //Resetting the random paths found and best fitness.
        m_bestRandomPath = new int[NUM_ACTIONS_INDIVIDUAL];
        m_currentRandomPath = new int[NUM_ACTIONS_INDIVIDUAL];
        m_bestFitnessFound = -1;
        this.playerID = playerId;
    }

    /**
     * Runs the Random Search engine for one cycle.
     * @param a_gameState Game state where the macro-action to be decided must be executed from.
     * @return  the action decided to be executed.
     */
    @Override
    public int run(SimpleBattle a_gameState, ElapsedCpuTimer elapsedTimer)
    {
        m_currentGameState = a_gameState;
        int numIters = 0;

        //check that we don't overspend
        while(numIters < 100)
        {
            //create and evaluate a new random path.
            double randomPathFitness = createRandomPath();
            //keep the best one.
            if(randomPathFitness > m_bestFitnessFound)
            {
                m_bestFitnessFound = randomPathFitness;
                System.arraycopy(m_currentRandomPath,0, m_bestRandomPath,0,NUM_ACTIONS_INDIVIDUAL);
            }
            //update remaining time.
            numIters++;
        }

        //take the best one so far, the best macroaction is the first one of the path.
        return m_bestRandomPath[0];
    }

    /**
     * Creates a new random path in m_currentRandomPath, watching for the limit time.
     * @return score of the new path.
     */
    private double createRandomPath()
    {
        m_futureGameState = m_currentGameState.clone();

        //Create and evaluate the path
        for(int i = 0; i < m_currentRandomPath.length; ++i)
        {
            //Next macro action:
            m_currentRandomPath[i] = m_rnd.nextInt(ActionMap.ActionMap.length);


            //Rollout macro-action in the game
            for(int j =0; j < Search.MACRO_ACTION_LENGTH; ++j)
            {
                if(playerID == 0)
                    m_futureGameState.advance(ActionMap.ActionMap[m_currentRandomPath[i]], ActionMap.ActionMap[0]);
                else
                    m_futureGameState.advance(ActionMap.ActionMap[0], ActionMap.ActionMap[m_currentRandomPath[i]]);
            }
        }
        //printGenome(m_currentRandomPath);

        //At the end of the random path, return evaluation of the reached state.
        return scoreGame(m_futureGameState);
    }



    /**
     * Provides an heuristic score of the game state m_futureGameState.
     * @return the score.
     */
    @Override
    public double scoreGame(SimpleBattle game)
    {
        return game.score(playerID); //game.getPoints(playerID);
    }



}
