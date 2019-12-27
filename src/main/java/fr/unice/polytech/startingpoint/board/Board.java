package fr.unice.polytech.startingpoint.board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import fr.unice.polytech.startingpoint.player.Player;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class Board{
    private ArrayList<Player> players;

    public Board(){
        players = new ArrayList<>();
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
    public void setPlayers(Player ... players) {
        this.players=new ArrayList<>();
        this.players.addAll(Arrays.asList(players));
    }

    public ArrayList<Player> getPlayers() {
        return players;
    } 

    /**
     * 
     * @param except 
     * @return le joueur qui a la main contenant le plus de districts, utile pour établir les stratégies de jeu du magicien.
     */
    public Player playerWithTheBiggestHand(Player except){
        return this.players.stream().filter(p->p!=except).max(
            (p1,p2)->Integer.compare(p1.sizeOfHand(),p1.sizeOfHand())
        ).get();
        
    }

    /**
     *
     * @param except
     * @return le joueur ayant le quartier le pus cher.
     */
    public Player playerWithTheBiggestCity(Player except){
        Comparator<Player> critere1=(p1,p2)->Integer.compare(p1.sizeOfCity(),p2.sizeOfCity());
        Comparator<Player> critere2=(p1,p2)->Integer.compare(p1.totalValueOfCity(),p2.totalValueOfCity());
        return this.players.stream().filter(p->p!=except).max(
            critere1.thenComparing(critere2)
        ).get();
    }

    /**
     *
     * @param except
     * @return le joueur ayant le plus d'argent
     */
    public Player richestPlayer(Player except){
        return this.players.stream().filter(p->p!=except).max(
            (p1,p2)->Integer.compare(p1.getGold(),p1.getGold())
        ).get();
    }


    /**
     * @return un joueur aléatoirement parmi tous les joueurs.
     */

    public Player randomPlayer(){
        var random=new Random();
        return players.get(random.nextInt(players.size()));
    }

    /**
     * 
     * @param except
     * @return un joueur aléatoirement parmi tous les joueurs excepté le joueur en paramètre.
     */
    public Player randomPlayer(Player except){
        return players.stream().filter(p->p!=except).findAny().get();
    }

    /**
     * 
     * @return le joueur qui a le cimetière dans sa cité, null si aucun joueur ne l'a posé.
     */
	public Player existsGraveyardPlayer() {
		for(Player p : players) {
			if(p.cityHasTheDistrict("Cimetiere")) {
				return p;
			}
		}
		return null;
	}

}