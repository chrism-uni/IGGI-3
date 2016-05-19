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
public class WASDController implements BattleController, KeyListener {

    public static Action[] ActionMap = new Action[]{
            new Action(0.0,0.0,false, false),
            new Action(0.0,-1.0,false, false),
            new Action(0.0,1.0,false, false),
            new Action(1.0,0.0,false, false),
            new Action(1.0,-1.0,false, false),
            new Action(1.0,1.0,false, false),
            new Action(0.0,0.0,true, false),
            new Action (0.0, 0.0, false, true)

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
    private boolean shoot;


    public WASDController()
    {
        m_turn = 0;
        m_thrust = false;
        shoot = false;
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

        if(shoot) return ActionMap[6];
        return ActionMap[0];
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                m_thrust = true;
                break;

            case KeyEvent.VK_A:
                m_turn = -1;
                break;

            case KeyEvent.VK_D:
                m_turn = 1;
                break;

            case KeyEvent.VK_SPACE:
                shoot = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) {
            m_thrust = false;
        }
        if (key == KeyEvent.VK_A) {
            m_turn = 0;
        }
        if (key == KeyEvent.VK_D) {
            m_turn = 0;
        }
        if(key == KeyEvent.VK_SPACE){
            shoot = false;
        }
    }


    public void draw(Graphics2D g)
    {

    }
}
