package fr.unice.polytech.startingpoint.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.role.Role;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class BotRnd extends Player{
    private Random random=new Random();

    public BotRnd(int id){
        super(id);
    }

    

    @Override
    public District processDistrictToDestroy(Player target) {
        if(target!=null){
            return target.city.randomDistrict();
        }
        return null;
    }
    @Override
    public Player processWhoseDistrictToDestroy() {
        return board.randomPlayer(this);
    }

    @Override
    public Player processWhoToExchangeHandWith() {
        return board.randomPlayer(this);
    }
    

    @Override
    public List<District> processWhatToBuild() {
        int i =0;
        var toBuild=new ArrayList<District>();
        var currHand=new ArrayList<District>();
        currHand.addAll(getHand().toList());
        int nb=getCharacter().getNumberDistrictBuildable();
        for (District d : currHand){
            if ((d.getCost() < getGold()) && i<nb) {
                toBuild.add(d);
                i++;
            }
        }
        return toBuild;
    }
    @Override
    public void discard(List<District> d){
        if(!d.isEmpty()){
            this.deck.putbackOne(d.remove(0));
        }
    }

    @Override
    public boolean coinsOrDistrict() {
        return random.nextBoolean();
    }

    @Override
    protected boolean isBuildingFirst() {
        return random.nextBoolean();
    }

    @Override
    public boolean wantsToUseFabric() {
        return random.nextBoolean();
    }

    @Override
    public Role processWhoToKill() {
       return  this.dealRoles.getRole(random.nextInt(8)+1);
    }

    @Override
    public Role processWhoToRob() {
        return  this.dealRoles.getRole(random.nextInt(8)+1);
    }
    
    Player pickRandomTargetPlayer(){
        return board.randomPlayer();
    }

    @Override
    public Role processChooseRole(List<Role> toConsiderRoles){
        return toConsiderRoles.get(random.nextInt(toConsiderRoles.size()));
    }

    @Override
    public Optional<District> wantsToUseLabo() {
        List<District> list = hand.cardsAboveGold(getGold());
        if(!list.isEmpty()) {
            District dis = list.get(random.nextInt(list.size()));
            return Optional.of(dis);
        }
        return super.wantsToUseLabo();
    }

    @Override
    public List<District> processCardsToExchange(){
        ArrayList<District> list = new ArrayList<>(hand.toList());
        ArrayList<District> cardsToExchange = new ArrayList<>();
        int i=0;
        if(!list.isEmpty()) i=random.nextInt(list.size());
        while(i>0){
            cardsToExchange.add(list.remove(random.nextInt(1)));
            i--;
        }
		return cardsToExchange;
	}
}
