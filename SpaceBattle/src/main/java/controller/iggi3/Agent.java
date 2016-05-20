package controller.iggi3;

import core.Action;
import core.ActionMap;
import core.SimpleBattle;
import utils.ElapsedCpuTimer;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 * 
 * ATTENTION: This file is edited to adapt to BattleGame.
 * Edited by Jialin Liu, University of Essex
 * Date: 01/04/2016
 */
public class Agent extends AbstractPlayer {

    public static int NUM_ACTIONS = ActionMap.ActionMap.length;
    public static int MCTS_ITERATIONS = 100;
    public static int ROLLOUT_DEPTH = 10;
    public static double K = Math.sqrt(2);
    public static double REWARD_DISCOUNT = 1.00;
    public static Action[] actions;

    protected SingleMCTSPlayer mctsPlayer;

    /**
     * Public constructor with state observation and time due.
     * @param gameState state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(SimpleBattle gameState, ElapsedCpuTimer elapsedTimer)
    {
        //Get the actions in a static array.
        actions = new Action[NUM_ACTIONS];
        for(int i = 0; i < actions.length; i++)
        {
            actions[i] = ActionMap.ActionMap[i];
        }
        //Create the player.
        mctsPlayer = getPlayer(gameState, elapsedTimer);
    }

    public SingleMCTSPlayer getPlayer(SimpleBattle gameState, ElapsedCpuTimer elapsedTimer) {
        return new SingleMCTSPlayer(new Random());
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param gameState Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Action act(SimpleBattle gameState, ElapsedCpuTimer elapsedTimer) {

        //Set the state observation object as the new root of the tree.
        mctsPlayer.init(gameState, 0);

        //Determine the action using MCTS...
        int action = mctsPlayer.run(elapsedTimer);

        //... and return it.
        return actions[action];
    }

}
