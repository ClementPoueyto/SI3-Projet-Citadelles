package fr.unice.polytech.startingpoint.role;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class Architect extends Role {

    private boolean alreadyUsePower=false;

    public Architect() {
        super(7);
        this.numberDistrictBuildable+=2;
        
        
    }

    @Override
    public void reInitialize() {
        super.reInitialize();
        this.alreadyUsePower=false;
    }
    /**
        * Le pouvoir spécial de l'architecte c'est de pouvoir prendre 
        *deux cartes en plus
        */
    private void action(){
        if(!this.alreadyUsePower){
            this.player.getHand().addAll(this.player.getDeck().withdrawMany(2));
            alreadyUsePower=true;
        }
        
    }

    @Override
    public void useSpecialPower() {
        this.action();
    }
}
