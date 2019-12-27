package fr.unice.polytech.startingpoint.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */
/**
 * Cette classe représente un tableau à double entrée
 * On peut imaginer en ligne d'entete les Personnages
 * et comme premiere colonne les id des joueurs
 * Les valeurs contenues au niveau des croisements
 * correspondent à la probabilité pour un joueur d'avoir
 * ce personnage
 * @author ANAGONOU Patrick
 */
public class MatchingProb {
    private HashMap<String,Integer> mappingRoleIndex=new HashMap<>();
    private HashMap<Integer,Integer> mappingPlayerIndex=new HashMap<>();

    private ArrayList<ArrayList<Double>> probs=new ArrayList<>();

    MatchingProb(List<Player>players){
        
        mappingRoleIndex.put("Murderer",0);
        mappingRoleIndex.put("Thief",1);
        mappingRoleIndex.put("Wizard",2);
        mappingRoleIndex.put("King", 3);
        mappingRoleIndex.put("Bishop",4);
        mappingRoleIndex.put("Merchant",5);
        mappingRoleIndex.put("Architect",6);
        mappingRoleIndex.put("Warlord",7);
        for (int i = 0; i < players.size(); i++) {
            mappingPlayerIndex.put(
                players.get(i).getId()
                , i);
        }

        for (int i = 0; i < mappingPlayerIndex.size(); i++) {
            ArrayList<Double> ligne=new ArrayList<>();
            for (int j = 0; j < mappingRoleIndex.size(); j++) {
                ligne.add(-1.0);
            }
            probs.add(ligne);
        }
    }

    public void setProbability(int playerId,String roleName,double probability){
        probs.get(mappingPlayerIndex.get(playerId)).set(mappingRoleIndex.get(roleName), probability);
           
    }

    public double getProbability(int playerId,String roleName){
        return probs.get(mappingPlayerIndex.get(playerId)).get(mappingRoleIndex.get(roleName));
    }

    Set<String> possibleRolesFor(int playerId){
        Set<String> possibleRoles=new HashSet<>();
        ArrayList<Double> lignePlayer=probs.get(mappingPlayerIndex.get(playerId));
        for (int i = 0; i < lignePlayer.size(); i++) {
            if(lignePlayer.get(i)<0){
                possibleRoles.add(
                    getKeyByValue(mappingRoleIndex,i)
                );
            }
        }
        return possibleRoles;
        
    }
    
    public static String getKeyByValue(Map<String, Integer> map, Integer value) {
        return map.entrySet()
                  .stream()
                  .filter(entry -> Objects.equals(entry.getValue(), value))
                  .map(Map.Entry::getKey)
                  .findFirst().get();
    }

    public void reInitialize(){
        mappingPlayerIndex.keySet().stream().forEach(p->{
            mappingRoleIndex.keySet().stream().forEach(r->{
                this.setProbability(p, r, -1.0);
            });
        });
    }


}