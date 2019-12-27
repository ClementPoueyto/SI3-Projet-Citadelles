package fr.unice.polytech.startingpoint.role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import fr.unice.polytech.startingpoint.player.*;

public class BishopTest {
    Player player;
    @BeforeEach
    void setUp(){
        player=new Player(1);
    }

    @Test
    void bishopTest(){
        Role bishop = new Bishop();
        assertEquals(5,bishop.getPosition());
        assertNull(bishop.getPlayer());
        assertEquals(1,bishop.getNumberDistrictBuildable());
        assertEquals(2,bishop.getNumberGold());
    }
}
