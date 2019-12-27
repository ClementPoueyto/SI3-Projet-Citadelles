package fr.unice.polytech.startingpoint.player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.role.Role;
/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */
public class BotBuildFast extends BotSmart{
    public BotBuildFast(int id) {
        super(id);
        
    }
    
    /* -----------------------OVERRIDING ------------------------------------*/
    
    @Override
    public List<District> processWhatToBuild() {
        List<District> toConsider;
        if(city.getSizeOfCity()==7){//si on est sur le point de finir 
            //on peut construire des cartes de cout 1 puisque le condotierre
            // ne peut pas détruire
            //les cités finies
            toConsider=getHand().toList().stream()
            .filter(d->!city.alreadyContains(d))
            .collect(Collectors.toList());
        }
        else{//ne pas construire des cartes de cout 1 sinon le condotierre peut les détruire facilement
            //sans rien payer
            toConsider=getHand().toList().stream()
            .filter(d->!city.alreadyContains(d)&& d.getCost()>1)
            .collect(Collectors.toList());
        }
        return buildables(
                        toConsider,
                        getGold()
                )
            .getDistricts().stream()
            .sorted((a,b)->-Integer.compare(a.getValue(), b.getValue()))
            .collect(Collectors.toList());
    }
    
    @Override
    public Player processWhoseDistrictToDestroy() {
        return board.playerWithTheBiggestCity(this);
    }

    

    @Override
    public District processDistrictToDestroy(Player target) {
        Optional<District> tmp=target.city.cheapestDistrict();
        if(tmp.isPresent()){
            return tmp.get();
        }
        else{
            return null;
        }
    }
    
    
    
    @Override
    public Role processChooseRole(List<Role> toConsiderRoles) {
        Optional<Role> alt1= toConsiderRoles.stream().filter(r->r.toString().equals("Architect")).findAny();
        Optional<Role> alt2=this.roleToOptimizeCoins(
            toConsiderRoles
        );
        
        
        
        Optional<Role> alt3= toConsiderRoles.stream().filter(r->r.toString().equals("Merchant")).findAny();
        Optional<Role> alt4= toConsiderRoles.stream().filter(r->r.toString().equals("Thief")).findAny();
        Optional<Role> alt5= toConsiderRoles.stream().filter(r->r.toString().equals("Murderer")).findAny();
        Optional<Role> alt6= toConsiderRoles.stream().filter(r->r.toString().equals("Wizard")).findAny();

        this.attributeProbsToPlayer();
        
        return alt1
        .or(()->alt2)
        .or(()->alt3)
        .or(()->alt4)
        .or(()->alt5)
        .or(()->alt6)
        .or(()->Optional.of(super.processChooseRole(toConsiderRoles))).get();
    }
    
    @Override
    protected boolean isBuildingFirst() {
        if(getCharacter().toString().equals("Architect")){ //pioche 2 cartes avant de jouer
            return true;
        }
        else if(getCharacter().toString().equals("Thief")){
            return false;
        }
        else if(getCharacter().toString().equals("Warlord")){
            return true;
        }
        else if(getCharacter().toString().equals("Wizard")){//si la main du magicien est mauvaise active son pouvoir, sinon il construit avant
            int countBadCards=getHand().nbTooExpensiveDistricts(getGold());
            return countBadCards!=getHand().size();
        }
        else {
            return true;
        }
    }
    
    @Override
    public boolean coinsOrDistrict() {
        return getGold() < 5;

    }
    
    /**
     * Souhait d'utilisation du laboratoire. Le quartier le plus cher sera privilegié
     * @return un optional d'un district de la main s'il lui est avantageux,
	 *  un optional empty sinon
     */
    @Override
    public Optional<District> wantsToUseLabo() {
        List<District> list = hand.cardsAboveGold(getGold());
       		if(!list.isEmpty() && city.getSizeOfCity() >= 6) {
                   District dis = hand.highCostDistrict(getGold());
                   return Optional.of(dis);
            }
            return super.wantsToUseLabo();
    }

    /**
	 * Souhait d'utilisation du cimetiere. Le bot aura tendance a l'utiliser si le quartier
	 * est abordable
	 * @param le district a recuperer
	 * @return true s'il lui est avantageux et utile de l'utiliser,
	 * false sinon
	 */
	@Override
	protected boolean wantsToUseGraveyard(District dis) {
		if (dis != null && !getCharacter().toString().equals("Warlord")) {
			District lowest = hand.lowCostDistrict();
			if (lowest == null) {
				if (dis.getCost() < getGold()) {
					return true;
				}
			} else {
				if ((lowest.getCost() < getGold() || dis.getCost() < getGold()) && lowest.getCost() != dis.getCost()) {
					return true;
				}
			}

		}
		return false;
	}
    /**
	 * Souhait d'utilisation de la manufacture. Le bot le souhaitera si a assez de golds pour
	 * payer son cout, et s'il est sur le point de finir sa city
	 * @return true s'il lui est avantageux et utile de l'utiliser,
	 * false sinon
	 */
	@Override
    public boolean wantsToUseFabric() {
        return getGold() >= 5
    			&& city.getSizeOfCity() < 7;
    }
	
	/*-------------------------------------------------------------*/

}