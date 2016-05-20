package execution;

import controller.Arnie.ArnieController;
import controller.CurlyStraight.sampleRHEA.search.CoevSearchCurlyStr;
import controller.pinkBomb.PinkBombController;
import core.*;
import controller.*;
import controller.human.*;
import controller.sampleRHEA.*;
import controller.sampleRHEA.search.*;
import controller.sampleRHEA.strategy.*;
import controller.sampleOLMCTS.*;
import controller.nullController.NullController;
import controller.onesteplookahead.OneStepLookAhead;
import controller.fireForwardController.FireForwardController;
import controller.random.RandomController;
import controller.rotateAndShoot.RotateAndShoot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 * Created by simon lucas on 10/06/15.
 */
public class BattleTest {
    BattleView view;
    public static boolean VISUAL;
    public static boolean SHOW_ROLLOUTS;
    public static int MAX_TICKS_GAME;
    public static int NUM_GAMES_TO_PLAY;

    public static void playOne(String ply1, String ply2)
    {
        boolean visuals = true;
        SimpleBattle battle = new SimpleBattle(visuals, MAX_TICKS_GAME);
        BattleController p1 = createPlayer(ply1);
        BattleController p2 = createPlayer(ply2);

        double []res = battle.playGame(p1, p2);
    }


    public static void tournament(String[] players)
    {
        double []points = new double[players.length];
        NUM_GAMES_TO_PLAY = 10;
        MAX_TICKS_GAME = 1000;

        //for(String pl1 : players)
        for(int i = 0; i < players.length; ++i)
        {
            for(int j = i+1; j < players.length; ++j)
            {
                System.out.println(players[i] + " vs " + players[j]);
                double res[] = playNTournament(players[i], players[j]);

                points[i] += res[0];
                points[j] += res[1];
            }
        }

        System.out.println();
        System.out.println("############################");
        for(int i = 0; i < players.length; ++i)
        {
            System.out.println(players[i] + ": " + points[i] + " points.");
        }
        System.out.println("############################");
    }


    public static double[] playNTournament(String ply1, String ply2)
    {
        boolean visuals = false;
        double []points = new double[2];

        for(int i = 0; i < NUM_GAMES_TO_PLAY; ++i) {

            SimpleBattle battle = new SimpleBattle(visuals, MAX_TICKS_GAME);
            BattleController p1 = createPlayer(ply1);
            BattleController p2 = createPlayer(ply2);
            battle.playGame(p1, p2);

            if(battle.getGameWinner() == -1)
            {
                points[0] += 0.5;
                points[1] += 0.5;
            }else{
                points[battle.getGameWinner()] += 1.0;
            }

        }
        return points;
    }

    public static void playN(String ply1, String ply2, String filename)
    {
        boolean visuals = false;
        double[][] results = new double[NUM_GAMES_TO_PLAY][MAX_TICKS_GAME*3];

        for(int i = 0; i < NUM_GAMES_TO_PLAY; ++i) {

            SimpleBattle battle = new SimpleBattle(visuals, MAX_TICKS_GAME);
            BattleController p1 = createPlayer(ply1);
            BattleController p2 = createPlayer(ply2);

            double []res = battle.playGame(p1, p2);
            System.arraycopy(res, 0, results[i], 0, MAX_TICKS_GAME*3);
        }

        System.out.println("Done.");
        dump(results, filename);
    }

    private static void dump(double[][] results, String filename)
    {
        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));

            for (int i = 0; i < results.length; ++i) {
                for (int j = 0; j < results[i].length; ++j) {
                    writer.write(results[i][j] + ",");
                }
                writer.write("\n");
            }

            writer.close();

        }catch(Exception e)
        {
            System.out.println("MEH: " + e.toString());
            e.printStackTrace();
        }
    }


    public static BattleController createPlayer(String ply)
    {
        Random rnd1 = new Random();

        switch (ply)
        {
            case "RHCA":
                return new BattleEvoController(new CoevSearch(
                        new UniformCrossover(rnd1),
                        new PMutation(rnd1),
                        new TournamentSelection(rnd1),
                        new RandomPairing(rnd1),
                        rnd1));
            case "RHGA":
                return new BattleEvoController(new GASearch(
                        new UniformCrossover(rnd1),
                        new PMutation(rnd1),
                        new TournamentSelection(rnd1),
                        new RndOpponentGenerator(rnd1),
                        rnd1));
            case "ONESTEP":
                return new OneStepLookAhead();
            case "OLMCTS":
                return new SingleMCTSPlayer(rnd1);
            case "NULL":
                return new NullController();
            case "RND":
                return new RandomController(rnd1);
            case "WASD":
                return new WASDController();
            case "ARROWS":
                return new ArrowsController();
            case "FIREFOR":
                return new FireForwardController();
            case "RAS":
                return new RotateAndShoot();
            case "DETOLMCTS":
                return new SingleMCTSPlayerDetOpp(rnd1);

            case "FUNT":
                return new controller.funGAController.BattleEvoController(new controller.funGAController.search.CoevSearch(
                        new controller.funGAController.strategy.TripleParentCrossover(rnd1),
                        new controller.funGAController.strategy.PMutation(rnd1),
                        new controller.funGAController.strategy.TournamentSelection(rnd1),
                        new controller.funGAController.strategy.RandomPairing(rnd1),
                        rnd1));

            case "PINK":
                return new PinkBombController(new controller.pinkBomb.GASearch(
                        new controller.pinkBomb.UniformCrossover(rnd1),
                        new controller.pinkBomb.PMutation(rnd1),
                        new controller.pinkBomb.TournamentSelection(rnd1),
                        new controller.pinkBomb.RndOpponentGenerator(rnd1),
                        rnd1));

            case "IGGI3":
                return new controller.iggi3.SingleMCTSPlayer(rnd1);

            case "ARNIE":
                return new ArnieController(new controller.Arnie.ArnieCoevSearch(
                        new controller.Arnie.ArnieUniformCrossover(rnd1),
                        new controller.Arnie.ArniePMutation(rnd1),
                        new controller.Arnie.ArnieTournamentSelection(rnd1),
                        new controller.Arnie.ArnieRandomPairing(rnd1), rnd1));

            case "CURLY":

                return new controller.CurlyStraight.sampleRHEA.BattleEvoController(new CoevSearchCurlyStr(
                        new controller.CurlyStraight.sampleRHEA.strategy.UniformCrossover(rnd1),
                        new controller.CurlyStraight.sampleRHEA.strategy.PMutation(rnd1),
                        new controller.CurlyStraight.sampleRHEA.strategy.TournamentSelection(rnd1),
                        new controller.CurlyStraight.sampleRHEA.strategy.RandomPairing(rnd1),
                        rnd1));

        }

        return new ArrowsController();
    }


}
