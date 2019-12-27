package fr.unice.polytech.startingpoint.player;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.unice.polytech.startingpoint.board.Bank;
import fr.unice.polytech.startingpoint.board.Board;
import fr.unice.polytech.startingpoint.board.Deck;
import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.board.DistrictColor;
import fr.unice.polytech.startingpoint.game.DealRoles;
import fr.unice.polytech.startingpoint.role.Warlord;

class BotRndTest {

	BotRnd b1, b2, b3, b4;
	Board board;
	Bank bank;
	Deck deck;
	
	@BeforeEach
	void setUp() throws Exception {
		b1 = new BotRnd(1);
		b2 = new BotRnd(2);
		b3 = new BotRnd(3);
		b4 = new BotRnd(4);
		board = new Board();
		bank=new Bank();
		deck=new Deck();
		board.setPlayers(new ArrayList<Player>(Arrays.asList(b1, b2, b3, b4)));
		b1.setDealRoles(new DealRoles());
		bank.setBourses(List.of(b1,b2,b3,b4));
		b1.setBoard(board);
		b1.setDeck(deck);
		b1.setCharacter(new Warlord());
	}

	@Test
	void testSpecialMove() {
		assertNull(b1.targetToExchangeHandWith);
		assertNull(b1.targetToKill);
		assertNull(b1.targetToRob);
		b1.specialMove();
		assertNotNull(b1.targetToExchangeHandWith);
		assertNotNull(b1.targetToKill);
		assertNotNull(b1.targetToRob);
	}
	
	@Test
	void testbuilding() {
		bank.withdraw(10, b1);
		b1.pickNewDistrict(new District(3, 2, DistrictColor.Religion, "Église"));
		b1.pickNewDistrict(new District(2, 2, DistrictColor.Noble, "Chateau"));
		b1.pickNewDistrict(new District(6, 2, DistrictColor.Warlord, "Pont"));
		assertTrue(b1.city.isEmpty());
		b1.building();
		assertEquals(1, b1.city.getSizeOfCity());
		b1.building();
		assertEquals(2, b1.city.getSizeOfCity());
		b1.building();
		assertEquals(2, b1.city.getSizeOfCity());
	}
}
