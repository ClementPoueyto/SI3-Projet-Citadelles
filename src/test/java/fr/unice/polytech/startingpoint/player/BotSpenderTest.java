package fr.unice.polytech.startingpoint.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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


class BotSpenderTest {


    BotSpender bot, anotherBot;
    District d1 = new District(3,4,DistrictColor.Religion, "quartier");
    District d2 = new District(6,6, DistrictColor.Wonder,"rue");
    Hand hand;
    Bank bank;
    Deck deck;

    @BeforeEach
    void setup() {
        deck=new Deck();
        bank=new Bank();
        hand = new Hand();
        bot = new BotSpender(1);
        bank.setBourses(List.of(bot));
        bot.setDeck(deck);
        hand.add(d1);
        hand.add(d2);
    }

    @Test
    void coinsOrDistrictTest() {
        assertTrue(bot.coinsOrDistrict());

        bank.withdraw(10,bot);
        bot.setBoard(new Board());
        ArrayList<District> badCards = new ArrayList<>();
        badCards.add(new District(1, 1, "religion", "Temple"));
        badCards.add(new District(1, 1, "religion", "Temple"));
        Hand h=mock(Hand.class);
        when(h.badCards(bot.getGold())).thenReturn(badCards);
        when(h.size()).thenReturn(2);
        bot.setHand(h);
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
        bot.takeCoinsFromBank(4);
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
        assertTrue(dis.contains(d1));

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
    void whatToBuild(){
        Role role = mock(Role.class);
        when(role.toString()).thenReturn("Architect");
        bot.setCharacter(role);
        hand.add(d1);
        hand.add(d2);
        bot.setHand(hand);
        assertEquals(d2,bot.whatToBuild(10));

        when(role.toString()).thenReturn("Warlord");
        assertEquals(d1,bot.whatToBuild(4));

        assertNull(bot.whatToBuild(2));
    }

    
    @Test
    void isBuildingFirstTest(){
        Role role = mock(Role.class);
        bot.setCharacter(role);
        Hand handTest = new Hand();
        bot.setHand(handTest);
        when(role.toString()).thenReturn("Wizard");
        assertTrue(bot.isBuildingFirst());

        bot.setHand(hand);
        when(role.toString()).thenReturn("Wizard");
        assertFalse(bot.isBuildingFirst());

        when(role.toString()).thenReturn("Architect");
        bot.takeCoinsFromBank(10);
        assertFalse(bot.isBuildingFirst());

        when(role.toString()).thenReturn("Bishop");
        assertTrue(bot.isBuildingFirst());

    }
    
	@Test
	void wantsToUseFabric() {
		assertFalse(bot.wantsToUseFabric());
		
		bot.takeCoinsFromBank(9);
		assertTrue(bot.wantsToUseFabric());
		
		Hand h = mock(Hand.class);
		when(h.highValuedDistrict(bot.getGold()-3)).thenReturn(true);
		bot.setHand(h);
		
		assertFalse(bot.wantsToUseFabric());
	}
    
    @Test
	void wantsToUseLaboTest() {
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
		District tmpDis = hand.lowCostDistrict();
		assertFalse(hand.cardsAboveGold(bot.getGold()).contains(tmpDis));
		
		int tmpDeckNb = bot.getDeck().numberOfCards();
		int tmpGold = bot.getGold();
		int tmpHandSize = bot.getHand().size();
		bot.isUsingLabo();
		
		assertEquals(tmpDeckNb + 1, bot.getDeck().numberOfCards());
		assertEquals(tmpGold + 1, bot.getGold());
		assertEquals(tmpHandSize - 1, bot.getHand().size());
		
		bot.reInitializeForNextTurn();
		bot.bank.deposit(5,bot);
		hand.add(new District(8, 5, DistrictColor.Noble, "pas picked"));
		bot.isUsingLabo();
		
		assertEquals(tmpDeckNb + 1, bot.getDeck().numberOfCards());
		assertEquals(tmpGold - 4, bot.getGold());
		assertEquals(tmpHandSize, bot.getHand().size());
	}
    
    @BeforeEach
	void setMultiPlayers() {
		anotherBot = new BotSpender(2);
		bot.city.add(d2);
		
		Board b = new Board();
		anotherBot.setBoard(b);
		b.setPlayers(bot, anotherBot);
	}
	
	@Test
	void wantsToUseGraveyardTest(){	
		anotherBot.setCharacter(new Warlord());
        assertFalse(bot.deleteDistrictFromCity(d1));
        Bank bank2=new Bank();
        bank2.setBourses(anotherBot);
		anotherBot.getBank().setBourses(List.of(bot,anotherBot));
		anotherBot.takeCoinsFromBank(7);
		anotherBot.setCharacter(new Merchant());
		
		assertTrue(anotherBot.wantsToUseGraveyard(d2));
		anotherBot.wantsToUseGraveyard(d2);
		
		bot.city.add(d1);
		bot.deleteDistrictFromCity(d1);
		anotherBot.takeCoinsFromBank(3);

		assertFalse(anotherBot.wantsToUseGraveyard(d1));
    }
}