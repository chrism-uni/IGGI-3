package controller.human;

import core.Action;
import controller.BattleController;
import core.SimpleBattle;
import utils.ElapsedCpuTimer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by jwalto on 12/06/2015.
 */
public class ArrowsController implements BattleController, KeyListener {

    public static Action[] ActionMap = new Action[]{
            new Action(0.0,0.0,false, false),
            new Action(0.0,-1.0,false, false),
            new Action(0.0,1.0,false, false),
            new Action(1.0,0.0,false, false),
            new Action(1.0,-1.0,false, false),
            new Action(1.0,1.0,false, false),
            new Action(0.0,0.0,true, false),
            new Action(0.0,0.0,false, true)
    };

    Action curAction = ActionMap[0];

    /**
     * Indicates if the thrust is pressed.
     */
    private boolean m_thrust;

    /**
     * Indicates if the turn must be applied.
     */
    private int m_turn;

    /**
     * USE action
     */
    private boolean m_use;
    private boolean m_mine;

    public ArrowsController()
    {
        m_turn = 0;
        m_thrust = false;
        m_use = false;
        m_mine = false;
    }

    @Override
    public Action getAction(SimpleBattle gameStateCopy, int playerId, ElapsedCpuTimer elapsedTimer) {

        if (curAction == null)
            curAction = ActionMap[0];
        else
            curAction = getCurrentAction();

        return curAction;
    }

    private Action getCurrentAction()
    {
        if(m_use)
            return ActionMap[6];
        if(m_mine)
            return ActionMap[7];
        //Thrust actions.
        if(m_thrust)
        {
            if(m_turn == -1) return ActionMap[4];
            if(m_turn == 1) return ActionMap[5];
            return ActionMap[3];
        }

        //No thrust actions.
        if(m_turn == -1) return ActionMap[1];
        if(m_turn == 1) return ActionMap[2];
        return ActionMap[0];
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                m_thrust = true;
                break;

            case KeyEvent.VK_LEFT:
                m_turn = -1;
                break;

            case KeyEvent.VK_RIGHT:
                m_turn = 1;
                break;

            case KeyEvent.VK_SPACE:
                m_use = true;
                break;
            case KeyEvent.VK_H:
                m_mine = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
            m_thrust = false;
        }
        if (key == KeyEvent.VK_LEFT) {
            m_turn = 0;
        }
        if (key == KeyEvent.VK_RIGHT) {
            m_turn = 0;
        }
        if (key == KeyEvent.VK_SPACE) {
            m_use = false;
        }
        if (key == KeyEvent.VK_H){
            m_mine = false;
        }

    }

    public void draw(Graphics2D g)
    {

    }
}
