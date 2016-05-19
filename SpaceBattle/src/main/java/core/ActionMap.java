package core;

import core.Action;
import java.util.Random;


/**
 * Created by dperez on 07/07/15.
 */
public class ActionMap
{
    public static Action[] ActionMap = new Action[]{
            new Action(0.0,0.0,false),
            new Action(0.0,-1.0,false),
            new Action(0.0,1.0,false),
            new Action(1.0,0.0,false),
            new Action(1.0,-1.0,false),
            new Action(1.0,1.0,false),
            new Action(0.0,0.0,true)
    };

    /**
     public static int mutateThrust(int action)
     {
     if (action==0||action==6) return 3;
     if (action==1) return 4;
     if (action==2) return 5;
     if (action==3) {
     Random m_rnd = new Random();
     if(m_rnd.nextDouble() < 0.5)
     return 0;
     else
     return 6;
     }
     if (action==4) return 1;
     if (action==5) return 2;
     return -1;
     }

     public static int mutateSteer(int action, boolean rightwise)
     {
     //From a side to center.
     if (action==1 || action==2) {
     Random m_rnd = new Random();
     if(m_rnd.nextDouble() < 0.5)
     return 0;
     else
     return 6;
     }
     if (action==4) return 3;
     if (action==5) return 3;

     if (action==0||action==6)
     if (rightwise) return 2;
     else return 1;

     if (action==3)
     if (rightwise) return 5;
     else return 4;

     return -1;
     }
     **/

    public static int mutateThrust(int action)
    {
        if (action==0||action==6) return 3;
        if (action==1) return 4;
        if (action==2) return 5;
        if (action==3) return 0;
        if (action==4) return 1;
        if (action==5) return 2;
        return -1;
    }

    public static int mutateSteer(int action, boolean rightwise)
    {
        //From a side to center.
        if (action==1 || action==2) return 0;
        if (action==4) return 3;
        if (action==5) return 3;

        if (action==0||action==6)
            if (rightwise) return 2;
            else return 1;

        if (action==3)
            if (rightwise) return 5;
            else return 4;

        return -1;
    }

    /**
     * Shooting
     * @param action
     * @return action index after mutation
     */
    public static int mutateShooting(int action)
    {
        if(action == 6)
            return 0;
        else
            return 6;
    }
}
