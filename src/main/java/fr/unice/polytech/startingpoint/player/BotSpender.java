package fr.unice.polytech.startingpoint.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.role.Role;
/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class BotSpender extends BotSmart {

    public BotSpender(int id) {
        super(id);
    }

    /*--------------------------@OVERRIDING---------------------------*/

    @Override
    public Role processChooseRole(List<Role> toConsiderRoles) {
        String maxColor=hand.bestColorDistrict();
        return bestRoleToChoose(toConsiderRoles,maxColor);
    }


    @Override
    public Player processWhoToExchangeHandWith() {
        return this.board.playerWithTheBiggestHand(this);
    }


    @Override
    public List<District> processWhatToBuild() {
    	List<District> specialBuilding = super.processWhatToBuild();
    	if(super.processWhatToBuild().isEmpty()) {
    		District tmp=this.whatToBuild(this.getGold());
            if(tmp!=null){
                return new ArrayList<>(List.of(tmp));
            }
    	}
    	return specialBuilding;
    }

    @Override
    public void discard(List<District> d){
            d.sort((a,b)->
                    Integer.compare(a.getCost(),b.getCost())
            );
            while(d.size()>this.getCharacter().getNumberDistrictKeepable()){//On ne garde qu'une carte
                if(d.get(0).getCost()>getGold()){
                    this.deck.putbackOne(d.remove(0));
                }
                else{
                    this.deck.putbackOne(d.remove(d.size()-1));
                }
            }
    }

    @Override
    public boolean coinsOrDistrict() {
        return getGold() < 2
                || hand.badCards(getGold()).size()<=hand.size()/2;
    }


    /**
     * Souhait d'utilisation du laboratoire. Le quartier le moins cher sera privilégié
     * @return un optional d'un district de la main s'il lui est avantageux,
	 *  un optional empty sinon
     */
    @Override
    public Optional<District> wantsToUseLabo() {
    	District dis = hand.lowCostDistrict();
    	if(dis == null) {
    		return Optional.empty();
    	} else if(dis.getValue() < getGold()) {
            return Optional.of(dis);
        }
        return Optional.empty();
    }

    /**
	 * Souhait d'utilisation du cimetiere. Le bot aura tendance a l'utiliser si le quartier
	 * est abordable et de haute valeur
	 * @param le district a recuperer
	 * @return true s'il lui est avantageux et utile de l'utiliser,
	 * false sinon
	 */
	@Override
	protected boolean wantsToUseGraveyard(District dis) {
		if (dis != null && !getCharacter().toString().equals("Warlord")) {
			if (dis.getCost() < getGold() && dis.getValue() > 4) {
				return true;
			}
		}
		return false;
	}

    /**
	 * Souhait d'utilisation de la manufacture. Le bot le souhaitera si a assez de golds pour
	 * payer son cout, et s'il n'a pas de quartier de haute valeur
	 * @return true s'il lui est avantageux et utile de l'utiliser,
	 * false sinon
	 */
    @Override
    public boolean wantsToUseFabric() {
        return getGold() >= 8
    			&& !hand.highValuedDistrict(getGold()-3);
    }
    
    /* -----------------------------------------------*/
    
    District whatToBuild(int limit) {
		if (handHasTheDistrict("Laboratoire") && 5 < limit && getHand().lowCostDistrict() != null) {
			return hand.findDistrictByName("Laboratoire");
		}
		if(handHasTheDistrict("Cimetiere") && 5 < limit && !getCharacter().toString().equals("Warlord")) {
			return hand.findDistrictByName("Cimetiere");
		}
		District district = getHand().highCostDistrict(limit);
		if (district.getCost() <= limit) {
			return district;
		} else {
			return null;
		}
	}

}