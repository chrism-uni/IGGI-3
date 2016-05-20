package controller.pinkBomb;


import controller.BattleController;
import core.*;
import execution.BattleTest;
import utils.ElapsedCpuTimer;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

/**
 * PTSP-Competition
 * Sample controller based on macro actions and random search.
 * Created by Diego Perez, University of Essex.
 * Date: 17/10/12
 */
public class PinkBombController implements BattleController {

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

    int myId, max_lives;

    public static int shootFromFront = 1;
    boolean firstTry = true;


    /**
     * Constructor of the controller
     */
    public PinkBombController(Search searchAlg)
    {
        m_resetRS = true;
        m_search = searchAlg;
        m_currentMacroAction = 10;
        m_lastMacroAction = 0;

        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "config.properties";
            input = getClass().getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return;
            }

            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        max_lives = Integer.parseInt(prop.getProperty("LIFE"));
        //System.out.println(max_lives);
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
        if(firstTry) {

            SimpleBattle model = game.clone();
            NeuroShip thisShip = model.getShip(playerId);
            NeuroShip oppShip = model.getShip((playerId + 1) % 2);


            double score = model.getScore(playerId);
            //System.out.println("before: " + score);
            int c = 0;
            int beforeShootMissile = model.getMissilesLeft(playerId);

            //System.out.println(model.getObjects().size());
            double correctAngle = 0.0;
            //while(true) {
            model.advance(new Action(0, 1, true), new Action(0, 0, false));
            // System.out.println(model.getObjects().size());

            BattleMissile missile = (BattleMissile) model.getObjects().get(model.getObjects().size() - 1);

            //System.out.println(thisShip.s.angleBetween(missile.s)+" "+thisShip.s.angleBetween(oppShip.s));
            if (thisShip.d.dot(missile.v) > 0) //{
            {
                shootFromFront = 0;
            }
            //correctAngle = thisShip.s.angleBetween(oppShip.s);
            //      break;
            //  }
            //}
            firstTry = false;
        }
 //       System.out.println(shootFromFront);
/*
        while (beforeShootMissile == model.getMissilesLeft(playerId)) {
            model.advance(new Action(0, 0, true), new Action(0, 0, false));
            c++;
            score = model.getScore(playerId);
        }
        double diff = score - model.getScore(playerId);
        System.out.println("after: " + model.getScore(playerId) + "shot times: " + c);

        int count=1;
        while (!model.isGameOver()) {
            //thisShip.s.rotate(2*Math.PI - thisShip.s.angleBetween(oppShip.s));
            model.advance(new Action(1, 1, true), new Action(0, 0, false));
            if (model.getCooldown(playerId) == 0)
                count++;
            //System.out.println(model.getScore(playerId));
        }
        System.out.println("winner:" + model.getGameWinner());
        System.out.println("shoot times: " + count);
        double hit = (model.getScore(playerId) - count*diff)/max_lives;
        System.out.println("score at end: " + model.getScore(playerId));
        System.out.println("shoot score: " + diff);
        System.out.println("hit score: " + hit);

        */
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
            m_resetRS = true;
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
                m_resetRS = true;
                if(suggestedAction != -1)
                    m_lastMacroAction = suggestedAction;

                if(m_lastMacroAction != -1)
                    m_currentMacroAction = Search.MACRO_ACTION_LENGTH-1;

            }else{
                throw new RuntimeException("This should not be happening: " + m_currentMacroAction);
            }
        }

        System.out.println(playerId + ": " + nextMacroAction);
        return ActionMap.ActionMap[nextMacroAction];
//        return null;new Action(0,0,false);//
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
    //    return new Action(0,0,false);
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
