package fr.unice.polytech.startingpoint.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import fr.unice.polytech.startingpoint.player.*;
import fr.unice.polytech.startingpoint.role.Role;

class ManagerTest {
	
	Manager manager = new Manager();
	Player p1;
    Player p2;
    Player p3;
	Player p4;
	Player p5;
	Player p6;
	Player p7;
	Player p8;

    
    ArrayList<Role> myRoles;

	@BeforeEach
	void setUp() throws Exception {
		p1 = new BotRnd(1);
		p2 = new BotRnd(2);
		p3 = new BotRnd(3);
		p4 = new BotRnd(4);
		p5 = new BotRnd(4);
		p6 = new BotRnd(6);
		p7 = new BotRnd(7);
		p8 = new BotRnd(8);
		
	}
	
	@Test
	void testLetsPlayAndOneRound() {
		Player[] players = {p1, p2, p3, p4};
		manager.letsPlay(players);
		for(Player p : players) {
			assertNotNull(p.getNextPlayer());
		}
	}

	@Test
	void testLetsPlayUnicityTest() {
		Player[] players = {p1, p2, p3,p4,p5};
		assertThrows(IllegalArgumentException.class, ()->{
			manager.letsPlay(players);
		});
	}

	@Test
	void testLetsPlayNbTest() {
		Player[] players = {p1, p2};
		assertThrows(IllegalArgumentException.class, ()->{
			manager.letsPlay(players);
		});
	}

	@Test
	void testLetsPlayNb2Test() {
		Player[] players = {p1, p2,p3,p4,p5,p6,p7,p8};
		assertThrows(IllegalArgumentException.class, ()->{
			manager.letsPlay(players);
		});
	}

	@Test
	void testEndGame() {
		Player[] players = {p1, p2, p3, p4};
		manager.endGame(players);
		assertEquals(4, manager.getWinner().size());
	}
	@Test
	void testPoints() {
		Player p=mock(Player.class);
		when(p.points()).thenReturn(50);
		Player[] players = {p,p1, p2, p3, p4};
		manager.endGame(players);
		assertTrue( manager.getWinner().contains(p));
	}
}
