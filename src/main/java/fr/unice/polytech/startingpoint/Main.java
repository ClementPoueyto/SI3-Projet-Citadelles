package fr.unice.polytech.startingpoint;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.unice.polytech.startingpoint.game.Manager;
import fr.unice.polytech.startingpoint.player.BotBuildFast;
import fr.unice.polytech.startingpoint.player.BotRainbow;
import fr.unice.polytech.startingpoint.player.BotRnd;
import fr.unice.polytech.startingpoint.player.BotSpender;
import fr.unice.polytech.startingpoint.player.Player;
/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class Main {
    private static HashMap<Integer,Integer> stats=new HashMap<>();
    private static Logger resume;
    private static Logger statistic;
    private static int numberOfGames = 1000;


    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%5$s %n");
        resume = Logger.getLogger("Running");
        statistic = Logger.getLogger("Statistic");
    }

    public static void main(String... args) throws IllegalArgumentException, IOException {
        if(args.length>0){
            if(args[0].equals("verbose")){
                resume.setLevel(Level.INFO);// afficher les détails de la partie
            }
            else{
                resume.setLevel(Level.OFF); //ne pas afficher : informations de la partie
            }
            
            if(args.length>1){
                try {
                   numberOfGames=Integer.parseInt(args[1]); 
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Error : not a number");
                }
            }
        }
        else{
            resume.setLevel(Level.OFF); //informations de la partie
        }
        
        statistic.setLevel(Level.INFO); //statitisques de la partie
        Player p1 = null;
        Player p2 = null;
        Player p3 = null;
        Player p4 = null;

        /**
         * 1000 parties du meilleur bot contre le second
         */
        int n=0;
        setScores();
        while (n < numberOfGames) {
            p1 = new BotRainbow(1);
            p2 = new BotRnd(2);
            p3 = new BotSpender(3);
            p4 = new BotBuildFast(4);
            Manager manager = new Manager();
            manager.letsPlay(p1, p2,p3, p4);
            countWinner(manager.getWinner());
            n++;
        }
        statistic.log(Level.INFO,results(p1,p2,p3,p4));


        /**
         * 1000 parties du meilleur bot contre lui-même
         */
        n=0;
        setScores();
        while (n < numberOfGames) {
            p1 = new BotBuildFast(1);
            p2 = new BotBuildFast(2);
            p3 = new BotBuildFast(3);
            p4 = new BotBuildFast(4);
            Manager manager = new Manager();
            manager.letsPlay(p1, p2,p3, p4);
            countWinner(manager.getWinner());
            n++;
        }
        statistic.log(Level.INFO,results(p1,p2,p3,p4));

    }

    /**
     * @param winner
     *
     *      incrémente de 1 la nombre de victoire
     *      d'un joueur ayant remporté une partie.
     */
    private static void countWinner(ArrayList<Player> winner) {
        for(Player p :winner){
            stats.put(p.getId(),stats.get(p.getId())+1);
        }
    }

    private static void setScores(){
        stats.put(1,0);
        stats.put(2,0);
        stats.put(3,0);
        stats.put(4,0);
    }
    private static String results(Player...players){
        String res = "Pourcentages de victoires des bots sur "+numberOfGames+" parties :\n";
        DecimalFormat df = new DecimalFormat("0.0");
        for(Integer e : stats.keySet()) {
            res += players[e-1].getClass().getSimpleName() + " = " + df.format(((((double)stats.get(e))/numberOfGames)*100)) + " %\n";
        }
        return res;
    }
}
