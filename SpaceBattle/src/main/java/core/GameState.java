package core;

import static core.Constants.*;
import utils.Vector2d;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class GameState {
    int level;
    int score;
    int nLives = 3;
    LiveList list;

    Ship ship;
    State state, nextState;
    String message;
    int wait;
    // Font font;

    public GameState() {

    }

    public GameState(int level) {
        this.level = level;
        list = new LiveList();
        makeAsteroids(level);
        makeColumns(3);
        makeShip();
        // makeBotShip();
        state = State.WaitingToStart;
    }

    public GameState copy() {
        GameState gs = new GameState();

        gs.level = level;
        gs.score = score;
        gs.nLives = nLives;
        gs.state = state;
        gs.nextState = nextState;
        gs.message = message;
        gs.wait = wait;


        // todo: need to make the handling of the object list more general
        // since currently we would be in danger of making two copies of the ship

        // the solution is to rewrite the livelist to store everything needed
        // in the list and then simply add the ship in afterwards


        gs.list = list.copy();

        gs.ship = ship.copy();

        // add the ship separately, we explicitly did not copy it before
        gs.list.add(gs.ship);

        return gs;
    }


    // update needs to take an action, or more generally a list of actions to
    // advance it to the next state

    public void update() {
        // System.out.println("GameState.update()");
        switch (state) {
            case WaitingToStart: {
                if (ship.action.shoot)
                    state = State.Playing;
                break;
            }
            case Playing: {
                list.update();
                if (ship.dead()) {
                    System.out.println("Dead ship!!!");
                    state = State.LifeLost;
                }
                // if (list.nAsteroids() == 0) state = State.LevelCleared;
                break;
            }
            case LifeLost: {
                wait = 100;
                nLives--;
                if (nLives <= 0) nextState = State.GameOver;
                else nextState = State.ReEntry;
                message = "Oops: be more careful!";
                System.out.println("Life lost!");
                state = State.Waiting;
                break;
            }

            case GameOver: {
                message = "Game Over";
                // System.out.println("Game Over");
                state = State.GameOver;
                break;
            }

            case ReEntry : {
                // do nothing until it is isSafe
                if (ship.dead()) makeShip();
                list.moveAsteroids();
                if (list.isSafe(ship)) {
                    state = State.Playing;
                }
                break;
            }
            case LevelCleared: {
                wait = 100;
                level++;
                list.clear();
                makeShip();
                makeAsteroids(level);
                nextState = State.Playing;
                state = State.Waiting;
                message = "Well done; prepare for next level!";
                break;
            }
            case Waiting: {
                if (wait <= 0) state = nextState;
                wait--;
            }
        }
    }

    public void draw(Graphics2D g) {
        // interesting: the best way to make the
        // game state tie in with what is drawn!
        // simple but not wholly satisfying is to use
        // another switch statement
        // System.out.println("GameState.draw()");
        switch (state) {
            case WaitingToStart: {
                View.messageScreen(g, "Hit Space Bar To Start");
                break;
            }

            case Playing: case ReEntry : {
                list.draw(g);
                break;
            }

            case LevelCleared: {
                View.messageScreen(g, "Level cleared!");

                break;
            }

            case Waiting : {
                View.messageScreen(g, message);
                break;
            }
            default: {
                View.messageScreen(g, "Game Over");
            }
        }
    }

    private void makeAsteroids(int nAsteroids) {
        Vector2d centre = new Vector2d(width / 2, height / 2, true);
        // assumes that the game object list is currently empty
        while (list.objects.size() < nAsteroids) {
            // choose a random position and velocity
            Vector2d s = new Vector2d(rand.nextDouble() * width,
                    rand.nextDouble() * height, true);
            Vector2d v = new Vector2d(rand.nextGaussian(), rand.nextGaussian(), true);
            if (s.dist(centre) > safeRadius && v.mag() > 0.5) {
                // Asteroid a = new Asteroid(this, s, v, 0);

                // these move in interesting ways ...
                Asteroid a = new AsteroidTwoFace(this, s, v, 0);
                // Asteroid a = new LissajousAsteroid(this, s, v, 0);
                list.objects.add(a);
            }
        }
//        System.out.println("Made " + list.objects.size());
//        System.out.println(list.objects);
    }

    private void makeColumns(int nColumns) {
        Vector2d centre = new Vector2d(width / 2, height / 2);
        // assumes that the game object list is currently empty
        for (int i=0; i<nColumns; i++) {
            list.objects.add(new Column(this, i));
        }
    }

    public boolean gameOn() {
        return state != State.GameOver;
    }

    public void add(GameObject ob) {
        list.add(ob);
    }

    public void makeShip() {
        ship = new Ship(this,
                new Vector2d(width / 2, height / 2, true),
                new Vector2d(true),
                new Vector2d(0, -1, true));
        add(ship);
    }

    public void makeBotShip() {
        BotShip ship = new BotShip(this,
                new Vector2d(width / 2, height / 2, true),
                new Vector2d(true),
                new Vector2d(0, -1, true));
        add(ship);
    }

    public void shipDeath() {
        // trigger some explosions
    }

    // public asteroidDeath()


    public void asteroidDeath(Asteroid a) {

        // if we still have smaller ones to
        // work through then do so
        // otherwise do nothing
        // score += asteroidScore;
        if (a.index < radii.length - 1) {
            // add some new ones at this position
            for (int i=0; i<nSplits; i++) {
                Vector2d v1 = new Vector2d(a.v, true);
                v1.add(rand.nextGaussian(), rand.nextGaussian());
                Asteroid a1 = new Asteroid(this, new Vector2d(a.s, true), v1, a.index + 1);
                list.add(a1);
            }
        }
        incScore(asteroidScore[a.index]);
    }

    public void asteroidTwoFaceDeath(Asteroid a) {

        //very messy to repeat this code JUST because
        // we needed a different class of Asteroid

        // there are much better ways to do this!!!

        if (a.index < radii.length - 1) {
            // add some new ones at this position
            for (int i=0; i<nSplits; i++) {
                Vector2d v1 = new Vector2d(a.v, true);
                v1.add(rand.nextGaussian(), rand.nextGaussian());
                Asteroid a1 = new AsteroidTwoFace(this, new Vector2d(a.s, true), v1, a.index + 1);
                list.add(a1);
            }
        }
        incScore(asteroidScore[a.index]);
    }


    // need a similar way to indicate the clearance of a column pipe

    private void incScore(int s) {
        score += s;
        if ( (score -s) % lifeThreshold > score % lifeThreshold ) {
            nLives++;
        }
    }
}