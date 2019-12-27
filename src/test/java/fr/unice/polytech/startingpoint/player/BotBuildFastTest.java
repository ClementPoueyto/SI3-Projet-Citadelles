package fr.unice.polytech.startingpoint.player;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.unice.polytech.startingpoint.board.Bank;
import fr.unice.polytech.startingpoint.board.Board;
import fr.unice.polytech.startingpoint.board.Deck;
import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.board.DistrictColor;
import fr.unice.polytech.startingpoint.game.DealRoles;
import fr.unice.polytech.startingpoint.role.Architect;
import fr.unice.polytech.startingpoint.role.Merchant;
import fr.unice.polytech.startingpoint.role.Role;
import fr.unice.polytech.startingpoint.role.Thief;
import fr.unice.polytech.startingpoint.role.Warlord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BotBuildFastTest {

	BotBuildFast bot, anotherBot;
	District d1 = new District(3, 4, DistrictColor.Religion, "quartier");
	District d2 = new District(6, 6, DistrictColor.Wonder, "rue");
	Hand hand;
	Bank bank;
	Deck deck;

	MatchingProb matches;
	DealRoles dealRoles;
	Board board;


	@BeforeEach
	void setup() {
		bot = new BotBuildFast(1){
			@Override
			public void attributeProbsToPlayer() {
				
			}
		};
		hand = new Hand();
		bank = new Bank();
		deck = new Deck();
		board= new Board();
		dealRoles = new DealRoles();

		bot.setDeck(deck);
		bot.setBoard(board);
		bank.setBourses(List.of(bot));
	}

	@Test
	void processChooseRoleTest1(){
		var merchant=new Merchant();
		var toConsiderRoles=List.of(
			new Thief(),
			merchant,
			new Warlord()
		);

		assertEquals(merchant, bot.processChooseRole(toConsiderRoles));
	}

	@Test
	void processChooseRoleTest2(){
		var merchant=new Merchant();
		var architect=new Architect();
		var toConsiderRoles=List.of(
			new Thief(),
			merchant,
			new Warlord(),
			architect
		);

		assertEquals(architect, bot.processChooseRole(toConsiderRoles));
	}

	@Test
	void coinsOrDistrictTest() {
		assertTrue(bot.coinsOrDistrict());
		bank.withdraw(6, bot);
		assertFalse(bot.coinsOrDistrict());
	}

	@Test
	void discardTest() {
		ArrayList<District> dis = new ArrayList<>();
		District d1 = new District(5, 3, "religion", "quartier1");
		District d2 = new District(6, 6, "religion", "quartier2");
		District d3 = new District(2, 4, "religion", "quartier3");
		dis.add(d1);
		dis.add(d2);
		dis.add(d3);

		bot.setBoard(new Board());
		assertEquals(3, dis.size());

		bot.setCharacter(new Merchant());
		bot.discard(dis);
		assertEquals(1, dis.size());
		assertEquals(2, dis.get(0).getCost());

		dis.clear();
		d1 = new District(8, 3, "religion", "quartier1");
		d2 = new District(6, 6, "religion", "quartier2");
		d3 = new District(5, 4, "religion", "quartier3");
		dis.add(d1);
		dis.add(d2);
		dis.add(d3);

		assertEquals(3, dis.size());
		bot.discard(dis);
		assertEquals(1, dis.size());
		assertTrue(dis.contains(d3));

		dis.clear();
		d1 = new District(3, 3, "religion", "quartier1");
		d2 = new District(1, 6, "religion", "quartier2");
		d3 = new District(2, 4, "religion", "quartier3");
		dis.add(d1);
		dis.add(d2);
		dis.add(d3);

		assertEquals(3, dis.size());
		bot.discard(dis);
		assertEquals(1, dis.size());
		assertTrue(dis.contains(d2));

		dis.clear();
		d1 = new District(5, 3, "religion", "quartier1");
		d2 = new District(1, 6, "religion", "quartier2");
		d3 = new District(2, 4, "religion", "quartier3");
		dis.add(d1);
		dis.add(d2);
		dis.add(d3);

		assertEquals(3, dis.size());
		bot.discard(dis);
		assertEquals(1, dis.size());
		assertTrue(dis.contains(d2));

		dis.clear();
		d1 = new District(5, 3, "religion", "quartier1");
		d2 = new District(1, 6, "religion", "quartier2");
		dis.add(d1);
		dis.add(d2);

		assertEquals(2, dis.size());
		bot.discard(dis);
		assertEquals(1, dis.size());
		assertTrue(dis.contains(d2));
	}


	@Test
	void isBuildingTest() {
		Role role = mock(Role.class);
		bot.setCharacter(role);
		when(role.toString()).thenReturn("Wizard");
		assertFalse(bot.isBuildingFirst());
		hand.add(d1);
		hand.add(d2);
		bot.setHand(hand);
		System.out.println(bot.getGold() + "	" +
				bot.getHand().badCards(bot.getGold()).size());
		assertFalse(bot.isBuildingFirst());

		when(role.toString()).thenReturn("Warlord");
		assertTrue(bot.isBuildingFirst());

		when(role.toString()).thenReturn("Bishop");
		assertTrue(bot.isBuildingFirst());

	}

	@Test
	void wantsToUseFabric() {
		assertFalse(bot.wantsToUseFabric());

		bot.takeCoinsFromBank(6);
		assertTrue(bot.wantsToUseFabric());

		City c = mock(City.class);
		when(c.getSizeOfCity()).thenReturn(8);
		bot.setCity(c);

		assertFalse(bot.wantsToUseFabric());
	}

	@Test
	void isUsingLaboTest() {
		assertEquals(Optional.empty(), bot.wantsToUseLabo());
		City c = mock(City.class);
		when(c.containsWonder("Laboratoire")).thenReturn(true);
		bot.setCity(c);

		bot.setHand(hand);
		hand.add(d1);
		hand.add(d2);
		hand.add(new District(8, 10, DistrictColor.Commerce, "rue"));
		bot.setBoard(new Board());
		bot.takeCoinsFromBank(5);
		assertFalse(hand.cardsAboveGold(bot.getGold()).isEmpty());

		when(c.getSizeOfCity()).thenReturn(8);
		District tmpDis = hand.highCostDistrict(bot.getGold());
		assertFalse(hand.cardsAboveGold(bot.getGold()).contains(tmpDis));

		int tmpDeckNb = bot.getDeck().numberOfCards();
		int tmpGold = bot.getGold();
		int tmpHandSize = bot.getHand().size();
		bot.isUsingLabo();

		assertEquals(tmpDeckNb + 1, bot.getDeck().numberOfCards());
		assertEquals(tmpGold + 1, bot.getGold());
		assertEquals(tmpHandSize - 1, bot.getHand().size());
	}

	void setMultiPlayers() {
		anotherBot = new BotBuildFast(2);
		bot.city.add(d1);

		Board b = new Board();
		anotherBot.setBoard(b);
		b.setPlayers(bot, anotherBot);
	}

	@Test
	void wantsToUseGraveyardTest() {
		setMultiPlayers();
		bot.getBoard().setPlayers(bot, anotherBot);
		anotherBot.setCharacter(new Warlord());
		assertFalse(anotherBot.wantsToUseGraveyard(d1));
		anotherBot.setBank(new Bank());
		anotherBot.getBank().setBourses(List.of(bot, anotherBot));
		anotherBot.takeCoinsFromBank(5);
		anotherBot.setCharacter(new Merchant());

		assertTrue(anotherBot.wantsToUseGraveyard(d1));
		anotherBot.wantsToUseGraveyard(d1);

		bot.city.add(d2);
		bot.deleteDistrictFromCity(d2);
		anotherBot.takeCoinsFromBank(3);

		assertTrue(anotherBot.wantsToUseGraveyard(d2));
	}

	@Test
	void processWhoToKillTest() {
		dealRoles = new DealRoles();
		bot.setDealRoles(dealRoles);
		bot.setCharacter(new Merchant());
		Player p = mock(Player.class);
		when(p.getId()).thenReturn(1);
		Board board = mock(Board.class);
		when(board.playerWithTheBiggestCity(bot)).thenReturn(p);
		bot.setBoard(board);
		Set<String> s = new HashSet<String>();
		s.add("Thief");
		matches = mock(MatchingProb.class);
		when(matches.possibleRolesFor(1)).thenReturn(s);
		bot.setMatches(matches);
		assertEquals(dealRoles.getRole("Thief"), bot.processWhoToKill());
	}

	@Test
	void processWhoToRobTest() {
		dealRoles = new DealRoles();
		bot.setDealRoles(dealRoles);
		bot.setCharacter(new Thief());
		Player p = mock(Player.class);
		when(p.getId()).thenReturn(1);
		Board board = mock(Board.class);
		when(board.richestPlayer(bot)).thenReturn(p);
		bot.setBoard(board);
		Set<String> s = new HashSet<String>();
		s.add("Merchant");
		matches = mock(MatchingProb.class);
		when(matches.possibleRolesFor(1)).thenReturn(s);
		bot.setMatches(matches);
		assertEquals(dealRoles.getRole("Merchant"), bot.processWhoToRob());
	}

	

	@Test
	public void roleToOptimizeCoinsTest(){
		setMultiPlayers();
		BotBuildFast botMock = mock(BotBuildFast.class);
		assertEquals(Optional.empty(),botMock.roleToOptimizeCoins(dealRoles.getRoles()));

		//voir beforeEach
		assertEquals("Bishop",bot.roleToOptimizeCoins(dealRoles.getRoles()).get().toString());
	}

}