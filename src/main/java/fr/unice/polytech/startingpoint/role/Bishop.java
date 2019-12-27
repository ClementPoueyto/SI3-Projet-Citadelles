package fr.unice.polytech.startingpoint.role;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

import fr.unice.polytech.startingpoint.board.DistrictColor;

public class Bishop extends Role{

    public Bishop(){
        super(5);
        this.setColor(DistrictColor.Religion);
    }

    void action(Role c){
    }

    @Override
    public void useSpecialPower() {

    }
    

}
