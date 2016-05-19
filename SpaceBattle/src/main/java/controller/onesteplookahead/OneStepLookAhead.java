
package controller.onesteplookahead;

import core.SimpleBattle;
import core.ActionMap;
import utils.Matrix;
import controller.BattleController;
import core.Action;
import utils.ElapsedCpuTimer;

import java.awt.*;

/**
 * Author: Jialin Liu, University of Essex                                    
 * Date: 01/04/2016 
 */
public class OneStepLookAhead implements BattleController {
    public Matrix my_fitness;
    
    public Matrix opponent_fitness;
   
    public Matrix scores;

    public int playerID;

    public static final int RECOMMEND_POLICY = 0;
    /**
     * Constructor of the engine.
     */
    public OneStepLookAhead()
    {
        my_fitness = new Matrix(ActionMap.ActionMap.length);
        opponent_fitness = new Matrix(ActionMap.ActionMap.length);
        scores = new Matrix(ActionMap.ActionMap.length);
    }

    /**
     * Initializes the engine.
     * This function is also called to reset it.
     */
    public void init(SimpleBattle gameState, int playerId)
    {
        this.playerID = playerId;
    }
    
    @Override
    public Action getAction(SimpleBattle gameState, int playerId, ElapsedCpuTimer elapsedTimer)
    {
        int best_action = -1;
        //SimpleBattle thisGameCopy = gameState.clone();
        for(int i=0; i<ActionMap.ActionMap.length; ++i)
        {
            for(int j=0; j<ActionMap.ActionMap.length; ++j)
            {
                SimpleBattle thisGameCopy = gameState.clone();
                thisGameCopy.advance(ActionMap.ActionMap[i], ActionMap.ActionMap[j]);
                my_fitness.fill(i,j,thisGameCopy.getScore(playerId));
                opponent_fitness.fill(i,j,thisGameCopy.getScore(1-playerId));
                scores.fill(i,j,thisGameCopy.score(1-playerId)); 
            }
        }
        best_action = recommend();
        //System.out.println("action : " + best_action);
        return ActionMap.ActionMap[best_action];
   }
   
    public int recommend()
    {
        int best_action = -1;
        Matrix diff = new Matrix(ActionMap.ActionMap.length);
        switch(RECOMMEND_POLICY) {
            case 0:
                best_action = my_fitness.MaxSum();
                break;
            case 1:
                best_action = opponent_fitness.MinSum();
                break;
            case 10:
                best_action = my_fitness.MaxMin();
                break;
            case 11:
                best_action = opponent_fitness.MinMax();
                break;
            case 100:
                best_action = scores.MaxSum();
                break;
            case 110:
                best_action = scores.MaxMin();
                break;
            case 1000:
                throw new RuntimeException("The nash policy is not defined yet.");
            default:
                throw new RuntimeException("Recommendation policy not defined.");
       }
        return best_action;
    }

    public void draw(Graphics2D g)
    {

    }

}
