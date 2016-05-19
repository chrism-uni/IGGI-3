package controller.sampleOLMCTS;

import java.util.Random;
import core.SimpleBattle;
import utils.ElapsedCpuTimer;
import utils.Util;
import core.ActionMap;

import core.Action;
/**
 * ATTENTION: This file is edited to adapt to BattleGame.                       
 * Edited by Jialin Liu, University of Essex                                    
 * Date: 01/04/2016
 */
public class SingleTreeNodeDetOpp
{
    private static final double HUGE_NEGATIVE = -10000000.0;
    private static final double HUGE_POSITIVE =  10000000.0;
    public static double epsilon = 1e-6;
    public static double egreedyEpsilon = 0.05;
    public SingleTreeNodeDetOpp parent;
    public SingleTreeNodeDetOpp[] children;
    public double totValue;
    public int nVisits;
    public static Random m_rnd;
    public int m_depth;
    protected static double[] bounds = new double[]{Double.MAX_VALUE, -Double.MAX_VALUE};
    public int childIdx;
    public int playerID;
    public int ROLLOUT_DEPTH = 20;
    public int NUM_ACTIONS = ActionMap.ActionMap.length;

    public static SimpleBattle rootState;

    public SingleTreeNodeDetOpp(Random rnd, int _playerID) {
        this(null, -1, rnd, _playerID);
    }

    public SingleTreeNodeDetOpp(SingleTreeNodeDetOpp parent, int childIdx, Random rnd, int _playerID) {
        this.parent = parent;
        this.m_rnd = rnd;
        children = new SingleTreeNodeDetOpp[NUM_ACTIONS];
        totValue = 0.0;
        this.childIdx = childIdx;
        this.playerID = _playerID;
        if(parent != null)
            m_depth = parent.m_depth+1;
        else
            m_depth = 0;
    }

    public void mctsSearch(ElapsedCpuTimer elapsedTimer) {

        long avgTimeTaken = 0;
        long acumTimeTaken = 0;
        int numIters = 0;

        long remainingLimit = 0;
        //long remaining = (long) (elapsedTimer.getMaxTime()/1000000.0);
        ElapsedCpuTimer testTimer = new ElapsedCpuTimer();
        testTimer.setMaxTime(elapsedTimer.getMaxTime());
        long remaining = testTimer.remainingTimeMillis();
        while(remaining > 2*avgTimeTaken && remaining > remainingLimit){
            SimpleBattle state = rootState.clone();
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            SingleTreeNodeDetOpp selected = treePolicy(state);
            double delta = selected.rollOut(state);
            backUp(selected, delta);
            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis()) ;
            avgTimeTaken  = acumTimeTaken/numIters;
            //remaining = (long) (elapsedTimer.getMaxTime()/1000000.0) - acumTimeTaken;
            remaining = testTimer.remainingTimeMillis();
            //System.out.println("remaining=" + remaining);
        }
    }

    public SingleTreeNodeDetOpp treePolicy(SimpleBattle state) {

        SingleTreeNodeDetOpp cur = this;

        while (!state.isGameOver() && cur.m_depth < ROLLOUT_DEPTH)
        {
           // System.out.println("treePolicy: cur.m_depth="+cur.m_depth);
            if (cur.notFullyExpanded()) {
               // System.out.println("treePolicy: not fully expanded, call expand, m_depth=" + m_depth);
                return cur.expand(state);
            } else {
               // System.out.println("treePolicy: fully expanded, call uct, m_depth=" + m_depth);
                SingleTreeNodeDetOpp next = cur.uct(state);
                cur = next;
            }
        }

        return cur;
    }


    public SingleTreeNodeDetOpp expand(SimpleBattle state) {
        int i=0;
        while(children[i] != null)
            i++;
        int bestAction = i;
        double bestValue = -1;
        for (; i < children.length; i++) {
            double x = m_rnd.nextDouble();
            if (x > bestValue && children[i] == null) {
               // System.out.println("m_depth=" + m_depth+ " expanded " + i);
                bestAction = i;
                bestValue = x;
            }
        }
        //Roll the state
        Action oppAction = new Action(0, 1, true);
        if(playerID==0){
            state.advance(ActionMap.ActionMap[bestAction], oppAction);
        } else {
            state.advance(oppAction, ActionMap.ActionMap[bestAction]);
        }
        SingleTreeNodeDetOpp tn = new SingleTreeNodeDetOpp(this,bestAction,this.m_rnd,playerID);
       // System.out.println("expand: new node created with bestAction="+bestAction);
        children[bestAction] = tn;
        return tn;
    }

    public SingleTreeNodeDetOpp uct(SimpleBattle state) {

        SingleTreeNodeDetOpp selected = null;
        double bestValue = -Double.MAX_VALUE;
        for (SingleTreeNodeDetOpp child : this.children)
        {
            double hvVal = child.totValue;
            double childValue =  hvVal / (child.nVisits + this.epsilon);

            childValue = Util.normalise(childValue, bounds[0], bounds[1]);
            //System.out.println("norm child value: " + childValue);

            double uctValue = childValue +
                    Math.sqrt(2) * Math.sqrt(Math.log(this.nVisits + 1) / (child.nVisits + this.epsilon));

            uctValue = Util.noise(uctValue, this.epsilon, this.m_rnd.nextDouble());     //break ties randomly

            // small sampleRandom numbers: break ties in unexpanded nodes
            if (uctValue >= bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }
        if (selected == null)
        {
            //int selectedInd = Util.randomIntInRange(0, this.children.length-1);
            //selected = this.children[selectedInd];
            //System.out.println("Randomly chosen " + selectedInd);
            throw new RuntimeException("Warning! returning null: " + bestValue + " : " + this.children.length + " " +
            + bounds[0] + " " + bounds[1]);
        }

        //Roll the state:
        Action oppAction = new Action(0, 1, true);
        if(playerID==0) {
            state.advance(ActionMap.ActionMap[selected.childIdx], oppAction);
        } else {
            state.advance(oppAction, ActionMap.ActionMap[selected.childIdx]);
        }
        return selected;
    }


    public double rollOut(SimpleBattle state)
    {
        int thisDepth = this.m_depth;

        while (!finishRollout(state,thisDepth)) {

            //System.out.println("rollOut: not finish rollout, thisDepth=" +thisDepth);
            int action = m_rnd.nextInt(NUM_ACTIONS);
            Action oppAction = new Action(0, 1, true);
            if(playerID==0) {
                state.advance(ActionMap.ActionMap[action],oppAction);
            } else {
                state.advance(oppAction,ActionMap.ActionMap[action]);
            }
            thisDepth++;
        }


        double delta = value(state);

        if(delta < bounds[0])
        {
            bounds[0] = delta;
            //System.out.println("delta is " +delta);
        }
        if(delta > bounds[1])
            bounds[1] = delta;

        //double normDelta = Util.normalise(delta ,lastBounds[0], lastBounds[1]);

        return delta;
    }

    public double value(SimpleBattle a_gameState) {

        boolean gameOver = a_gameState.isGameOver();
        int winner = a_gameState.getGameWinner();
        double rawScore = a_gameState.score(playerID);

        if(gameOver && winner == (1-playerID))
            rawScore += HUGE_NEGATIVE;

        if(gameOver && winner == playerID)
            rawScore += HUGE_POSITIVE;

        return rawScore;
    }

    public boolean finishRollout(SimpleBattle rollerState, int depth)
    {
        if(depth >= ROLLOUT_DEPTH) {      //rollout end condition.
           // System.out.println("depth=" + depth +"   >=   ROLLOUT_DEPTH="+ROLLOUT_DEPTH);
            return true;
        }

        if(rollerState.isGameOver()) {              //end of game
           // System.out.println("Game over");
            return true;
        }

        return false;
    }

    public void backUp(SingleTreeNodeDetOpp node, double result)
    {
        SingleTreeNodeDetOpp n = node;
        while(n != null)
        {
            n.nVisits++;
            n.totValue += result;
            n = n.parent;
        }
    }


    public int mostVisitedAction() {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;
        boolean allEqual = true;
        double first = -1;

        for (int i=0; i<children.length; i++) {

            if(children[i] != null)
            {
               // System.out.println("mostVisitedAction: not null");
                if(first == -1)
                    first = children[i].nVisits;
                else if(first != children[i].nVisits)
                {
                    allEqual = false;
                }

                double childValue = children[i].nVisits;
                childValue = Util.noise(childValue, this.epsilon, this.m_rnd.nextDouble());     //break ties randomly
               // System.out.println("mostVisitedAction: child=" +i +" bestValue=" + bestValue + " childValue=" + childValue );
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                }
            }
        }

        if (selected == -1)
        {
           // System.out.println("mostVisitedAction: Unexpected selection!");
            selected = 0;
        }else if(allEqual)
        {
            //If all are equal, we opt to choose for the one with the best Q.
            selected = bestAction();
        } else {
           // System.out.println("mostVisitedAction: Selected=" + selected);
        }

        return selected;
    }

    public int bestAction()
    {
        int selected = -1;
        double bestValue = -Double.MAX_VALUE;

        for (int i=0; i<children.length; i++) {
            if(children[i] != null) {
                double childValue = children[i].totValue / (children[i].nVisits + this.epsilon);
                childValue = Util.noise(childValue, this.epsilon, this.m_rnd.nextDouble());     //break ties randomly
               // System.out.println("bestAction: child=" +i+ " bestValue=" + bestValue + " childValue=" + childValue );
                if (childValue > bestValue) {
                    bestValue = childValue;
                    selected = i;
                    //System.out.println("bestAction: Selected=" + selected);
                }
            }
        }
       // System.out.println("bestAction: Selected=" + selected);

        if (selected == -1)
        {
           // System.out.println("bestAction: Unexpected selection!");
            selected = 0;
        }

        return selected;
    }


    public boolean notFullyExpanded() {
        for (SingleTreeNodeDetOpp tn : children) {
            if (tn == null) {
                return true;
            }
        }

        return false;
    }
}
