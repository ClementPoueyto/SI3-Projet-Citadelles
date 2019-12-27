package fr.unice.polytech.startingpoint.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.board.DistrictColor;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class City {
    private ArrayList<District> districts;
    private int presentDate=0;
    
    public City(){
    	districts=new ArrayList<>();
    }

    /**
     * Cette méthode renverra la couleur qui permet
     * de rapporter le plus d'argent dans la ville
     * actuelle
     * On renverra null dans le cas où il n'y a pas
     * de couleur payante
     * @return la couleur la plus rentable
     */
    public DistrictColor mostPotentiallyPayingColor(){
        if(districts.isEmpty()){
            return null;
        }
        else{
            
            return List.of(DistrictColor.Warlord,DistrictColor.Commerce,DistrictColor.Noble,DistrictColor.Religion).stream().max((a,b)->
                Long.compare(nbOcurrencesOf(a) , nbOcurrencesOf(b))
            ).get();

        }

    }

    /**
     * 
     * @param availableColors
     * @return une liste de couleurs de districts qui peuvent rapporter le plus d'argent en fonction du rôle joué (Roi, Eveque, Condottière, Marchand).
     */
    public String mostPotentiallyPayingColor(List<String> availableColors){
        if(districts.isEmpty() || availableColors.isEmpty()){
            return null;
        }
        else{
            //les couleurs payantes sont : soldatesque,noblesse,commerce,religion
            return availableColors.stream().max((a,b)->
                Long.compare(nbOcurrencesOf(a) , nbOcurrencesOf(b))
            ).get();

        }

    }

    /**
     * 
     * @param color
     * @return le nombre d'occurences des districts de couleur color contenus dans la ville.
     */
    public int nbOcurrencesOf(DistrictColor color) {
       return (int)districts.stream().filter(d -> d.hasColor(color)).count();
    }

    /**
     * 
     * @param color
     * @return le nombre d'occurences des districts de couleur color contenus dans la ville.
     */
    public int nbOcurrencesOf(String color) {
        return (int)districts.stream().filter(d -> d.hasColor(color)).count();
    }

    /**
     * Renvoie un set des primaryColors de la city
     * @return
     */
    public Set<DistrictColor> colorsOfCity(){
        var colors= new HashSet<DistrictColor>();

        districts.forEach(d->{
            colors.add(d.primaryColor());
        });

        return colors;
    }

     /**
      * La méthode removeDistrict reset la buildDate du district à supprimer
      * @param toDelete
      * @return
      */
	public boolean removeDistrict(District toDelete) {
        toDelete.resetBuildDate();
        return districts.remove(toDelete);
        
    }
    
    /**
     * 
     * @return true si la ville contient au moins un disrtrict de chaque couleur.
     */
    public boolean containsAllColors(){
        var s=new HashSet<DistrictColor>();
		districts.stream().filter(d->!d.getName().equals("Cour des Miracles")).forEach(c->{
            s.add(c.primaryColor());
        });

        var optCour=this.getWonder("Cour des Miracles");
        
        if(s.size()==4 && optCour.isPresent()){
            return true;
        }

        return s.size()==5;
    }

    boolean checkDateContainsAllColors(){
        var s=new HashSet<DistrictColor>();
		districts.stream().filter(d->!d.getName().equals("Cour des Miracles")).forEach(c->{
            s.add(c.primaryColor());
        });

        var optCour=this.getWonder("Cour des Miracles");
        
        if(s.size()==4 && optCour.isPresent() && optCour.get().getBuildDate()<this.presentDate){
            return true;
        }
        if(optCour.isPresent()){
            s.add(optCour.get().primaryColor());
        }
        return s.size()==5;
    }
    
    public int getSizeOfCity(){
		return districts.size();
    }
    /**
     * La valeur de la City  avec les potentiels bonus
     * @return
     */
    public int totalValue(){
        if(this.checkDateContainsAllColors()){
            return 3+this.netValue();
        }
        else{
            return this.netValue();
        }
    }
    /**
     * La valeur nette de la City sans le bonus 
     * utiliser totalValue() pour les potentiels bonus
     */
    public int netValue(){
        return districts.stream().mapToInt(d -> d.getValue()).sum();
    }

    public void add(District theDistrict) {
        if(theDistrict!=null && !alreadyContains(theDistrict)){
            districts.add(theDistrict);
            theDistrict.setBuildDate(this.presentDate);
        }
    }
    
    /**
     * 
     * @param aDistrict
     * @return true si la ville contient déjà le district aDistrict.
     */
    public boolean alreadyContains(District aDistrict) {
        return districts.contains(aDistrict);
    }
    
    public ArrayList<District> getListDistricts(){
        return districts;
    }

	public boolean isEmpty() {
		return districts.isEmpty();
    }
    @Override
    public String toString() {
        return districts.toString();
    }

    /**
     * Principalement utilise pour les cartes "merveilles" : chercher un district par son nom
     * @param nom du quartier recherché
     * @return true si trouvé, false sinon
     */
	public boolean containsWonder(String wonder) {
		return districts.stream().anyMatch(d -> d.getName().equals(wonder));
    }
    
    public Optional<District> getWonder(String name){
        return districts.stream().filter(d -> d.getName().equals(name)).findAny();
    }
    
	public Collection<District> toList() {
		return districts;
	}

    /**
     * 
     * @return le district de la ville le moins cher.
     */
    public Optional<District> cheapestDistrict(){
		return districts.stream().min(
                (a,b)->Integer.compare(a.getCost(), b.getCost())
            );
    }
    
    public void nextDay(){
        this.presentDate+=1;
    }

    public District randomDistrict(){
        var rand=new Random();
        if(!districts.isEmpty()){
            return districts.get(rand.nextInt(districts.size())) ;
        }
        return null;
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

	public void clearEverything() {
        districts.clear();
        presentDate=0;
	}

}