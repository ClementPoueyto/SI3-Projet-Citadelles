package fr.unice.polytech.startingpoint.role;

import fr.unice.polytech.startingpoint.board.Board;
import fr.unice.polytech.startingpoint.game.DealRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import fr.unice.polytech.startingpoint.player.*;

import java.util.ArrayList;

public class MurdererTest {
    Player player;
    Player target;
    Board board;
    DealRoles dealRoles;

    @BeforeEach
    void setUp(){
        player=new Player(1);
        target = new Player(2);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        players.add(target);
        dealRoles = new DealRoles();
        board = new Board();

        player.setDealRoles(dealRoles);
        target.setDealRoles(dealRoles);

        player.setBoard(board);
        target.setBoard(board);
    }


    @Test
    void murdererTest(){
        Role murderer = new Murderer();
        Role merchant = new Merchant();
        player.setCharacter(murderer);
        target.setCharacter(merchant);
        player.setTargetToKill(merchant);
        murderer.useSpecialPower();
        assertEquals(1,murderer.getPosition());
        assertEquals(player,murderer.getPlayer());
        assertEquals(1,murderer.getNumberDistrictBuildable());
        assertEquals(2,murderer.getNumberGold());
        assertTrue( merchant.isMurdered());
    }
}
