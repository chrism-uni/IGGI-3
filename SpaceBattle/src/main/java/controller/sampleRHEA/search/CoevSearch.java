package controller.sampleRHEA.search;

import core.Constants;
import core.SimpleBattle;
import core.ActionMap;
import controller.sampleRHEA.strategy.ICoevPairing;
import controller.sampleRHEA.strategy.ICrossover;
import controller.sampleRHEA.strategy.IMutation;
import controller.sampleRHEA.strategy.ISelection;

import utils.ElapsedCpuTimer;

import java.util.Random;

/**
 * PTSP-Competition
 * Random Search engine. Creates random paths and determines which one is the best to execute according to an heuristic.
 * It keeps looking during MACRO_ACTION_LENGTH time steps. After that point, the search is reset.
 * Created by Diego Perez, University of Essex.
 * Date: 17/10/12
 */
public class CoevSearch extends Search {

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

    public GAIndividual[] m_individualsOpp;

    public int numEvals;

    public int m_numGenerations;


    ICrossover cross;
    IMutation mut;
    ISelection sel;
    ICoevPairing pair;


    /**
     * Constructor of the random search engine.
     */
    public CoevSearch(ICrossover ic, IMutation im, ISelection is, ICoevPairing icp, Random rnd)
    {
        super(rnd);
        cross = ic;
        mut = im;
        sel = is;
        pair = icp;
        //System.out.println("num_action=" + this.NUM_ACTIONS_INDIVIDUAL+ " macro_action="+ this.MACRO_ACTION_LENGTH);
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
        m_individualsOpp = new GAIndividual[NUM_INDIVIDUALS];
        this.playerID = _playerID;

        for(int i = 0; i < NUM_INDIVIDUALS; ++i) {
            m_individuals[i] = new GAIndividual(Search.NUM_ACTIONS_INDIVIDUAL, playerID, this);
            m_individuals[i].randomize(m_rnd, ActionMap.ActionMap.length);

            m_individualsOpp[i] = new GAIndividual(Search.NUM_ACTIONS_INDIVIDUAL, -1, null);
            m_individualsOpp[i].randomize(m_rnd, ActionMap.ActionMap.length);
        }

        // check that we have at least enough time for initialisation           
        // indeed we need more than this                                        
        //if(NUM_EVALS >= NUM_INDIVIDUALS*pair.getGroupSize()) {
        //We need to evaluate once the opp population is complete.
        for(int i = 0; i < NUM_INDIVIDUALS; ++i)
        {
            pair.evaluate(gameState, m_individuals[i], m_individualsOpp);
            this.numEvals += pair.getGroupSize();
        }

        sortPopulationByFitness(m_individuals);
        sortPopulationByFitness(m_individualsOpp);

        //Resetting the random paths found and best fitness.
        m_bestRandomPath = new int[NUM_ACTIONS_INDIVIDUAL];
        m_currentRandomPath = new int[NUM_ACTIONS_INDIVIDUAL];
        for(int i=0; i<Search.NUM_ACTIONS_INDIVIDUAL;i++)
            m_currentRandomPath[i] = m_individuals[0].m_genome[i];
        m_bestRandomPath = m_currentRandomPath;
        m_bestFitnessFound = m_individuals[0].getFitness();
        //m_bestFitnessFound = -1;
        m_numGenerations = 0;
        //} else {
        //    throw new RuntimeException("The total evaluation number " + NUM_EVALS + " is less than the population size.");
        //}
    }

    /**
     * Runs the Random Search engine for one cycle.
     * @param _m_currentGameState Game state where the macro-action to be decided must be executed from.
     * @return  the action decided to be executed.
     */
    @Override
    public int run(SimpleBattle _m_currentGameState, ElapsedCpuTimer elapsedTimer)
    {
        m_currentGameState = _m_currentGameState;

        //check that we don't overspend
        long avgTimeTaken = 0;
        long acumTimeTaken = 0;
        int numIters = 0;
        
        long remainingLimit = 0;
        //long remaining = (long) (elapsedTimer.getMaxTime()/1000000.0);
        ElapsedCpuTimer testTimer = new ElapsedCpuTimer();
        testTimer.setMaxTime(elapsedTimer.getMaxTime());
        long remaining = testTimer.remainingTimeMillis();
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit)
        {
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();

            //OPPONENT POPULATION: prepare the next generation (no evaluation nor sorting yet!).
            GAIndividual[] nextOppPop = new GAIndividual[m_individualsOpp.length];
            int i;
            // Remain the best ELITISM individuals
            for(i = 0; i < ELITISM; ++i)  nextOppPop[i] = m_individualsOpp[i];

            for(;i<m_individualsOpp.length;++i)
            {
                nextOppPop[i] = breed(m_individualsOpp);
                mut.mutate(nextOppPop[i]);
            }
            // Replace the opponent's population
            m_individualsOpp = nextOppPop;

            //SOURCE POPULATION: create the next generation and evaluate against new individuals from
            // the opponent population. This also evaluates those.
            GAIndividual[] nextPop = new GAIndividual[m_individuals.length];

            // Remain the best ELITISM individuals
            for(i = 0; i < ELITISM; ++i) {
                nextPop[i] = m_individuals[i];
                pair.evaluate(m_currentGameState, nextPop[i], m_individualsOpp);
                this.numEvals += pair.getGroupSize();
            }
            // Mutate the other individuals
            for(;i<m_individuals.length;++i)
            {
                nextPop[i] = breed(m_individuals);
                mut.mutate(nextPop[i]);
                //System.out.println(i + " before score=" + nextPop[i].getFitness() + " opp score=" + m_individualsOpp[0].getFitness());
                pair.evaluate(m_currentGameState, nextPop[i], m_individualsOpp);
                //System.out.println(i + "after score=" + nextPop[i].getFitness() + " opp score=" + m_individualsOpp[0].getFitness());
                this.numEvals += pair.getGroupSize();
            }
            m_individuals = nextPop;

            //and sort both populations by fitness for the next iteration
            sortPopulationByFitness(m_individuals);
            sortPopulationByFitness(m_individualsOpp);

            //System.out.println("0 before score=" + m_individuals[0].getFitness() + " opp score=" + m_individualsOpp[0].getFitness());


            m_numGenerations++;
            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis());
            avgTimeTaken = acumTimeTaken/numIters;
            remaining = testTimer.remainingTimeMillis();

            /**
            switch(CONTROL_TYPE) {                                              
                case 0:                                                         
                    long current_time = System.nanoTime();                      
                    stop = ((current_time - start_time)/1e6 >= DURATION_PER_TICK);
                    break;                                                      
                case 1:                                                         
                    stop = (this.numEvals >= NUM_EVALS);                        
                    break;                                                      
                case 2:                                                         
                    stop = (m_numGenerations >= NUM_ITERS);                             
                    break;                                                      
                default:                                                        
                   throw new RuntimeException("Control parameter is not right.");
            }
            */
        }
        //System.out.println("COEV: numIters " + m_numGenerations + ", numEvals " + numEvals);


        //System.out.print(a_gameState.currentTick + " ");
        //m_individuals[0].print();

        return m_individuals[0].m_genome[0];
    }

    private GAIndividual breed(GAIndividual[] pop)
    {
        GAIndividual gai1 = sel.getParent(pop, null);        //First parent.
        GAIndividual gai2 = sel.getParent(pop, gai1);        //Second parent.
        return cross.uniformCross(gai1, gai2);
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

    /**
     * Prints a population, including fitness.
     */
    private void printPopulation(GAIndividual[] pop)
    {
        for(int i=0;i<m_individuals.length;++i)
        {
            pop[i].print();
        }
    }




}
