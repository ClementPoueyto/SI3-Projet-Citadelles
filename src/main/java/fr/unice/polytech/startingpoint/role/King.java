package fr.unice.polytech.startingpoint.role;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

import fr.unice.polytech.startingpoint.board.Crown;
import fr.unice.polytech.startingpoint.board.DistrictColor;

public class King extends Role {

    public King(){
        super(4);
        this.setColor(DistrictColor.Noble);
    }

    void action(Crown c){
        c.goesTo(this.player);
    }

    

    @Override
    public void useSpecialPower() {

    }
}
