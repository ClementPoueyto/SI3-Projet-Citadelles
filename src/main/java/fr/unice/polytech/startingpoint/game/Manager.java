package fr.unice.polytech.startingpoint.game;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import fr.unice.polytech.startingpoint.board.Bank;
import fr.unice.polytech.startingpoint.board.Board;
import fr.unice.polytech.startingpoint.board.Crown;
import fr.unice.polytech.startingpoint.board.Deck;
import fr.unice.polytech.startingpoint.player.Player;
import fr.unice.polytech.startingpoint.role.Role;
/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */
public class Manager implements PropertyChangeListener {
    private Logger logger = Logger.getLogger("Running");
    private int i=1;
    private Crown crown = new Crown();
    private Board board = new Board();
    private Deck deck=new Deck();
    private Bank bank=new Bank();
    private Boolean gameOver = false;
    private DealRoles dealRoles;

    private ArrayList<Player> winner = new ArrayList<>();

    /**
     * Tout ce qui est commun à un tour complet de table excluant ce qui ce passe en
     * début de partie
     * 
     * @param players
     */
    void oneRound(Player... players) {

        /**
         * On reInitialize les Roles on reInitialize les Personnages
         */
        logger.log(Level.INFO,"#########    Round#"+(i++)+"   #########");
        dealRoles.reInitializeRoles();
        for (Player p : players) {
            p.reInitializeForNextTurn();
        }

        dealRoles.readyToDistribute(players.length);
        crown.getCrownOwner().chooseRole();

         /**
          * On remet la couronne au Roi s'il est dans la partie
          */
        if(dealRoles.getRole("King").getPlayer()!=null){
            crown.goesTo(dealRoles.getRole("King").getPlayer());
        }

         /**
          * On commence à jouer par l'Assassin
          (s'il est dans la partie)
          */
        Iterator<Role> it = dealRoles.getRoles().iterator();
        while(it.hasNext()) {
        	Player p = it.next().getPlayer();
            if(p != null){
                logger.log(Level.INFO,"Tour du joueur " +p.getId()+" : "+p.getCharacter());
                p.playTurn();
            }
        }
    }

    public void letsPlay(Player... players) {
        Set<Integer> ids =new HashSet<>();
        for(Player p:players){
            ids.add(p.getId());
        }
        if(ids.size()!=players.length){
            throw new IllegalArgumentException("Les id des players doivent etre différents");
        }
        if(players.length<3 || players.length>7){
            throw new IllegalArgumentException("Il faut au moins trois joueurs et au max sept");
        }
        // On met les players en cercle
        for (int j = 0; j < players.length - 1; j++) {
            players[j].setNextPlayer(players[j + 1]);
        }
        players[players.length - 1].setNextPlayer(players[0]);

        
        board.setPlayers(players);

        dealRoles = new DealRoles();
        bank.setBourses(players);
        bank.distributeCoinsAtBeggining();
        for (Player p : players) {
            p.addPropertyChangeListener(this);
            p.setBoard(board);
            p.setDeck(deck);
            p.setDealRoles(dealRoles);
            p.takeCardsAtBeginning();
        }

        crown.goesTo(players[0]);

        /**
         * distribution des roles déclenchement de la distrib des roles par le joueur
         * ayant la couronne
         */
        while(!gameOver) {
            oneRound(players);
        }

        endGame(players);
        printResults(players);

    }

    /**
     * Permet d'afficher les résultats d'une partie.
     * @param players
     */
    public void printResults(Player... players) {
        String res = "";
        for (Player p : players) {
            res += "******************\n" + "Player: " + p.getId() + "\n" + "Points: " + p.points() + "\n"
                    + "******************\n";
        }
        res += "WINNER : \n" + winner.toString();
        logger.log(Level.INFO,res);
    }

    public void endGame(Player... players) {
        int max=List.of(players).stream().max((a,b)->Integer.compare(a.points(), b.points())).get().points();
        winner.addAll(
            List.of(players).stream().filter(p->p.points()==max).collect(Collectors.toList()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        gameOver=true;
    }

    public ArrayList<Player> getWinner(){
        return winner;
    }
}