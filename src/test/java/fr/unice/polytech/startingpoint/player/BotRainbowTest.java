package fr.unice.polytech.startingpoint.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import fr.unice.polytech.startingpoint.role.*;
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
import fr.unice.polytech.startingpoint.role.Thief;
import fr.unice.polytech.startingpoint.role.Warlord;

class BotRainbowTest {

	BotRainbow bot, anotherBot;
    District d1 = new District(3,4,DistrictColor.Religion, "quartier");
    District d2 = new District(6,6, DistrictColor.Wonder,"rue");
	Hand hand;
	Bank bank;
	Deck deck;
	
	MatchingProb matches;
	DealRoles dealRoles;


    @BeforeEach
	void setup(){
		bot=new BotRainbow(1);
		hand=new Hand();
		bank=new Bank();
		deck=new Deck();
		dealRoles=new DealRoles();

		bot.setDeck(deck);
		bank.setBourses(List.of(bot));
	}
	@Test
	void coinsOrDistrictsTest() {
		assertTrue(bot.coinsOrDistrict());

		bank.withdraw(9,bot);
		assertTrue(bot.coinsOrDistrict());
		
        bot.setBoard(new Board());
        ArrayList<District> badCards = new ArrayList<>();
        badCards.add(new District(1, 1, "religion", "Temple"));
        badCards.add(new District(1, 1, "religion", "Temple"));
        Hand h=mock(Hand.class);
        when(h.badCards(bot.getGold())).thenReturn(badCards);
        when(h.size()).thenReturn(2);
        bot.setHand(h);
		assertTrue(bot.coinsOrDistrict());
		
		ArrayList<DistrictColor> missingC = new ArrayList<>(Arrays.asList(DistrictColor.Religion));
		bot = mock(BotRainbow.class);
		when(bot.missingColors()).thenReturn(missingC);
		assertFalse(bot.coinsOrDistrict());
	}

	@Test
	void processChooseRoleTest(){
		ArrayList<Role> roles = new ArrayList<>();
		roles.add(new Merchant());
		roles.add(new Warlord());

		assertEquals("Merchant",bot.processChooseRole(roles).toString());
		bot.setHand(hand);
		roles.add(new Bishop());
		hand.clearEverything();
		hand.add(new District(2,2,"religion","test"));
		assertEquals("Bishop",bot.processChooseRole(roles).toString());
		hand.clearEverything();
		hand.add(new District(2,2,"noblesse","noblesse"));
		roles.add(new King());
		assertEquals("King",bot.processChooseRole(roles).toString());
		hand.clearEverything();
		hand.add(new District(2,2,"soldatesque","soldatesque"));
		assertEquals("Warlord",bot.processChooseRole(roles).toString());
		roles.add(new Architect());
		assertEquals("Architect",bot.processChooseRole(roles).toString());
	}


	@Test
	void buildNewColorsTest(){
		bot.getCity().clearEverything();
		assertEquals(0, bot.getCity().getSizeOfCity());
		
		bot.getCity().add(
			new District(2, 2, DistrictColor.Commerce, "Carrefour"));
		bot.getCity().add(
			new District(2, 2, DistrictColor.Warlord, "PorteAvions"));

		District cathedral=new District(4, 4, DistrictColor.Religion, "Cathedrale");
		bot.getHand().add(
			cathedral
		);
		bot.getHand().add(
			new District(4, 4, DistrictColor.Commerce, "Casino")
		);

		assertEquals(1,bot.buildNewColors(8).size());
		assertTrue(bot.buildNewColors(8).contains(cathedral));

		
	}

	@Test
	void processWTBTest(){
		bot.setCharacter(new Architect());
		bot.getCity().clearEverything();
		assertEquals(0, bot.getCity().getSizeOfCity());
		
		bot.getCity().add(
			new District(2, 2, DistrictColor.Commerce, "Carrefour"));
		bot.getCity().add(
			new District(2, 2, DistrictColor.Warlord, "PorteAvions"));

		District cathedral=new District(4, 4, DistrictColor.Religion, "Cathedrale");
		bot.getHand().add(
			cathedral
		);
		bot.getHand().add(
			new District(3, 3, DistrictColor.Commerce, "Casino")
		);

		bot.takeCoinsFromBank(2);
		assertEquals(0,bot.processWhatToBuild().size());
		assertFalse(bot.processWhatToBuild().contains(cathedral));
		
		bot.takeCoinsFromBank(2);
		assertEquals(1,bot.processWhatToBuild().size());
		assertTrue(bot.processWhatToBuild().contains(cathedral));
	}

    @Test
    void wantsToUseFabric() {
    	assertTrue(bot.wantsToUseFabric());
    	
    	City c = mock(City.class);
		when(c.containsAllColors()).thenReturn(true);
		bot.setCity(c);
    	assertFalse(bot.wantsToUseFabric());
    
    	bot.takeCoinsFromBank(9);
    	assertTrue(bot.wantsToUseFabric());
    	
    	Hand h = mock(Hand.class);
    	when(h.highValuedDistrict(bot.getGold()-3)).thenReturn(true);
    	bot.setHand(h);
    	assertFalse(bot.wantsToUseFabric());
    }
    
	@Test
	void wantToUseLaboTest() {
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
		
		bot.reInitializeForNextTurn();
		hand.add(new District(5, 7, DistrictColor.Commerce, "doublon"));
		bot.isUsingLabo();
		
		assertEquals(tmpDeckNb + 2, bot.getDeck().numberOfCards());
		assertEquals(tmpGold + 2, bot.getGold());
		assertEquals(tmpHandSize - 1, bot.getHand().size());
		
		bot.reInitializeForNextTurn();
		bot.bank.deposit(5,bot);
		hand.add(new District(8, 5, DistrictColor.Noble, "pas picked"));
		bot.isUsingLabo();
		
		assertEquals(tmpDeckNb + 2, bot.getDeck().numberOfCards());
		assertEquals(tmpGold - 3, bot.getGold());
		assertEquals(tmpHandSize, bot.getHand().size());
	}
	
	void setMultiPlayers() {
		anotherBot = new BotRainbow(2);
		bot.city.add(d1);
		
		Board b = new Board();
		anotherBot.setBoard(b);
		b.setPlayers(bot, anotherBot);
	}
	
	@Test
	void wantsToUseGraveyardTest(){
		setMultiPlayers();
		anotherBot.setCharacter(new Warlord());
		assertFalse(anotherBot.wantsToUseGraveyard(d1));
		Bank bank=new Bank();
		bank.setBourses(anotherBot);
		anotherBot.takeCoinsFromBank(5);
		anotherBot.setCharacter(new Merchant());
		
		assertTrue(anotherBot.wantsToUseGraveyard(d1));
		anotherBot.isUsingGraveyard(d1);

		bot.city.add(d2);
		bot.deleteDistrictFromCity(d2);
		anotherBot.takeCoinsFromBank(3);
		
		assertTrue(anotherBot.wantsToUseGraveyard(d2));
		
		anotherBot.hand.add(new District(2, 2, DistrictColor.Noble, "un quartier"));
		District tmp = new District(1, 1, DistrictColor.Noble, "un autre quartier");
		bot.city.add(tmp);
		
		anotherBot.takeCoinsFromBank(2);
		bot.deleteDistrictFromCity(tmp);
		
		assertFalse(anotherBot.wantsToUseGraveyard(tmp));
	}

	@Test
	void discardTest(){
		bot.setCharacter(new Thief());
		bot.takeCoinsFromBank(5);
		ArrayList<District> cards = new ArrayList<>();

		bot.getCity().add(
			new District(1, 1, DistrictColor.Warlord, "Tour de guet"));
		bot.getCity().add(
			new District(1, 1, DistrictColor.Wonder, "Tower of Essentil"));
		

		
		cards.add(new District(1, 1, DistrictColor.Commerce, "Taverne"));
		cards.add(new District(1, 1, DistrictColor.Warlord, "PorteAvions"));
		var expect =new District(1, 1, DistrictColor.Commerce, "Taverne");
		bot.discard(cards);
		assertEquals(expect, cards.get(0));
	}

	

}
