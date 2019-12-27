package fr.unice.polytech.startingpoint.role;

import fr.unice.polytech.startingpoint.board.Board;
import fr.unice.polytech.startingpoint.board.Deck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import fr.unice.polytech.startingpoint.player.*;

public class ArchitectTest {
    Player player;
    Board board;
    Deck deck;
    @BeforeEach
    void setUp(){
        deck=new Deck();
        player=new Player(1);
        board = new Board();
        player.setBoard(board);
        player.setDeck(deck);
    }

    @Test
    void architectTest(){
        Role architect = new Architect();
        player.setCharacter(architect);
        architect.useSpecialPower();
        assertEquals(7,architect.getPosition());
        assertEquals(player,architect.getPlayer());
        assertEquals(3,architect.getNumberDistrictBuildable());
        assertEquals(2,architect.getNumberGold());
        assertFalse( player.getHand().isEmpty());
        assertEquals(2,player.getHand().size());
    }
}
