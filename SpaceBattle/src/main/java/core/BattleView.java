package core;

import static core.Constants.*;
import execution.LoadAndRun;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

import static java.awt.Color.black;

public class BattleView extends JComponent {
    static int offset = 0;
    int scale;
    // static int carSize = 5;
    static Color bg = black;
    SimpleBattle game;

    public boolean ready = false;
    static double viewScale = 1.0;


    public BattleView(SimpleBattle game) {
        this.game = game;
        scale = size.width - 2 * offset;

    }

    public void paintComponent(Graphics gx) {
        if (game.s1 == null || game.s2 == null) {
            return;
        }

        Graphics2D g = (Graphics2D) gx;
        AffineTransform at = g.getTransform();
        g.translate((1 - viewScale) * width / 2, (1-viewScale)*height / 2);

        // this was an experiment to turn it into a side-scroller
        // but it produces a weird moving screen effect
        // needs more logic in the drawing process
        // to wrap the asteroids that have been projected off the screen
        // g.translate(-(game.ship.s.x - width/2), 0);

        g.scale(viewScale, viewScale);

        game.draw(g);
        g.setTransform(at);
        paintState(g);
        ready = true;
    }


    public void paintState(Graphics2D g) {

        if(!game.objects.isEmpty()) {
            GameObject[] objectsCopy = game.objects.toArray(new GameObject[game.objects.size()]);
            for (GameObject object : objectsCopy) {
                object.draw(g);
            }
        }

        g.setColor(Color.white);
        g.setFont(font);
        // String str = "" + game.score + " : " + game.list.nShips() + " : " + game.state
        //         + " : " + game.list.isSafe(game.ship) + " : " + game.nLives;
        // FontMetrics fm = font.

        //String str = game.stats.get(0) + " " + game.stats.get(1) + " " + game.currentTick;
        SimpleBattle.PlayerStats p1Stats = game.stats.get(0);
        SimpleBattle.PlayerStats p2Stats = game.stats.get(1);
        //String strScores    = "Score:    " + p1Stats.getPoints() + " | " + p2Stats.getPoints();

        double sc0 = ((int)(game.score(0) * 1000) * 0.001);
        double sc1 = ((int)(game.score(1) * 1000) * 0.001);
        String strScores    = "Score:    " + sc0 + " | " + sc1;

        double scA0 = ((int)(game.ss1.sum() * 1000) * 0.001);
        double scA1 = ((int)(game.ss2.sum() * 1000) * 0.001);
        String strAcumScores = "Acc. Score:    " + scA0 + " | " + scA1;

        String strPoints = "Points:    " + p1Stats.nPoints + " | " + p2Stats.nPoints;
        String strMissiles  = "Missiles: " + p1Stats.getMissilesFired() + " | " + p2Stats.getMissilesFired();
        String strTicks = "Ticks:    " + game.currentTick;
        String strLives = "Life: " + p1Stats.life +  " | " + p2Stats.life;
        String p1 = "P1 Green: " + LoadAndRun.player1;
        String p2 = "P2 Blue: " + LoadAndRun.player2;
        //g.drawString(strScores, 10, 20);
        //g.drawString(strAcumScores, 10, 20);
        g.drawString(strPoints, 10, 20);
        g.drawString(strMissiles, 10, 50);
        g.drawString(strLives, 10, 80);
        g.drawString(strTicks, 10, 110);
        g.drawString(p1, 10, 140);
        g.drawString(p2, 10, 170);
    }


    public Dimension getPreferredSize() {
        return size;
    }


}
