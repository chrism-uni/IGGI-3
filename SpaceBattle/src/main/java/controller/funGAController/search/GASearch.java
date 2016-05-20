package controller.funGAController.search;

import controller.funGAController.search.*;
import controller.funGAController.search.Search;
import core.Constants;
import core.SimpleBattle;
import core.ActionMap;
import controller.funGAController.strategy.ICrossover;
import controller.funGAController.strategy.IMutation;
import controller.funGAController.strategy.ISelection;
import controller.funGAController.strategy.OpponentGenerator;
import utils.ElapsedCpuTimer;

import java.util.Random;

/**
 * PTSP-Competition
 * Random Search engine. Creates random paths and determines which one is the best to execute according to an heuristic.
 * It keeps looking during MACRO_ACTION_LENGTH time steps. After that point, the search is reset.
 * Created by Diego Perez, University of Essex.
 * Date: 17/10/12
 */
public class GASearch extends Search {

    /**
     * Current game state
     */
    public static SimpleBattle m_currentGameState;

    /**
     * Game state used to roll actions and evaluate  the current path.
     */
    public static SimpleBattle m_futureGameState;


    public static int ELITISM = 2;
    public static int NUM_INDIVIDUALS = 10;

    public GAIndividual[] m_individuals;
    controller.funGAController.strategy.ICrossover cross;
    controller.funGAController.strategy.IMutation mut;
    controller.funGAController.strategy.ISelection sel;
    public int m_numGenerations;
    public int numEvals;

    controller.funGAController.strategy.OpponentGenerator opponentGen;

    /**
     * Constructor of the random search engine.
     */
    public GASearch(controller.funGAController.strategy.ICrossover ic, controller.funGAController.strategy.IMutation im, controller.funGAController.strategy.ISelection is, controller.funGAController.strategy.OpponentGenerator opponentGen, Random rnd)
    {
        super(rnd);
        cross = ic;
        mut = im;
        sel = is;
        this.opponentGen = opponentGen;
    }

    /**
     * Initializes the random search engine. This function is also called to reset it.
     */
    @Override
    public void init(SimpleBattle gameState, int _playerID)
    {
        hitMapOwn = new int[Constants.width][Constants.height];
        hitMapOpp = new int[Constants.width][Constants.height];

        this.numEvals = 0;
        m_individuals = new GAIndividual[NUM_INDIVIDUALS];
        this.playerID = _playerID;
        GAIndividual opponent = opponentGen.getOpponent(Search.NUM_ACTIONS_INDIVIDUAL);

        // check that we have at least enough time for initialisation
        // indeed we need more than this
        if(NUM_EVALS>=NUM_INDIVIDUALS) {
            for(int i = 0; i < NUM_INDIVIDUALS; ++i)
            {
                m_individuals[i] = new GAIndividual(Search.NUM_ACTIONS_INDIVIDUAL, playerID, this);
                m_individuals[i].randomize(m_rnd, ActionMap.ActionMap.length );
                m_individuals[i].evaluate(gameState, opponent);
                this.numEvals++;
            }
        } else {
            throw new RuntimeException("The total evaluation number " + NUM_EVALS + " is less than the population size.");
        }

        // sort the population by fitness decreasing order
        sortPopulationByFitness(m_individuals);

        //Resetting the random paths found and best fitness.
        m_bestRandomPath = new int[NUM_ACTIONS_INDIVIDUAL];
        m_currentRandomPath = new int[NUM_ACTIONS_INDIVIDUAL];
        for(int i=0; i< Search.NUM_ACTIONS_INDIVIDUAL;i++)
            m_currentRandomPath[i] = m_individuals[0].m_genome[i];
        m_bestRandomPath = m_currentRandomPath;
        m_bestFitnessFound = m_individuals[0].getFitness();
        //m_bestFitnessFound = -1;
        m_numGenerations = 0;
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
        long avgTimeTaken = 0;
        long acumTimeTaken = 0;
        int numIters = 0;
        
        long remainingLimit = 0;
        //long remaining = (long) (elapsedTimer.getMaxTime()/1000000.0);
        ElapsedCpuTimer testTimer = new ElapsedCpuTimer();
        testTimer.setMaxTime(elapsedTimer.getMaxTime());
        long remaining = testTimer.remainingTimeMillis();
        
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit) {
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            
            GAIndividual[] nextPop = new GAIndividual[m_individuals.length];
            
            GAIndividual opponent = opponentGen.getOpponent(Search.NUM_ACTIONS_INDIVIDUAL);
            int i;
            for(i = 0; i < ELITISM; ++i)
            {
                nextPop[i] = m_individuals[i];
                nextPop[i].evaluate(a_gameState, opponent);
            }
            for(;i<m_individuals.length;++i)
            {
                nextPop[i] = breed();
                mut.mutate(nextPop[i]);
                nextPop[i].evaluate(a_gameState, opponent);
                this.numEvals++;
            }
            
            m_individuals = nextPop;
            sortPopulationByFitness(m_individuals);
            
            /*for(i = 0; i < m_individuals.length; ++i)
             System.out.format("individual i: " + i + ", fitness: %.3f, actions: %s \n", m_individuals[i].m_fitness, m_individuals[i].toString());     */
            
            
            m_numGenerations++;
            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis());
            avgTimeTaken = acumTimeTaken/numIters;
            //remaining = elapsedTimer.remainingTimeMillis();
            remaining = testTimer.remainingTimeMillis();
        }
        //System.out.println("GA: numIters " + numIters + ", numEvals " + this.numEvals);
        return m_individuals[0].m_genome[0];
    }

    private GAIndividual breed()
    {
        GAIndividual gai1 = sel.getParent(m_individuals, new GAIndividual[]{null, null});        //First parent.
        GAIndividual gai2 = sel.getParent(m_individuals,  new GAIndividual[]{gai1, null});        //Second parent.
        return cross.uniformCross( new GAIndividual[]{gai1, gai2});
    }

    /**
     * Provides an heuristic score of the game state m_futureGameState.
     * @return the score.
     */
    @Override
    public double scoreGame(SimpleBattle game)
    {
        return game.score(playerID); //game.getPoints(playerId);
    }



}
