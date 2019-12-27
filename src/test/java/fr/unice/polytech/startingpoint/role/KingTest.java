package fr.unice.polytech.startingpoint.role;

import fr.unice.polytech.startingpoint.board.Crown;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import fr.unice.polytech.startingpoint.player.*;

public class KingTest {
    Player player;
    Crown crown;
    @BeforeEach
    void setUp(){
        player=new Player(1);
        //crown = new Crown();
    }

    @Test
    void kingTest(){
        Role king = new King();
        assertEquals(4,king.getPosition());
    }
}
