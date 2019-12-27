package fr.unice.polytech.startingpoint.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class Deck{
    private ArrayList <District> list;
    public Deck(){
        list= new DistrictsInput().getDistricts();

        this.shuffle();//We shuffle the districts
    }


    /**
     * Mélange le deck.
     */
    public void shuffle(){
        Collections.shuffle(list);
    }

    /**
     * 
     * @return le district pioché.
     */
    public District withdraw(){
        if(!list.isEmpty()){
            return list.remove(0);
        }
        return null;
    }

    /*Avant d'appeler cette méthode il faut appeler 
    la méthode lenght de Deck pour vérifier si l'opération est possible
    On suppose ici que il reste au moins nb Districts dans Deck
    */
    public ArrayList<District> withdrawMany(int nb){
        ArrayList<District> taken=new ArrayList<>();
        while(nb>0){
            District d=withdraw();
            if(d!=null){
                taken.add(d);
            }
            nb--;
        }
        return taken;
    }

    /**
     * @param d
     *      un district
            
        On remet le district d sur le Deck
        
     * 
     */
    public void putbackOne(District d){
        if(d!=null){
            list.add(d);
            this.shuffle();
        } 
    }

    /**
     * @param c
     *      une collection de District
            
        On remet le district d sur le Deck
        
     * 
     */
    public void putbackMany(ArrayList<District> c){
        list.addAll(c);
        this.shuffle();
    }

    /**
     * @param d
     *      un district
            

        @return 
            Un district

            La méthode peut renvoyer null si il n'y avait plus de District dans 
            le deck. Il faut donc vérifier la valeur de retour
        
     * 
     */
    public District exchangeOne(District d){
        var tmp=this.withdraw();
        list.add(d);
        this.shuffle();
        return tmp;
    }

    /**
     * @param districts
            Une collection de cartes à remettre dans le deck

        @return 
            Une arraylist de district de même taille que la taille du paramètre

            Si le nombre de Districts à échanger est supérieur au nombre de cartes du deck 
            aucune action n'est entreprise
        
     * 
     */
    public ArrayList<District> exchangeMany(List<District> districts){
        var l=new ArrayList<District>();
        if(districts.size()<list.size()){
            l.addAll(withdrawMany(districts.size()));
            list.addAll(districts);
        }
        return l;
    }

    /**
     * 
     * @return le nombre de cartes dans le deck.
     */
    public int numberOfCards(){
        return list.size();
    }





    
}