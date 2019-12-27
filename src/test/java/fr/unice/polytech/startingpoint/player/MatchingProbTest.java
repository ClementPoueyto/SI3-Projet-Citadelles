package fr.unice.polytech.startingpoint.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MatchingProbTest {
    Player p1 ;
            Player p2;
            Player p3;
            Player p4;
    MatchingProb mprob;
        
    @BeforeEach
    void setUp(){
        p1 = new BotRnd(1);
        p2 = new BotRnd(2);
        p3 = new BotBuildFast(3);
        p4 = new BotSpender(4);
        mprob=new MatchingProb(List.of(p1,p2,p3,p4));

    }

    @Test
    void getSetProbabilityTest(){
        assertEquals(-1.0, mprob.getProbability(1, "Murderer"));
        assertEquals(-1.0, mprob.getProbability(1, "Warlord"));

        mprob.setProbability(3, "Thief", 0.0);

        assertEquals(-1.0, mprob.getProbability(3, "Murderer"));

        assertEquals(0.0, mprob.getProbability(3, "Thief"));

    }

    @Test
    void possibleRolesForTest(){
        mprob.setProbability(3, "Thief", 0.0);

        Set<String> possibleRoles=mprob.possibleRolesFor(3);

        assertFalse( possibleRoles.contains("Thief"));
    }


}