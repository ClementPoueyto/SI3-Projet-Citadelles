package fr.unice.polytech.startingpoint.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.board.DistrictColor;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class Hand {
    private ArrayList<District> districts;

    Hand(){
        this.districts=new ArrayList<>();
    }
    
    public Hand(List<District> liste){
        this.districts=new ArrayList<>(liste);
    }

    /**
     * @return retourne le district le moins cher de la main, null si la main est vide.
     */
	public District lowCostDistrict() {
		if (districts.isEmpty()) {
			return null;
		} else {
            return districts.stream().min((a,b)->Integer.compare(a.getCost(),b.getCost())).get();
		}
	}

    public District lowCostDistrictForNextTurn(int golds) {
        District ld= lowCostDistrict();
        District optimized=ld;
        for(District d : districts){
            if(d.getCost()+ld.getCost()<=golds+2&&(!d.equals(ld))&&d.getCost()>optimized.getCost()){
                optimized=d;
            }
        }
        return optimized;
    }

    /**
     * Cherche un district qui vaut plus que le threeshold
     * @return true s'il y en a
     */
    public boolean highValuedDistrict(int threeshold) {
    	for(District d : districts){
    		if(d.getValue() > threeshold) {
    			return true;
    		}
    	}
    	return false;
    }

	public void addAll(List<District> list) {
        districts.addAll(list);
	}

	public void add(District d) {
        districts.add(d);
	}

	public boolean remove(District theDistrict) {
        return districts.remove(theDistrict);
    }

	public List<District> toList() {
		return districts;
	}

	public boolean isEmpty() {
		return districts.isEmpty();
	}

    /**
     * Compte le nombre de district dont le cout est plus eleve que le nombre de piece d'or posseder par le joueur
     * @return int : nombre de district
     */
    public int nbTooExpensiveDistricts(int gold){
        return (int)districts.stream().filter(d->d.getCost()>gold).count();
    }

    /**
     * Renvoie un set des primaryColors de la hand
     * @return
     */
    public Set<DistrictColor> colorsOfHand(){
        var colors= new HashSet<DistrictColor>();

        districts.forEach(d->{
            colors.add(d.primaryColor());
        });
 
        return colors;
    }

	public int size() {
		return districts.size();
	}

    /**
     * 
     * @param limit
     * @return les districts ayant un coup inférieur au paramètre limit.
     */
    public int nbCardsCostingLessThan(int limit){
		return (int)districts.stream().filter(d -> d.getCost() <= limit).count();
	}

    /**
     * 
     * @param gold
     * @return le district le plus cher contenu dans la main.
     */
	public District highCostDistrict(int gold) {
		if (districts.isEmpty()) {
			return null;
		} else {
			District highCost = districts.get(0);
			for (District d : districts) {
				if (d.getCost() > highCost.getCost() && (d.getCost() <= gold)) {
					highCost = d;
				}
			}
			return highCost;
		}
	}

    /**
     * 
     * @param gold
     * @return la liste des cartes considérées comme "mauvaises" de la main.
     */
	public List<District> badCards(int gold) {
        return districts.stream()
        .filter(d->d.getCost()+3<gold||d.getCost()>gold)
        .collect(Collectors.toList());
		
    }
    
    /**
     * 
     * @param gold
     * @return Une liste des cartes dont le cout==gold+1
     */

    public List<District> cardsAboveGold(int gold){
        return districts.stream()
              .filter(d->d.getCost() - gold == 1)
              .collect(Collectors.toList());
    	
    }

    /**
     * 
     * @return la liste des couleurs des districts contenus dans la ville et leurs occurences.
     */
    public HashMap<DistrictColor,Integer> countColors(){
        HashMap<DistrictColor,Integer> countColors=new HashMap<>();
        countColors.put(DistrictColor.Religion,0);
        countColors.put(DistrictColor.Commerce,0);
        countColors.put(DistrictColor.Warlord,0);
        countColors.put(DistrictColor.Noble,0);
        countColors.put(DistrictColor.Wonder,0);
        for (DistrictColor color :countColors.keySet()){
            districts.forEach(d->{
                if(d.hasColor(color)){
                    countColors.computeIfPresent(color,(k,v)->v+1);
                }
            });
        }
        return countColors;
    }

    public String bestColorDistrict(){
        HashMap<DistrictColor,Integer> countColors=countColors();
        Entry<DistrictColor,Integer> resultat= Collections.max(countColors.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue());

        if(resultat.getValue()<=0){
            return DistrictColor.Commerce.toString();
        }
        else{
            return resultat.getKey().toString();
        }
    }

    /**
     * Cette méthode retourne une liste contenant les éléments de la Hand
     * excepté ceux qui sont dans la liste excepts en parametre
     * Si deux ou plusieurs éléments identiques entre eux et à l'un des éléments de **excepts** alors
     * les deux sont enlevés
     * @param excepts
     * @return
     */
    public List<District> contentExceptStrict(List<District> excepts){
        return districts.stream().filter(d -> !excepts.contains(d)).collect(Collectors.toList());
    }

    public List<District> contentExcept(List<District> excepts){
        List<District> copy=new ArrayList<>(this.districts);
        for(var d:excepts){
            copy.remove(d);
        }
        return copy;
    }

    /**
     * 
     * @return les quartiers dont la ou les couleur(s) est/sont deja presente(s) dans la main
     */
    public List<District> discardDistrictsForMultiColors(){
    	List<District> res = new ArrayList<>();
    	HashMap<DistrictColor,Integer> countColors = countColors();
    	List<DistrictColor> severalDistrictsColor = new ArrayList<>();
    	countColors.forEach((k, v) -> {
    		if(v > 1) {
    			severalDistrictsColor.add(k);
    		}
    	});
    	districts.forEach(d -> {
    		if(severalDistrictsColor.contains(d.primaryColor())) {
    			res.add(d);
    		}
    	});
    	res.sort((a,b) -> a.getValue() - b.getValue());
    	return res;
    }
    
    /**
     * Principalement utilise pour les cartes "merveilles" : chercher un district par son nom
     * @param nom du quartier recherché
     * @return true si trouvé, false sinon
     */
    public boolean containsWonder(String wonder) {
		return districts.stream().anyMatch(d -> d.getName().equals(wonder));
	}
    
    /**
     * Principalement utilise pour les cartes "merveilles" : chercher un district par son nom
     * @param nom du quartier recherché
     * @return le district si trouvé, null sinon
     */
    public District findDistrictByName(String name) {
    	if(containsWonder(name)) {
    		for (District d : districts) {
				if (d.getName().equals(name)) {
					return d;
				}
			}
    	}
    	return null;
    }
    
	public void clearEverything() {
        districts.clear();
	}
}