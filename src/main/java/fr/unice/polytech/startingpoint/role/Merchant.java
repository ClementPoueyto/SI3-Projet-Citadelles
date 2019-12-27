package fr.unice.polytech.startingpoint.role;

import fr.unice.polytech.startingpoint.board.DistrictColor;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class Merchant extends Role {

    public Merchant(){
        super(6);
        this.numberGold++;
        this.setColor(DistrictColor.Commerce);
    }

    void action(Role c){
    }

    @Override
    public void useSpecialPower() {
    }

    
}
