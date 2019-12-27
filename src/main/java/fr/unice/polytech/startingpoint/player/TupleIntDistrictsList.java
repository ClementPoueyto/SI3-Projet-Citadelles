package fr.unice.polytech.startingpoint.player;

import java.util.List;

import fr.unice.polytech.startingpoint.board.District;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class TupleIntDistrictsList {
    private  int val=0;
        private final List<District> districts;

        TupleIntDistrictsList(int val,List<District> districts){
            this.districts=districts;
            this.val=val;
        }
        public int getVal() {
            return val;
        }

        public void addVal(int toAdd){
            val+=toAdd;
        }

        

        public List<District> getDistricts() {
            return districts;
        }

}