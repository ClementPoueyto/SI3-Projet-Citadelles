package fr.unice.polytech.startingpoint.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TupleIntDistrictsListTest {

    TupleIntDistrictsList t;
    @BeforeEach
    void setUp(){
        t=new TupleIntDistrictsList(0,List.of());

    }

    @Test
    void testAddVal(){
        assertEquals(0, t.getVal());
        t.addVal(8);
        assertEquals(8, t.getVal());
    }

    @Test
    void testGetDistricts(){
        assertEquals(0,t.getDistricts().size());
    }


}