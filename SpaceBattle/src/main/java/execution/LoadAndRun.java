package execution;

/**
 * Created by jliu on 15/05/16.
 */

import controller.sampleRHEA.search.*;
import controller.sampleRHEA.strategy.*;
import core.SimpleBattle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Enumeration;

public class LoadAndRun {
    private Properties prop = new Properties();
    public static String player1;
    public static String player2;

    public static void main(String[] args) {
        LoadAndRun run = new LoadAndRun();
        run.loadProperties();
        run.printProperties();
        run.setProperties();
        run.runBattleGame();
    }

    private void loadProperties () {
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
    }

    private void printProperties() {
        Enumeration<?> e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = prop.getProperty(key);
            System.out.println("Key : " + key + ", Value : " + value);
        }
    }

    private void setProperties() {
        // players
        player1 = prop.getProperty("player1");
        player2 = prop.getProperty("player2");
        // test variables
        BattleTest.MAX_TICKS_GAME = Integer.parseInt(prop.getProperty("MAX_TICKS_GAME"));
        BattleTest.NUM_GAMES_TO_PLAY = Integer.parseInt(prop.getProperty("NUM_GAMES_TO_PLAY"));
        BattleTest.VISUAL = Boolean.parseBoolean(prop.getProperty("VISUAL"));
        BattleTest.SHOW_ROLLOUTS = Boolean.parseBoolean(prop.getProperty("SHOW_ROLLOUTS"));
        // variables of ship
        SimpleBattle.DURATION_PER_TICK = Integer.parseInt(prop.getProperty("DURATION_PER_TICK"));
        SimpleBattle.LIFE = Integer.parseInt(prop.getProperty("LIFE"));
        SimpleBattle.MIN_SHOOT_RANGE = Integer.parseInt(prop.getProperty("MIN_SHOOT_RANGE"));
        // variables of missile
        SimpleBattle.MISSILE_BUDGET = Integer.parseInt(prop.getProperty("MISSILE_BUDGET"));
        SimpleBattle.MISSILE_SPEED = Integer.parseInt(prop.getProperty("MISSILE_SPEED"));
        SimpleBattle.COOLDOWN_TIME = Integer.parseInt(prop.getProperty("COOLDOWN_TIME"));
        // variables for RHEAs
        if(player1.equals("RHCA") || player2.equals("RHCA") || player1.equals("RHGA") || player2.equals("RHGA")) {
            Search.NUM_ACTIONS_INDIVIDUAL = Integer.parseInt(prop.getProperty("NUM_ACTIONS_INDIVIDUAL"));
            Search.MACRO_ACTION_LENGTH = Integer.parseInt(prop.getProperty("MACRO_ACTION_LENGTH"));
            TournamentSelection.TOURNAMENT_SIZE = Integer.parseInt(prop.getProperty("TOURNAMENT_SIZE"));
            PMutation.MUT_PROB = Double.parseDouble(prop.getProperty("MUT_PROB"));
            // variables for RHCA
            if(player1.equals("RHCA") || player2.equals("RHCA")) {
                ICoevPairing.GROUP_SIZE = Integer.parseInt(prop.getProperty("RHCA_GROUP_SIZE"));
                CoevSearch.ELITISM = Integer.parseInt(prop.getProperty("RHCA_ELITISM"));
                CoevSearch.NUM_INDIVIDUALS = Integer.parseInt(prop.getProperty("RHCA_NUM_INDIVIDUALS"));
            }
            // variables for RHGA
            if(player1.equals("RHGA") || player2.equals("RHGA")) {
                GASearch.ELITISM = Integer.parseInt(prop.getProperty("RHGA_ELITISM"));
                GASearch.NUM_INDIVIDUALS = Integer.parseInt(prop.getProperty("RHGA_NUM_INDIVIDUALS"));
            }
        }
    }

    private void runBattleGame() {
        // prepare output
        String player1name = player1;
        String player2name = player2;
        if (player1.equals("RHCA") || player1.equals("RHGA")) {
            player1name = player1 + "_" + Search.NUM_ACTIONS_INDIVIDUAL + "x" + Search.MACRO_ACTION_LENGTH;
        }
        if (player2.equals("RHCA") || player2.equals("RHGA")) {
            player2name = player2 + "_" + Search.NUM_ACTIONS_INDIVIDUAL + "x" + Search.MACRO_ACTION_LENGTH;
        }
        // run the game
        if (BattleTest.VISUAL) {
            BattleTest.playOne(player1, player2);
        } else {
            BattleTest.playN(player1, player2, "data/" + player1name + "_vs_" + player2name + "_" + BattleTest.MAX_TICKS_GAME + "x" + BattleTest.NUM_GAMES_TO_PLAY + ".txt");
        }
    }
}
