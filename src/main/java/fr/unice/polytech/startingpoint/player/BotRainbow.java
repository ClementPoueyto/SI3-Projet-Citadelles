package fr.unice.polytech.startingpoint.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.board.DistrictColor;
import fr.unice.polytech.startingpoint.role.Role;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class BotRainbow extends BotSmart{
    public BotRainbow(int id){
        super(id);
    }

    /*-------------------------OVERRIDING --------------------------------------------------------*/

    @Override
    public Role processChooseRole(List<Role> toConsiderRoles) {
        String maxColor=hand.bestColorDistrict();       
        return bestRoleToChoose(toConsiderRoles,maxColor);
    }
    
    @Override
    public List<District> processWhatToBuild() {
        int gold=this.getGold();
        ArrayList<District> newDistricts = new ArrayList<>(buildNewColors(gold));
		hand.toList().forEach(d -> {
			if (!newDistricts.contains(d)) {
				if ((d.getName().equals("Cimetiere") && gold >= 5 && !getCharacter().toString().equals("Warlord"))
						|| (d.getName().equals("Cour des Miracles") && gold >= 2)
						|| (d.getName().equals("Manufacture") && gold >= 8) 
						|| (d.getName().equals("Laboratoire") && gold >= 6 && !hand.discardDistrictsForMultiColors().isEmpty())) {
					newDistricts.add(d);
				}
			}
		});     
        if(city.containsAllColors() || newDistricts.isEmpty()){
            return super.processWhatToBuild();
        }
        else{
            newDistricts.sort((a,b)->-Integer.compare(a.getValue(), b.getValue()));
            if(newDistricts.size()<this.getCharacter().getNumberDistrictBuildable()){
            	Set<District> smartBuild=new HashSet<>(super.processWhatToBuild());
                smartBuild.addAll(newDistricts);
                return new ArrayList<>(smartBuild);
                
            }
            

            return newDistricts;
        }
    }

    
    @Override
    public void discard(List<District> d) {
        d.sort((a,b)->
                Integer.compare(a.getCost(),b.getCost())
        );
        ArrayList<DistrictColor> missingColors = new ArrayList<>();
        this.missingColors().forEach(c -> missingColors.add(c));
        int tmp=this.getCharacter().getNumberDistrictKeepable();
        //vérifier si on a déja les 5 couleurs
        if(city.containsAllColors()){
            while(d.size()>tmp){
                if(d.get(0).getCost()>getGold()){
                    this.deck.putbackOne(d.remove(0));
                } else {
                    this.deck.putbackOne(d.remove(d.size() - 1));
                }
            }
        }
        else{
            //chercher les districts dont j'ai déja la couleur
            //dans ma main ou ma ville
            //vérifier
            Iterator<District> it=d.iterator();
            
            while(it.hasNext() && d.size()>tmp){
                District present= it.next();
                if(!missingColors.contains(present.primaryColor())){
                    it.remove();
                }
            }
            while(d.size()>tmp){
                if(d.get(0).getCost()>getGold()){
                    this.deck.putbackOne(d.remove(0));
                } else {
                    this.deck.putbackOne(d.remove(d.size() - 1));
                }
            }
        }
    }
    
    @Override
    public boolean coinsOrDistrict() {
        return getGold() < 2
                || hand.badCards(getGold()).size()<=hand.size()/2
                || missingColors().size() > 2;
    }

    /**
     * Souhait d'utilisation du laboratoire. Le quartier le moins cher 
     * dont la couleur est deja representee dans city sera privilégié
     * @return un optional d'un district de la main s'il lui est avantageux,
	 *  un optional empty sinon
     */
    @Override
    public Optional<District> wantsToUseLabo() {
    	List<District> districts = hand.discardDistrictsForMultiColors();
    	if(districts.isEmpty()) {
    		District dis = hand.lowCostDistrict();
    		if(dis == null) {
    	        return Optional.empty();
    		} else if(dis.getValue() < getGold()) {
                return Optional.of(dis);
    		}
        } else {
        	return Optional.of(districts.get(0));
        }
		return Optional.empty();
    }
    
    /**
	 * Souhait d'utilisation du cimetiere. Le bot aura tendance a l'utiliser si le quartier
	 * est abordable et de haute valeur, ou s'il n'as pas de district 
	 * de la couleur du district en question.
	 * @param le district a recuperer
	 * @return true s'il lui est avantageux et utile de l'utiliser,
	 * false sinon
	 */
    @Override
	protected boolean wantsToUseGraveyard(District dis) {
		if (dis != null && !getCharacter().toString().equals("Warlord")) {
			if (missingColors().contains(dis.primaryColor()) || dis.getCost() < getGold() && dis.getValue() > 4) {
				return true;
			}
		}
		return false;
	}

    /**
     * Le bot verifie s'il lui est pratique et utile d'utiliser la manufacture
     * en regardant sa ville, sa main, ses golds.
     * @return true s'il decide que oui, false sinon
     */
    @Override
    public boolean wantsToUseFabric() {
        return !city.containsAllColors() ||
        		(getGold() >= 8	&& !hand.highValuedDistrict(getGold()-3));
    }
    
    /*------------------------------------------------------------------------------*/

    public List<District> buildNewColors(int limit){
        if(city.containsAllColors()){
            return List.of();
        }
        Set <DistrictColor> tmpHand=hand.colorsOfHand();
        Set <DistrictColor> tmpCity=city.colorsOfCity();
        tmpHand.removeAll(tmpCity);

        if(tmpHand.isEmpty()){
            //si il n'y a pas dans la main des couleurs non construites
            return List.of();
        }
        else{
            var toConsider= hand.toList().stream().filter(d->tmpHand.contains(d.primaryColor()))
            .collect(Collectors.toList());

            return buildables(toConsider, limit).getDistricts();
        }
    }

    
}
