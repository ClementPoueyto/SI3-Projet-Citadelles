package fr.unice.polytech.startingpoint.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.unice.polytech.startingpoint.player.BotRnd;
import fr.unice.polytech.startingpoint.player.Player;
import fr.unice.polytech.startingpoint.role.Role;

class DealRolesTest {
	
	Player p1 = new BotRnd(1);
    Player p2 = new BotRnd(2);
    Player p3 = new BotRnd(3);
    ArrayList<Player> players = new ArrayList<Player>(Arrays.asList(p1, p2, p3));
	DealRoles dealer;
	ArrayList<Role> myRoles;
	
	@BeforeEach
	void setUp() throws Exception {
		dealer = new DealRoles();
		myRoles = dealer.getRoles();
	}
	
	@Test
	void testReadyToDistribute() {
		for (Player p : players) {
            p.reInitializeForNextTurn();
        }
		dealer.readyToDistribute(3);
		assertEquals(dealer.getRoles().size(), dealer.getLeftRoles().size()+dealer.getVisible().size()+1);
		assertEquals(2, dealer.getVisible().size());
	}
	
	
	@Test
	void reInitializeRoles(){
		for(Role r : dealer.getRoles()) {
			r.setPlayer(new Player(6){
				@Override
				public boolean coinsOrDistrict() {
					return false;
				}
				
			});
		}
		for(Role r : dealer.getRoles()) {
			assertNotNull(r.getPlayer());
		}
		dealer.reInitializeRoles();
		for(Role r : dealer.getRoles()) {
			assertNull(r.getPlayer());
		}
	}
}
