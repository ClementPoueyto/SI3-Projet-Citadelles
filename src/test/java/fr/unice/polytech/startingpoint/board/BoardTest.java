package fr.unice.polytech.startingpoint.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.unice.polytech.startingpoint.player.Player;

public class BoardTest{
    Board board;

    @BeforeEach
    public void setUp(){
        board=new Board();
    }

    @Test
    public void playerWithTheBiggestHandTest(){
        Player p1=mock(Player.class);
        when(p1.sizeOfHand()).thenReturn(4);
        Player p2=mock(Player.class);
        when(p2.sizeOfHand()).thenReturn(3);
        Player p3=mock(Player.class);
        when(p3.sizeOfHand()).thenReturn(1);

        board.setPlayers(p1,p2,p3);

        assertEquals(p1,board.playerWithTheBiggestHand(null)); 

        assertEquals(p2,board.playerWithTheBiggestHand(p1)); 
    }

    @Test
    public void playerWithTheBiggestCityTest(){
        Player p1=mock(Player.class);
        when(p1.sizeOfCity()).thenReturn(4);
        when(p1.totalValueOfCity()).thenReturn(29);
        Player p2=mock(Player.class);
        when(p2.sizeOfCity()).thenReturn(3);
        when(p2.totalValueOfCity()).thenReturn(30);
        Player p3=mock(Player.class);
        when(p3.sizeOfCity()).thenReturn(1);
        when(p3.totalValueOfCity()).thenReturn(30);


        board.setPlayers(p1,p2,p3);

        assertEquals(p1, board.playerWithTheBiggestCity(null));

        assertEquals(p2, board.playerWithTheBiggestCity(p1));



    }

    @Test
    public void playerWithTheBiggestCityComplexeTest(){
        Player p1=mock(Player.class);
        when(p1.sizeOfCity()).thenReturn(4);
        when(p1.totalValueOfCity()).thenReturn(29);
        Player p2=mock(Player.class);
        when(p2.sizeOfCity()).thenReturn(3);
        when(p2.totalValueOfCity()).thenReturn(30);
        Player p3=mock(Player.class);
        when(p3.sizeOfCity()).thenReturn(1);
        when(p3.totalValueOfCity()).thenReturn(30);

        Player p4=mock(Player.class);
        when(p4.sizeOfCity()).thenReturn(4);
        when(p4.totalValueOfCity()).thenReturn(30);

        board.setPlayers(p1,p2,p3,p4);

        assertEquals(p4, board.playerWithTheBiggestCity(null));


    }

    @Test
    public void richestPlayerTest(){
        Player p1=mock(Player.class);
        when(p1.getGold()).thenReturn(4);
        Player p2=mock(Player.class);
        when(p2.getGold()).thenReturn(3);
        Player p3=mock(Player.class);
        when(p3.getGold()).thenReturn(1);

        board.setPlayers(p1,p2,p3);

        assertEquals(p1,board.richestPlayer(null)); 

        assertEquals(p2,board.richestPlayer(p1)); 
    }

    @Test
    public void existsGraveyardPlayerTest() {
        Player p1=mock(Player.class);
        when(p1.cityHasTheDistrict("Cimetiere")).thenReturn(true);
        board.setPlayers(p1);
        assertEquals(p1,board.existsGraveyardPlayer());

        when(p1.cityHasTheDistrict("Cimetiere")).thenReturn(false);
        assertNull(board.existsGraveyardPlayer());

    }

    @Test
    public void randomPlayer(){
        Player p1=mock(Player.class);
        board.setPlayers(p1);
        assertEquals(p1,board.randomPlayer());

    }
}