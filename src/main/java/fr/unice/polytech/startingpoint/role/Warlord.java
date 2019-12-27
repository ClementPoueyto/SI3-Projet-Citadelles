package fr.unice.polytech.startingpoint.role;

import java.util.logging.Level;

import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.board.DistrictColor;
import fr.unice.polytech.startingpoint.player.Player;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class Warlord extends Role {

    public Warlord(){
        super(8);
        this.setColor(DistrictColor.Warlord);
    }

    void action(Player target, District toDestroy){
        if(target.deleteDistrictFromCity(toDestroy)){
            //le joueur paie le prix du district -1
            this.player.getBank().deposit(toDestroy.getCost()-1,this.player);
            logger.log(Level.INFO,this+" (joueur numéro "+this.player.getId()+") détruit le district \n"+this.player.getDistrictToDestroy()+" du joueur numéro "+this.player.getTargetToDestroyDistrict().getId());
        }    
    }

    @Override
    public void useSpecialPower() {
        var condition=this.player.getDistrictToDestroy()!= null && 
                        this.player.getDistrictToDestroy().getCost()-1<=this.player.getGold() &&
                        this.player.getTargetToDestroyDistrict()!=null &&
                        this.player.getTargetToDestroyDistrict().sizeOfCity()<8;
        if(condition) {
            action(this.player.getTargetToDestroyDistrict(), this.player.getDistrictToDestroy());
        }
    }

    
}
