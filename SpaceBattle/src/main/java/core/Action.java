package core;

/**
 * Created by Simon M. Lucas
 * sml@essex.ac.uk
 * Date: 26/12/11
 * Time: 15:09
 */
public class Action {
    public double thrust;
    public double turn;
    public boolean shoot;

    public Action() {}

    /**
     * Create a new action to be executed by the controller
     *
     * @param thrust 1 is full thrust, 0 is nothing
     * @param turn 1 is clockwise, -1 is anticlockwise, 0 is don't turn
     * @param shoot true is fire, false is don't fire
     */
    public Action(double thrust, double turn, boolean shoot) {
        this.thrust = thrust;
        this.turn = turn;
        this.shoot = shoot;
    }

    /**
     * Create an action which is a copy of an existing action
     *
     * @param a the action to clone
     */
    public Action(Action a) {
        thrust = a.thrust;
        turn = a.turn;
        shoot = a.shoot;
    }

    public String toString() {
        return thrust + " : " + turn + " : " + shoot;
    }
}
