package controller.funGAController;


import controller.BattleController;
import controller.funGAController.search.Search;
import core.Action;
import core.ActionMap;
import core.SimpleBattle;
import execution.BattleTest;
import utils.ElapsedCpuTimer;

import java.awt.*;
import java.util.Random;

/**
 * PTSP-Competition
 * Sample controller based on macro actions and random search.
 * Created by Diego Perez, University of Essex.
 * Date: 17/10/12
 */
public class BattleEvoController implements BattleController {

    /**
     *   Current action in the macro action being execut
     */
    private int m_currentMacroAction;

    /**
     * Random Search engine to find the optimal macro-action to execute.
     */
    private Search m_search;

    /**
     * Flag that indicates if the RS engine must be restarted (a new action has been decided).
     */
    boolean m_resetRS;

    /**
     *  Last macro action to be executed.
     */
    private int m_lastMacroAction;

    public static long DURATION_PER_TICK = 10;

    int myId;

    /**
     * Constructor of the controller
     */
    public BattleEvoController(Search searchAlg)
    {
        m_resetRS = true;
        m_search = searchAlg;
        m_currentMacroAction = 10;
        m_lastMacroAction = 0;
    }

    /**
     * Returns an action to execute in the game.
     * @param game A copy of the current game
     * @param playerId the player is playing
     * @return
     */
    @Override
    public Action getAction(SimpleBattle game, int playerId, ElapsedCpuTimer elapsedTimer)
    {
        this.myId=playerId;
        m_search.playerID = playerId;
        if(Search.MACRO_ACTION_LENGTH == 1)
            return getSingleAction(game, playerId, elapsedTimer);

        int cycle = game.getTicks();
        int nextMacroAction;

        if(cycle == 0)
        {
            //First cycle of a match is special, we need to execute any action to start looking for the next one.
            m_lastMacroAction = 0;
            nextMacroAction = m_lastMacroAction;
            //m_resetRS = true;
            m_currentMacroAction = Search.MACRO_ACTION_LENGTH-1;
        }else
        {
            //advance the game until the last action of the macro action
            prepareGameCopy(game);
            if(m_currentMacroAction > 0)
            {
                if(m_resetRS)
                {
                    //search needs to be restarted.
                    m_search.init(game, playerId);
                }
                //keep searching, but it is not time to retrieve the best action found
                m_search.run(game, elapsedTimer);

                //we keep executing the same action decided in the past.
                nextMacroAction = m_lastMacroAction;
                m_currentMacroAction--;
                m_resetRS = false;
            }else if(m_currentMacroAction == 0)
            {
                nextMacroAction = m_lastMacroAction; //default value
                //keep searching and retrieve the action suggested by the random search engine.
                int suggestedAction = m_search.run(game, elapsedTimer);
                //now it's time to execute this action. Also, in next cycle, we need to reset the search
                //m_resetRS = true;
                if(suggestedAction != -1)
                    m_lastMacroAction = suggestedAction;

                if(m_lastMacroAction != -1)
                    m_currentMacroAction = Search.MACRO_ACTION_LENGTH-1;

            }else{
                throw new RuntimeException("This should not be happening: " + m_currentMacroAction);
            }
        }

        //System.out.println(playerId + ": " + nextMacroAction);
        return ActionMap.ActionMap[nextMacroAction];
    }


    /**
     * Updates the game state using the macro-action that is being executed. It rolls the game up to the point in the
     * future where the current macro-action is finished.
     * @param game  State of the game.
     */
    public void prepareGameCopy(SimpleBattle game)
    {
        //If there is a macro action being executed now.
        if(m_lastMacroAction != -1)
        {
            //Find out how long have we executed this macro-action
            int first = Search.MACRO_ACTION_LENGTH - m_currentMacroAction - 1;
            for(int i = first; i < Search.MACRO_ACTION_LENGTH; ++i)
            {
                Random randomGenerator = new Random();
                if(m_search.playerID == 0)
                    game.advance(ActionMap.ActionMap[m_lastMacroAction], ActionMap.ActionMap[randomGenerator.nextInt(ActionMap.ActionMap.length)]);
                else
                    game.advance(ActionMap.ActionMap[randomGenerator.nextInt(ActionMap.ActionMap.length)], ActionMap.ActionMap[m_lastMacroAction]);
                //make the moves to advance the game state.
                /**
                 if(m_search.playerID == 0)
                 game.update(ActionMap.ActionMap[m_lastMacroAction], ActionMap.ActionMap[0]);
                 else
                 game.update(ActionMap.ActionMap[0], ActionMap.ActionMap[m_lastMacroAction]);
                 **/
            }
        }
    }

    public Action getSingleAction(SimpleBattle game, int playerId, ElapsedCpuTimer elapsedTimer)
    {
        m_search.init(game, playerId);
        int suggestedAction = m_search.run(game, elapsedTimer);
        return ActionMap.ActionMap[suggestedAction];
    }


    /**
     * We are boring and we don't paint anything here.
     * @param a_gr Graphics device to paint.
     */
    public void paint(Graphics2D a_gr) {}


    public void draw(Graphics2D g)
    {
        if(BattleTest.SHOW_ROLLOUTS) {

            if (m_search.hitMapOwn != null) {

                for (int i = 0; i < m_search.hitMapOwn.length; ++i) {
                    for (int j = 0; j < m_search.hitMapOwn[i].length; ++j) {


                        g.setColor( myId == 0 ? Color.GREEN : Color.BLUE);
                        if (m_search.hitMapOwn[i][j] > 0) {
                            //System.out.println(i + " " + j + ":" + m_search.hitMapOwn[i][j]);
                            g.fillOval(i, j, 1, 1);
                        }


                        g.setColor(Color.WHITE);
                        if (m_search.hitMapOpp[i][j] > 0) {
                            //System.out.println(i + " " + j + ":" + m_search.hitMapOwn[i][j]);
                            g.fillOval(i, j, 1, 1);
                        }


                    }

                }
            }



        }
    }
}
