package fr.unice.polytech.startingpoint.board;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DistrictTest{

    District d1 ;
    District d2;
    District d3 ;
    @BeforeEach
    public void setUp(){
        d1 = new District(3,3,DistrictColor.Religion,"test");
        d2 = new District(3,4,DistrictColor.Religion,"test");
        d3 = new District(3,4,DistrictColor.Religion,"test2");
    }
    
    @Test
    public void equalsTest(){
        assertTrue(d1.equals(d2));
    }
    @Test
    public void notEqualsTest(){
        assertFalse(d1.equals(d3));
    }

    @Test
    public void hasColorTest(){
        assertTrue( d1.hasColor(DistrictColor.Religion));
        assertFalse(d1.hasColor(DistrictColor.Commerce)); 
    }

    @Test
    public void hasColorTestMulticolor(){
        District d=new District(4,4,
            "merveille-religion-commerce","Polytech");

        assertTrue( d.hasColor(DistrictColor.Religion));
        assertTrue( d.hasColor(DistrictColor.Commerce));
        assertTrue( d.hasColor(DistrictColor.Wonder));
        assertFalse( d.hasColor(DistrictColor.Warlord));
    }
}