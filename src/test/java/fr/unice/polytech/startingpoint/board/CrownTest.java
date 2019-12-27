package fr.unice.polytech.startingpoint.board;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.unice.polytech.startingpoint.player.BotBuildFast;
import fr.unice.polytech.startingpoint.player.Player;


import org.junit.jupiter.api.Test;

public class CrownTest{
    Crown crown = new Crown();
    Player player=new BotBuildFast(1);


    @Test
    public void goesToTest(){
        crown.goesTo(player);
        assertEquals(player, crown.getCrownOwner());
    }

}