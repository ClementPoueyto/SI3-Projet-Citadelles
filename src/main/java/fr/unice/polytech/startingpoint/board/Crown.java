package fr.unice.polytech.startingpoint.board;

import fr.unice.polytech.startingpoint.player.Player;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class Crown {
    Player player;

    public Player getCrownOwner(){
        return this.player;
    }

    /**
     * Donne l'objet couronne au joueur p.
     * @param p
     */
    public void goesTo(Player p){
        this.player = p;
    }

}
