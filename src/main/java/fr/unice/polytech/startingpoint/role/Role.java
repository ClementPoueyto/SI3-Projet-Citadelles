package fr.unice.polytech.startingpoint.role;

import fr.unice.polytech.startingpoint.board.*;
import fr.unice.polytech.startingpoint.player.*;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public abstract class Role {
     protected Logger logger = Logger.getLogger("Running");
     protected Player player;
     private final int position;
     protected int numberDistrictBuildable = 1;
    /**
     * Nombre de pieces qu'on peut retirer de la banque
     * n'inclut pas le nombre de pieces qu'on
     * gagne grace au matching Role.color==Disctrict.color
     */
     protected int numberGold = 2;
    /**
     * Permet de savoir si un Role est volé
     */
     protected boolean isStolen = false;
    /**
     * Permet de savoir si on est tué
     */
     private boolean isMurdered = false;
    /**
     * Nombre de districts qu'on peut consulter dans le Deck
     */
    protected int numberDistrictPickable = 2;
    /**
     * Nombre de districts parmi les cartes du Deck consulter que le joueur peut garder
     * Change pour l'architecte
     */
    protected int numberDistrictKeepable = 1;
    /**
     * Couleur du Role 
     * Penser à utiliser une enum Gem ou Color au lieu de String
     */
    private String color="#";
    Role(int position) {
        this.position = position;
    }

    /**
     * ???
     * @param d
     */
    void buildDistrict(District d){
        this.player.addToTheCity(d);
    }
    /**
     * Cette méthode doit remettre les paramètres potentiellement 
     * affecté par un tour précédent à leur valeur par défaut 
     * eg isStolen,isMurdered,player ...
     */
    public void reInitialize(){
        isMurdered=false;
        isStolen=false;
        player=null;
    }

    /**
     * Interface commune permettant à un Player de communiquer avec
     * son Role de façon indépendante de la nature exacte de ce personnage
     * (Murderer,Thief,Wizard ...)
     * Cette méthode va de concert avec la création dans Player d'attributs
     * permettants au Player de désigner sa cible au personnage 
     * Il suffit pour le personnage d'interroger le bon attribut avec son getter
     * eg: Murderer ---> player.getTargetToKill
     *  */

    public abstract void useSpecialPower();

    /**
     * Le Role est chargé de collecter l'argent généré par les cités
     * Ainsi lorsqu'on aura plusieurs roles par Player
     * il suffira d'appeler les méthodes collectRentMoney de chacun
     * 
     * Le problème ne se pose pas pour les roles sans couleur parce qu'il ne 
     * correspondront à rien
     * 
     */
    public void collectRentMoney(){
        ArrayList<District> districts=this.player.getCity().getListDistricts();

        for(District d:districts){
            if(d.hasColor(color)){
                //Que se passe t-il si il y a pas assez d'argent dans la banque?
                this.player.takeCoinsFromBank(1);
            }
        }

    }

    /**
     * On renvoie le nom de la classe 
     * Permet d'afficher facilement des messages pour suivre 
     * le jeu
     */
    @Override
    public String toString() {
    
        return this.getClass().getSimpleName();
    }

    /* Les getters et setters */

    public Player getPlayer(){
        return this.player;
    }

    public int getPosition(){
        return this.position;
    }

    

    public int getNumberDistrictBuildable(){
        return this.numberDistrictBuildable;
    }

    /**
     * Les districts merveilles Manufacture Bibliotheque et Observatoire
     * agissent sur le nombre de DistrictPickable et DistrictKeepable
     * @return
     */
    public int getNumberDistrictPickable() {
        if(player.cityHasTheDistrict("Manufacture") && player.isUsingFabric()){
            return 3;
        }
        else if(player.cityHasTheDistrict("Bibliotheque")){
                return 2;
        }
        else if(player.cityHasTheDistrict("Observatoire")){
            return 3;
        }
        else{
            return numberDistrictPickable;
        }

        
        
    }

    public int getNumberGold(){
        return this.numberGold;
    }

    public void setPlayer(Player player){
        this.player = player;
    }


    void stolen(){
        this.isStolen = true;
    }

    void murdered(){
        this.isMurdered = true;
    }

    public boolean isMurdered(){
        return this.isMurdered;
    }

    public boolean isStolen(){
        return this.isStolen;
    }

    public String getColor() {
        return color;
    }


    
    protected void setColor(DistrictColor color) {
        this.color = color.toString();
    }

    public int getNumberDistrictKeepable() {
        if(player.cityHasTheDistrict("Manufacture")&& player.isUsingFabric()){
            return 3;
        }
        else if(player.cityHasTheDistrict("Bibliotheque")){
                return 2;
        }
        else if(player.cityHasTheDistrict("Observatoire")){
            return 1;
        }
        else{
            return numberDistrictKeepable;
        }
        
    }
}
