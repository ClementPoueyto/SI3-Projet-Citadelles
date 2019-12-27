package fr.unice.polytech.startingpoint.board;

import java.util.HashMap;
import java.util.List;

import fr.unice.polytech.startingpoint.player.Player;

/**
 * @author Patrick Anagonou
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
* */


public class Bank{
    private final int  NBCOINS=30;
    private int currNbCoins=NBCOINS;
    private HashMap<Player,Integer> bourses;

    public Bank(){
    	bourses = new HashMap<>();
    }

    /**
     * @param players
     *
     */
    public void setBourses(List<Player> players) {
        bourses = new HashMap<>();
        for(Player player : players){
            bourses.put(player,0);
            player.setBank(this);
        }
    }

    public void setBourses(Player... players) {
        bourses = new HashMap<>();
        for(Player player : players){
            bourses.put(player,0);
            player.setBank(this);
        }
    }

    public int getCurrNbCoins() {
        return currNbCoins;
    }

    /**
     * @param desiredAmount
     * @return True s'il reste assez d'argent dans la banque, false sinon
     */
    public boolean canWithdraw(int desiredAmount){
        if(desiredAmount<0){
            return false;
        }
        else{
            return !(currNbCoins<desiredAmount);
        }
        
    }

    /**
     * La banque ajoute de l'argent au player
     * @param nb
     * @param player
     * @return True si la transaction a été effectué, false sinon
     */
    public boolean withdraw(int nb, Player player){

        if(currNbCoins < nb || nb < 0){
            return false;
        }
        else{
            currNbCoins-=nb;
            int addMoney = bourses.get(player);
            addMoney+=nb;
            bourses.put(player,addMoney);
            return true; 
        }
    }

    /**
     * Fonction pour enlever l'argent du compte du joueur
     * @param nb
     * @param player
     * @return True si la transaction a été effectué, false sinon
     */
    public boolean deposit(int nb,Player player){
        if((currNbCoins+nb) <=NBCOINS && nb>=0 && bourses.get(player)>=nb){
            currNbCoins+=nb;
            bourses.computeIfPresent(player, (k,v)->v-nb);
            return true;
        }
        else{
            return false;
        }

    }

	public int getPlayerMoney(Player player) {
		return bourses.get(player);
    }
    /**
	 * En tout début de partie chacun prend deux pièces avant 
	 * même de prendre son personnage
	 * D'où cette méthode
	 */
    public void distributeCoinsAtBeggining(){
        for(Player p : bourses.keySet()){
            bourses.put(p,2);
            currNbCoins-=2;
        }
    }

    /**
     * Transfère l'argent de la bourse d'un joueur à la bourse d'un autre joueur
     * @param from
     * @param to
     */
    public void transferFromTo(Player from,Player to){
        bourses.computeIfPresent(to, (k,v)->v+bourses.get(from));
        bourses.put(from, 0);
    }



}