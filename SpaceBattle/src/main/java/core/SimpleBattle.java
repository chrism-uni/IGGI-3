package core;

import static core.Constants.*;

import controller.BattleController;
import controller.RenderableBattleController;
import execution.BattleTest;

import utils.Vector2d;
import utils.Util;
import utils.JEasyFrame;
import utils.StatSummary;
import utils.ElapsedCpuTimer;

import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.awt.*;
/**
 * Created by simon lucas on 10/06/15.
 * <p>
 * Aim here is to have a simple battle class
 * that enables ships to fish with each other
 * <p>
 * Might start off with just two ships, each with their own types of missile.
 */

public class SimpleBattle {

    // play a time limited game with a strict missile budget for
    // each player

    public static int nTicks = 10;
    boolean visible = true;
    public static long DURATION_PER_TICK = 20;

    public static int nbObstacles = 5;
    public static int MISSILE_BUDGET = 10000;
    public static int MINE_BUDGET = 30;
    public static int MISSILE_SPEED = 4;
    public static int MINE_SPEED = 0;
    public static int MINECOOLDOWN_TIME = 4;
    public static int COOLDOWN_TIME = 4;
    public static int LIFE = 3;
    public static double MIN_SHOOT_RANGE = 60;
    public static double maxShootRange = 10000;

    public static int MISSILE_COST = 1;
    public static int MINE_COST = 1;
    public static int fitFunc = 1;

    private static final double MAX_SCORE = 1000000;
    private static final double MIN_SCORE = -1000000;

    ArrayList<GameObject> objects;
    ArrayList<PlayerStats> stats;
    //ArrayList<Wall> obstacles;

    NeuroShip s1, s2;
    BattleController p1, p2;
    BattleView view;
    public int currentTick;

    double score1, score2;
    StatSummary ss1 = new StatSummary();
    StatSummary ss2 = new StatSummary();
    double scoreRecord[];
    double score1Record[];
    double score2Record[];

    int winner = -1;

    public SimpleBattle() {
        this(true);
        scoreRecord = new double[nTicks+1];
        score1Record = new double[nTicks+1];
        score2Record = new double[nTicks+1];
    }

    public SimpleBattle(boolean visible, int nTicks) {
        this(visible);
        this.nTicks = nTicks;
        scoreRecord = new double[nTicks+1];
        score1Record = new double[nTicks+1];
        score2Record = new double[nTicks+1];
    }

    public SimpleBattle(boolean visible) {
        this.objects = new ArrayList<>();
        this.stats = new ArrayList<>();
        //this.obstacles = new ArrayList<>();
        this.visible = visible;
        this.score1 = 0.0;
        this.score2 = 0.0;

        if (visible) {
            view = new BattleView(this);
            new JEasyFrame(view, "battle");
            view.repaint();
        }
    }

    public int getTicks() {
        return currentTick;
    }

    public double[] playGame(BattleController p1, BattleController p2) {
        this.p1 = p1;
        this.p2 = p2;
        reset(true);

        if (p1 instanceof KeyListener) {
            view.addKeyListener((KeyListener)p1);
            view.setFocusable(true);
            view.requestFocus();
        }

        if (p2 instanceof KeyListener) {
            view.addKeyListener((KeyListener)p2);
            view.setFocusable(true);
            view.requestFocus();
        }

        waitTillReady();
        while (!isGameOver()) {

            update();
            //System.out.println("current time: " + currentTick);
            //System.out.println("player 1: " + this.stats.get(0).life);
            //System.out.println("player 2: " + this.stats.get(1).life);
        }

        //update();
        if(this.winner == -1) {
            if (stats.get(0).nPoints > stats.get(1).nPoints)
                this.winner = 0;
            else if (stats.get(0).nPoints < stats.get(1).nPoints)
                this.winner = 1;
        }
        if(this.winner!=-1) {
            System.out.println("Player " + (this.winner+1) + " wins at " + currentTick + " with life " + stats.get(this.winner).life + " fired " + stats.get(this.winner).getMissilesFired() + " points " + stats.get(this.winner).nPoints);
            System.out.println("Player " + (2-this.winner) + " loss at " + currentTick + " with life " + stats.get(1-this.winner).life  + " fired " + stats.get(1-this.winner).getMissilesFired() + " points " + stats.get(1-this.winner).nPoints);
        } else {
            System.out.println("Player 1 draws at " + currentTick + " with life " + stats.get(0).life + " with life " + stats.get(0).life + " fired " + stats.get(0).getMissilesFired() + " points " + stats.get(0).nPoints);
            System.out.println("Player 2 draws at " + currentTick + " with life " + stats.get(1).life + " with life " + stats.get(1).life + " fired " + stats.get(1).getMissilesFired() + " points " + stats.get(1).nPoints);
        }

        if (p1 instanceof KeyListener) {
            view.removeKeyListener((KeyListener)p1);
        }
        if (p2 instanceof KeyListener) {
            view.removeKeyListener((KeyListener)p2);
        }

        double[] tmp = Util.combineArray(scoreRecord,score1Record);
        double[] allRecord = Util.combineArray(tmp,score2Record);

        return allRecord;
    }

    protected void waitTillReady()
    {
        if(visible)
        {
            while(!view.ready) {
                view.repaint();
                waitStep(1000);
            }
        }

        waitStep(1000);
    }

    /**
     * Waits until the next step.
     * @param duration Amount of time to wait for.
     */
    protected static void waitStep(int duration) {

        try
        {
            Thread.sleep(duration);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Randomly reset the game if randomInit==true
     */
    public void reset(boolean randomInit) {
        stats.clear();
        objects.clear();
        if(randomInit) {
            Vector<Integer> pos = randomPositionBy4Area();
            if(pos.get(0)>=pos.get(2)) {
                s1 = buildShip(pos.get(0), pos.get(1), 1, 0, 0);
                s2 = buildShip(pos.get(2), pos.get(3), -1, 0, 1);
            } else {
                s1 = buildShip(pos.get(0), pos.get(1), -1, 0, 0);
                s2 = buildShip(pos.get(2), pos.get(3), 1, 0, 1);
            }
            //for(int i=0; i<nbObstacles; i++) {
            //    addRandomObstacle();
            //}
        } else {
            s1 = buildShip(200, 250, 1, 0, 0);
            s2 = buildShip(300, 250, -1, 0, 1);
        }

        this.currentTick = 0;
        this.winner = -1;

        stats.add(new PlayerStats(MISSILE_BUDGET, 0, LIFE, 0, MISSILE_BUDGET, MINE_BUDGET, MINE_BUDGET));
        stats.add(new PlayerStats(MISSILE_BUDGET, 0, LIFE, 0, MISSILE_BUDGET, MINE_BUDGET, MINE_BUDGET));
        objects.add(s1);
        objects.add(s2);
    }

    /**
    public void addRandomObstacle() {
        obstacles.add(new Wall(Util.randomIntInRange(0,500),Util.randomIntInRange(0,500),Util.randomIntInRange(2,50),Util.randomIntInRange(2,50)));
    }
     */

    public Vector<Integer> randomPositionBy4Area() {
        Vector<Integer> pos = new Vector<Integer>();
        Random randomGenerator = new Random();
        int area1 = randomGenerator.nextInt(4)+1;
        int area2 = randomGenerator.nextInt(4)+1;
        while(area2==area1) {
            area2 = randomGenerator.nextInt(4)+1;
        }
        pos.add(Util.randomIntInRange(0,100)+((int)(area1%2)*250)+100);
        pos.add(Util.randomIntInRange(0,100)+((int)(area1/2)*250)+100);
        pos.add(Util.randomIntInRange(0,100)+((int)(area2%2)*250)+100);
        pos.add(Util.randomIntInRange(0,100)+((int)(area2/2)*250)+100);
        return pos;
    }

    protected NeuroShip buildShip(int x, int y, int dx, int dy, int playerId) {
        Vector2d position = new Vector2d(x, y, true);
        Vector2d speed = new Vector2d(true);
        Vector2d direction = new Vector2d(dx, dy, true);

        return new NeuroShip(position, speed, direction, playerId );
    }

    public void update() {

        //checkMissiles();
        // get the actions from each player
        checkMissiles();
        // apply them to each player's ship, taking actions as necessary
        ElapsedCpuTimer elapsedTimer = new ElapsedCpuTimer();
        elapsedTimer.setMaxTimeMillis(this.DURATION_PER_TICK);
        Action a1 = p1.getAction(this.clone(), 0, elapsedTimer);
        Action a2 = p2.getAction(this.clone(), 1, elapsedTimer);
        //System.out.println("Player 0 at time " + currentTick + " life="+this.stats.get(0).life+ " cooldown=" +this.stats.get(0).cooldown+" missiles="+this.stats.get(0).nMissiles);
        //System.out.println("Player 1 at time " + currentTick + " life="+this.stats.get(1).life+ " cooldown=" +this.stats.get(1).cooldown+" missiles="+this.stats.get(1).nMissiles);
        advance(a1, a2);
        // update missiles
        for (GameObject ob : objects) {
            if (ob instanceof BattleMissile)
                ob.update();
            if (ob instanceof Shockwave)
                ob.update();
        }

        checkCollision(s1);
        checkCollision(s2);
        checkMissiles();
        ss1.add(score(0));
        ss2.add(score(1));
    }

        //checkMines();
        // get the actions from each player


    public void advance(Action a1, Action a2) {
        // and fire any missiles as necessary
        if (a1.shoot) {
            fireMissile(s1.s, s1.d, 0);
        } else {
            stats.get(0).cooldown--;
        }

        if (a2.shoot) {
            fireMissile(s2.s, s2.d, 1);
        } else {
            stats.get(1).cooldown--;
        }
        if (a1.mine){
            dropMine(s1.s, s1.d, 0);
        }
        // now apply them to the ships
        s1.update(a1);
        s2.update(a2);

        wrap(s1);
        wrap(s2);



        wrap(s1);
        wrap(s2);
        /**
         for (GameObject object : objects) {
         wrap(object);
         }

         **/
        // here need to add the game objects ...
        /**
         java.util.List<GameObject> killList = new ArrayList<GameObject>();
         for (GameObject object : objects) {
         object.update();
         wrap(object);
         if (object.dead()) {
         killList.add(object);
         }
         }

         objects.removeAll(killList);
         **/
        currentTick++;

        NeuroShip ss1 = s1;
        NeuroShip ss2 = s2;



        //if(playerId == 0)
        //    System.out.println("player 1 currentTick: " +currentTick+"; d: " + dist + "; dp: " + distPoints + "; dot: " + dot + "; TOTAL: " + (dot*distPoints));
        //if(playerId == 1)
        //    System.out.println("player 2 currentTick: " +currentTick+"; d: " + dist + "; dp: " + distPoints + "; dot: " + dot + "; TOTAL: " + (dot*distPoints));

        /**
         * Check if the two ships are too closed to each other
         */
        double dist = ss1.distTo(ss2) - MIN_SHOOT_RANGE;
        if(dist<0)
        {
            Vector2d dir = Vector2d.subtract(ss2.s, ss1.s);
            dir.normalise();
            s2.addRepulsiveForce( 0.5f * MIN_SHOOT_RANGE, MIN_SHOOT_RANGE, dir);
            dir.multiply(-1);
            s1.addRepulsiveForce( 0.5f * MIN_SHOOT_RANGE, MIN_SHOOT_RANGE, dir);
        }

        updateScores();


        if (visible) {
            view.repaint();
            sleep();
        }

        if(scoreRecord != null)
            scoreRecord[currentTick] = score(0);
        if(score1Record != null)
            score1Record[currentTick] = score1;
        if(score2Record != null)
            score2Record[currentTick] = score2;
        //System.out.println(currentTick + " " + score(0));
        //int a = 0;
    }

    public void updateScores()
    {
        score1 = calcScore(0);
        score2 = calcScore(1);
    }


    /**
     * TODO: here to modify the heuristic
     * This method calculates the score for specific player
     * @param playerId
     * @return
     */
    private double calcScore(int playerId)
    {
        NeuroShip ss1 = s1;
        NeuroShip ss2 = s2;

        if(playerId == 1)
        {
            ss1 = s2;
            ss2 = s1;
        }

        double dist = Math.abs(ss1.distTo(ss2));//-minShootRange);


        double dot = ss1.dotTo(ss2);
        double dotDirs = ss1.dotDirections(ss2);
        double distPoints = 1.0/(1.0+dist/100.0);
        /**
         * Check if the opponent in the shooting range
         */
        double dotAngle= Math.sqrt(1)/2.0;
        double firePoints = stats.get(playerId).nPoints/(10);
        if(fitFunc==1)
            return (dot*distPoints + firePoints);
        else
            return firePoints;
    }

    public double score(int playerId)
    {
        if(stats.get(playerId).life>0 && stats.get(1-playerId).life<=0)
            return MAX_SCORE;
        if(stats.get(playerId).life<=0 && stats.get(1-playerId).life>0)
            return MIN_SCORE;
        //if(stats.get(playerId).life==0 && stats.get(1-playerId).life==0)
        //    return 0;
        //double remainedBudget = stats.get(playerId).nMissiles/this.missilesBudget;
        //double firePoints = 2*(stats.get(playerId).nPoints/27;
        if(fitFunc==1) {
            if(playerId == 0)
                return score1 - score2;
            else
                return score2 - score1;
        } else {
            if(playerId == 0)
                return score1;
            else
                return score2;
        }
    }

    public SimpleBattle clone() {
        SimpleBattle state = new SimpleBattle(false);
        state.objects = copyObjects();
        state.stats = copyStats();
        state.currentTick = currentTick;
        state.visible = false; //stop MCTS people having all the games :p

        state.s1 = s1.copy();
        state.s2 = s2.copy();
        return state;
    }

    protected ArrayList<GameObject> copyObjects() {
        ArrayList<GameObject> objectClone = new ArrayList<GameObject>();
        for (GameObject object : objects) {
            objectClone.add(object.copy());
        }

        return objectClone;
    }

    protected ArrayList<PlayerStats> copyStats() {
        ArrayList<PlayerStats> statsClone = new ArrayList<PlayerStats>();
        for (PlayerStats object : stats) {
            statsClone.add(new PlayerStats(object.nMissiles, object.cooldown, object.life, object.nPoints, MISSILE_BUDGET, MINE_BUDGET, MINE_BUDGET));
        }

        return statsClone;
    }

    protected void checkCollision(GameObject actor) {
        // check with all other game objects
        // but use a hack to only consider interesting interactions
        // e.g. asteroids do not collide with themselves
        if (!actor.dead() &&
                (actor instanceof BattleMissile
                        || actor instanceof NeuroShip || actor instanceof Shockwave)) {
            if (actor instanceof BattleMissile) {
                System.out.println("Missile: " + actor);
            }
            /**
             for(Wall wall:obstacles) {
             Rectangle2D thisWall = wall.getBound();
             boolean crash = thisWall.intersects(actor.getBound());
             if(crash) {
             actor.hit();
             int playerId = (actor == s1 ? 0 : 1);
             PlayerStats stats = this.stats.get(playerId);
             stats.cooldown++;
             return;
             }
             }
             **/
            // the actor is a ship
            int playerId = (actor == s1 ? 0 : 1);
            for (GameObject ob : objects) {
                if (ob instanceof Shockwave) {
                    if (overlap(actor, ob)) {
                        Vector2d dir = new Vector2d(actor.s,true);
                        dir.subtract(ob.s);
                        dir.normalise();
                        if(playerId==1) {
                            s2.addForceRotate(NeuroShip.MIN_FORCE, NeuroShip.MAX_FORCE, dir);
                            //    this.stats.get(0).nPoints += 10;
                        } else {
                            s1.addForceRotate(NeuroShip.MIN_FORCE, NeuroShip.MAX_FORCE, dir);
                            //   this.stats.get(1).nPoints += 10;
                        }
                    }
                }
                if(ob.getId() != playerId) {
                    if(ob instanceof NeuroShip) {
                        //System.out.println("ship " + ob.getId() + " playerId " + playerId);
                        if (overlap(actor, ob)) {
                            this.stats.get(playerId).life = 0;
                            this.stats.get(1 - playerId).life = 0;
                            //System.out.println(this.stats.get(1-playerId).life);
                            return;
                        }
                    } else if (ob instanceof BattleMissile) {
                        //System.out.println("missile " + ob.getId() + " playerId " + playerId);
                        if (overlap(actor, ob)) {
                            ob.hit();
                            this.stats.get(playerId).life--;
                            Vector2d dir = new Vector2d(ob.v, true);
                            dir.normalise();

                            if(playerId==1) {
                                s2.addForceRotate(NeuroShip.MIN_FORCE, NeuroShip.MAX_FORCE, dir);
                                this.stats.get(0).nPoints += 10;
                            } else {
                                s1.addForceRotate(NeuroShip.MIN_FORCE, NeuroShip.MAX_FORCE, dir);
                                this.stats.get(1).nPoints += 10;
                            }
                        }
                    }
                        else if (ob instanceof BattleMine) {
                            //System.out.println("missile " + ob.getId() + " playerId " + playerId);
                            if (overlap(actor, ob)) {
                                ob.hit();
                                this.stats.get(playerId).life--;
                                Vector2d dir = new Vector2d(ob.v, true);
                                dir.normalise();

                                if(playerId==1) {
                                    s2.addForceRotate(NeuroShip.MIN_FORCE, NeuroShip.MAX_FORCE, dir);
                                    this.stats.get(0).nPoints += 10;
                                } else {
                                    s1.addForceRotate(NeuroShip.MIN_FORCE, NeuroShip.MAX_FORCE, dir);
                                    this.stats.get(1).nPoints += 10;
                                }
                            }
                    }
                }
            }
            removeDead();
        }
    }

    protected void checkMissiles() {
        for(int i=objects.size()-1; i>1; i--) {
            GameObject ob1 = objects.get(i);
            for(int j=i-1; j>1; j--) {
                GameObject ob2 = objects.get(j);
                if(ob1.getId() != ob2.getId()) {
                    if(overlap(ob1,ob2)) {
                        ob1.hit();
                        ob2.hit();
                        Shockwave shock = new Shockwave(ob1.s, new Vector2d(0,0, true));
                        objects.add(shock);
                    }
                }
            }
        }
        removeDead();
    }
  //  protected void checkMines() {
  //      for(int i=objects.size()-1; i>1; i--) {
  //          GameObject ob1 = objects.get(i);
   //         for(int j=i-1; j>1; j--) {
   //             GameObject ob2 = objects.get(j);
    //            if(ob1.getId() != ob2.getId()) {
   //                 if(overlap(ob1,ob2)) {
    //                    ob1.hit();
     //                   ob2.hit();
    ////                }
    //            }
   //         }
    //    }
   //     removeDead();
  //  }
    protected void removeDead() {
        for(int i=objects.size()-1; i>1; i--) {
            GameObject ob = objects.get(i);
            if(ob.dead())
                objects.remove(i);
        }
    }

    private boolean overlap(GameObject actor, GameObject ob) {
        // otherwise do the default check
        double dist = actor.s.dist(ob.s);
        boolean ret = dist < (actor.r() + ob.r());
        return ret;
    }

    public void sleep() {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void fireMissile(Vector2d s, Vector2d d, int playerId) {
        // need all the usual missile firing code here
        NeuroShip currentShip = playerId == 0 ? s1 : s2;
        PlayerStats thisStats = this.stats.get(playerId);
        NeuroShip ss1 = s1;
        NeuroShip ss2 = s2;
        if(playerId == 1)
        {
            ss1 = s2;
            ss2 = s1;
        }
        double dist = ss1.distTo(ss2);
        //if (dist >= minShootRange && dist<=maxShootRange && thisStats.nMissiles > 0 && thisStats.cooldown <=0) {
        if (/*dist >= minShootRange && dist<=maxShootRange &&*/ thisStats.nMissiles > 0 && thisStats.cooldown <=0) {
            BattleMissile m = new BattleMissile(s, new Vector2d(0, 0, true), playerId);
            // the velocity is noisy
            //double noiseStrength = 0.05;
            double noiseStrength = 0.0;
            double releaseVelocity = MISSILE_SPEED * (1+Math.random()*noiseStrength);
            //double releaseVelocity =0;
            //m.v.add(d, releaseVelocity);
            m.v = Vector2d.multiply(d,releaseVelocity);
            // make it clear the ship
            m.s.add(m.v, (currentShip.r() + missileRadius) * 1.5 / m.v.mag());
            // add missile to the object list
            objects.add(m);
            //System.out.println("Fired: " + m);
            //sounds.fire();
            //this.stats.get(playerId).nMissiles--;
            thisStats.nMissiles--;
            thisStats.nPoints -= this.MISSILE_COST;
            thisStats.cooldown = this.COOLDOWN_TIME;
            //currentShip.addInverseForce(1, 5);
        } else {
            thisStats.cooldown--;
        }
    }

    protected void dropMine(Vector2d s, Vector2d d, int playerId) {
        // need all the usual missile firing code here
        NeuroShip currentShip = playerId == 0 ? s1 : s2;
        PlayerStats thisStats = this.stats.get(playerId);
        NeuroShip ss1 = s1;
        NeuroShip ss2 = s2;
        if(playerId == 1)
        {
            ss1 = s2;
            ss2 = s1;
        }
        double dist = ss1.distTo(ss2);
        //if (dist >= minShootRange && dist<=maxShootRange && thisStats.nMissiles > 0 && thisStats.cooldown <=0) {
        if (/*dist >= minShootRange && dist<=maxShootRange &&*/ thisStats.nMines > 0 && thisStats.cooldown <=0) {
            BattleMine m = new BattleMine(s, new Vector2d(0, 0, true), playerId);
            // the velocity is noisy
            //double noiseStrength = 0.05;
            double noiseStrength = 0.0;
            double releaseVelocity = MINE_SPEED * (1+Math.random()*noiseStrength);
            //double releaseVelocity =0;
            //m.v.add(d, releaseVelocity);
            m.v = Vector2d.multiply(d,releaseVelocity);
            // make it clear the ship
           // m.s.add(m.v, (currentShip.r() + mineRadius) * 1.5 / m.v.mag());
            // add missile to the object list
            objects.add(m);
            //System.out.println("Fired: " + m);
            //sounds.fire();
            //this.stats.get(playerId).nMissiles--;
            thisStats.nMines--;
            thisStats.nPoints -= this.MINE_COST;
            thisStats.cooldown = this.MINECOOLDOWN_TIME;
            //currentShip.addInverseForce(1, 5);
        } else {
            thisStats.cooldown--;
        }
    }
    public void draw(Graphics2D g) {
        // for (Object ob : objects)
        if (s1 == null || s2 == null) {
            return;
        }

        // System.out.println("In draw(): " + n);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(bg);
        g.fillRect(0, 0, size.width, size.height);

        if(!objects.isEmpty()) {
            GameObject[] objectsCopy = objects.toArray(new GameObject[objects.size()]);
            for (GameObject go : objectsCopy) {
                go.draw(g);
            }
        }

        s1.draw(g);
        if (p1 instanceof RenderableBattleController) {
            RenderableBattleController rbc = (RenderableBattleController)p1;
            rbc.render(g, s1.copy());
        }

        s2.draw(g);
        if (p2 instanceof RenderableBattleController) {
            RenderableBattleController rbc = (RenderableBattleController)p2;
            rbc.render(g, s2.copy());
        }

        p1.draw(g);
        p2.draw(g);

        if(BattleTest.SHOW_ROLLOUTS)
        {
            //waitStep(5000);
        }


    }

    public NeuroShip getShip(int playerId) {
        assert playerId < 2;
        assert playerId >= 0;

        if (playerId == 0) {
            return s1.copy();
        } else {
            return s2.copy();
        }
    }

    public ArrayList<GameObject> getObjects()
    {
        return new ArrayList<>(objects);
    }

    public int getCooldown(int playerId) {
        assert playerId < 2;
        assert playerId >= 0;

        return stats.get(playerId).cooldown;
    }

    public int getMissilesLeft(int playerId) {
        //return 0;
        assert playerId < 2;
        assert playerId >= 0;

        return stats.get(playerId).nMissiles;
    }
    public int getMinesLeft(int playerId) {
        //return 0;
        assert playerId < 2;
        assert playerId >= 0;

        return stats.get(playerId).nMines;
    }

    private void wrap(GameObject ob) {
        // only wrap objects which are wrappable
        if (ob.wrappable()) {
            //System.out.println( "before wrap" + ob.s);
            ob.s.x = (ob.s.x + width) % width;
            ob.s.y = (ob.s.y + height) % height;
            //System.out.println( "after wrap" + ob.s);
        }
    }

    public boolean isGameOver() {
        if(stats.get(1).life<=0 && stats.get(0).life<=0)
        {
            return true;
        }
        if(score1==MAX_SCORE || stats.get(1).life<=0)
        {
            this.winner = 0;
            //System.out.println("green win at " + currentTick+" " +score1 + " " +score2);
            return true;
        }
        if(score2==MAX_SCORE || stats.get(0).life<=0)
        {
            //System.out.println("blue win at " + currentTick+" " +score1 + " " +score2);
            this.winner = 1;
            return true;
        }
        /*if (getMissilesLeft(0) >= 0 && getMissilesLeft(1) >= 0) {
            //ensure that there are no bullets left in play
            if (objects.isEmpty()) {
                return true;
            }
        }*/

        /**
         PlayerStats stat = this.stats.get(0);
         assert(stat.cooldown<=this.life);
         if(stat.cooldown==this.life) {
         this.winner = 1;
         return true;
         }
         stat = this.stats.get(1);
         assert(stat.cooldown<=this.life);
         if(stat.cooldown==this.life) {
         this.winner = 0;
         return true;
         }
         **/
        return currentTick >= nTicks;
    }

    public int getGameWinner() {
        boolean end = isGameOver();
        assert((!end) && (this.winner !=-1));
        return this.winner;
    }

    public double getScore(int playerId)
    {
        if(playerId == 0)
            return score1;
        return score2;
    }

    static class PlayerStats {
        int nMissiles;
        int nMines;
        int cooldown;
        int life;
        int nPoints;
        int totalMissiles;
        int totalMines;

        public PlayerStats(int _nMissiles, int _cooldown, int _life, int _nPoints, int _totMissiles, int _nMines, int _totMines) {
            this.nMissiles = _nMissiles;
            this.nMines = _nMines;
            this.cooldown = _cooldown;
            this.life = _life;
            this.nPoints = _nPoints;
            this.totalMissiles = _totMissiles;
            this.totalMines = _totMines;
        }

        public int getMissilesFired() {
            return (this.totalMissiles-this.nMissiles);
        }
        public int getMinesFired() {

            return (this.totalMines-this.nMines);
        }

        public String toString() {
            return "M:" + nMissiles + "; C: " + cooldown + "; L: " + life + "; P: " + nPoints + " : ";
        }
    }
}
