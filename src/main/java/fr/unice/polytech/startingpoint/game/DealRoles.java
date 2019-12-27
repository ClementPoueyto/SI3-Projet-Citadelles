package fr.unice.polytech.startingpoint.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.unice.polytech.startingpoint.role.Architect;
import fr.unice.polytech.startingpoint.role.Bishop;
import fr.unice.polytech.startingpoint.role.King;
import fr.unice.polytech.startingpoint.role.Merchant;
import fr.unice.polytech.startingpoint.role.Murderer;
import fr.unice.polytech.startingpoint.role.Role;
import fr.unice.polytech.startingpoint.role.Thief;
import fr.unice.polytech.startingpoint.role.Warlord;
import fr.unice.polytech.startingpoint.role.Wizard;
/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class DealRoles{
    private ArrayList<Role> leftRoles = new ArrayList<>();
    private ArrayList<Role> roles = new ArrayList<>();
    private ArrayList<Role> visible = new ArrayList<>();
    private Map<Integer,Integer> nbVisible=new HashMap<>(){
        private static final long serialVersionUID = 1L;
        {
        put(3,2);
        put(4,2);
        put(5,1);
        put(6,0);
        put(7,0);
    }};
        
    
    private Role hidden;
    
    public DealRoles(){
        this.visible=new ArrayList<Role>();
        this.roles.add(new Murderer());
        this.roles.add(new Thief());
        this.roles.add(new Wizard());
        this.roles.add(new King());
        this.roles.add(new Bishop());
        this.roles.add(new Merchant());
        this.roles.add(new Architect());
        this.roles.add(new Warlord());
    }

    /**
     * Prépare le paquet de rôles: des rôles sont définis hidden et un rôle est défini visible.
     * @param nbplayers
     */
    public void  readyToDistribute(int nbplayers){
        ArrayList<Role> al = new ArrayList<Role>(roles);
        Collections.shuffle(al);
        this.hidden=al.remove(0);
        Collections.shuffle(al);
        int compt=0;
        while(compt<nbVisible.get(nbplayers)){
            if(!al.get(0).toString().equals("King")){//Le Roi ne peut pas etre un Role Visible
                visible.add(al.remove(0));
                compt++;
            }
            else{
                leftRoles.add(al.remove(0));
            }
            
        }
        leftRoles.addAll(al);
    }

    /**
     * Réinitialise les rôles pour la prochaine distribution.
     */
    public void reInitializeRoles(){
        for(Role r:roles){
            r.reInitialize();
        }
        leftRoles.removeAll(leftRoles);
        visible.removeAll(visible);
    }
    
    public ArrayList<Role> getRoles(){
        return this.roles;
    }

    /**
     * C'est la position de Role qui est pris en paramètre
     * maintenant
     * @param position
     * @return
     */
    public Role getRole(int position){
        return roles.stream().filter(r->r.getPosition()==position).findFirst().get();
    }

    public Role getRole(String roleName){
        return roles.stream().filter(role-> role.toString().equals(roleName)).findFirst().get();
    }

    public ArrayList<Role> getLeftRoles(){
        return this.leftRoles;
    }

    public ArrayList<Role> getVisible(){
        return this.visible;
    }

    public Role getHidden() {
        return hidden;
    }

}