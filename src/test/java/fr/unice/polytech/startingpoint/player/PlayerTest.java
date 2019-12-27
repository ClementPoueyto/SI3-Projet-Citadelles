package fr.unice.polytech.startingpoint.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import fr.unice.polytech.startingpoint.board.Bank;
import fr.unice.polytech.startingpoint.board.Board;
import fr.unice.polytech.startingpoint.board.Deck;
import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.board.DistrictColor;
import fr.unice.polytech.startingpoint.game.DealRoles;
import fr.unice.polytech.startingpoint.role.Bishop;
import fr.unice.polytech.startingpoint.role.King;
import fr.unice.polytech.startingpoint.role.Merchant;
import fr.unice.polytech.startingpoint.role.Role;
import fr.unice.polytech.startingpoint.role.Warlord;
import fr.unice.polytech.startingpoint.role.Wizard;

class PlayerTest {

	Player player;
	Board board;
	Bank bank;
	Deck d;
	DealRoles dl;
	Player p2;
	
    @BeforeEach
    void setUp(){
        player = new Player(1);
        p2 = new Player(3);
		board = new Board();
		bank=new Bank();
		d=new Deck();
        dl = new DealRoles();
		//board.setDealRoles(dl);
		bank.setBourses(List.of(player,p2));
		player.setDeck(d);
		player.setDealRoles(dl);
        //player.setBoard(board);
    }
    
    @Test
    void testTakeCardsAtBeginning(){
    	assertEquals(0, player.getGold());
    	player.takeCardsAtBeginning();
    	assertEquals(4, player.getHand().size());
	}
    
    @Test
    void testPickNewDistrict(){
        assertEquals(0, player.getHand().size());
        player.pickNewDistrict(new District(3,3,"noblesse","Eglise"));
        assertEquals(1, player.getHand().size());
    }

    @Test
    void testAddToTheCity(){
        assertFalse( player.addToTheCity(new District(3,3,DistrictColor.Religion,"Eglise")));
        bank.withdraw(3, player);
        assertTrue(player.addToTheCity(new District(3,3,DistrictColor.Religion,"Eglise")));
        assertFalse( player.addToTheCity(new District(3,3,DistrictColor.Religion,"Eglise")));
    }
    
    @Test
    public void testAddMoney() {
		assertEquals(0, player.getGold());
		bank.withdraw(5, player);
    	assertEquals(5, player.getGold());
	}
    
    @Test
    void testDeleteDistrictFromCity(){
    	player.setBoard(new Board());
    	player.getBoard().setPlayers(player, p2);
    	
		assertEquals(0, player.getCity().getSizeOfCity());
		bank.withdraw(3, player);
    	player.addToTheCity(new District(3,3,DistrictColor.Religion,"Eglise"));
    	assertEquals(1, player.getCity().getSizeOfCity());
    	
    	
    	player.deleteDistrictFromCity(player.getCity().getListDistricts().get(0));
    	assertEquals(0, player.getCity().getSizeOfCity());
    	
    	bank.withdraw(3, player);
    	player.addToTheCity(new District(3,3,DistrictColor.Religion,"Eglise"));
    	player.setCharacter(new Bishop());
    	player.deleteDistrictFromCity(player.getCity().getListDistricts().get(0));
    	assertEquals(1, player.getCity().getSizeOfCity());
    	
    	bank.withdraw(3, player);
    	player.addToTheCity(new District(3,3,DistrictColor.Wonder,"Donjon"));
    	player.deleteDistrictFromCity(player.getCity().getListDistricts().get(0));
    	assertEquals(2, player.getCity().getSizeOfCity());
    	
    	player.setCharacter(new Merchant());
    	bank.withdraw(3, player);
    	player.addToTheCity(new District(3,3,DistrictColor.Wonder,"Donjon"));
    	player.deleteDistrictFromCity(player.getCity().getListDistricts().get(0));
    	assertEquals(1, player.getCity().getSizeOfCity());
	}
    
    @Test 
    void testDeletion(){
    	board = mock(Board.class);
    	p2 = mock(Player.class);
    	player.setBoard(board);
    	board.setPlayers(player, p2); 
    	District d = new District(0, 2, DistrictColor.Commerce, "quartier");
    	Mockito.verify(p2, Mockito.never()).isUsingGraveyard(d);
    	when(board.existsGraveyardPlayer()).thenReturn(p2);
    	player.deletion(d);
    	Mockito.verify(p2).isUsingGraveyard(d);
    }
    
    @Test
    void testTakeCoinsFromBank(){
    	assertEquals(0, player.getGold());
		player.takeCoinsFromBank(3);
		assertEquals(3, player.getGold());
		player.takeCoinsFromBank(-2);
		assertEquals(3, player.getGold());
	}
    
    @Test
	void testExchangeHands() {
    	Player p2 = new Player(3);
    	p2.pickNewDistrict(new District(3,3,"noblesse","Eglise"));
    	player.setTargetToExchangeHandWith(p2);
    	assertEquals(0, player.getHand().size());
    	assertEquals(1, p2.getHand().size());
    	player.exchangeHands();
    	assertEquals(1, player.getHand().size());
    	assertEquals(0, p2.getHand().size());
	}
    
    @Test
    void testSurrenderToThief(){
    	bank.withdraw(6, player);
    	
    	
    	Role r =dl.getRole("Thief");
    	r.setPlayer(p2);
    	
    	assertEquals(6, player.getGold());
    	assertEquals(0, r.getPlayer().getGold());
    	player.surrenderToThief();
    	assertEquals(0, player.getGold());
    	assertEquals(6, r.getPlayer().getGold());
    }

    @Test
    void testChooseRole(){   	
    	assertNull( player.getCharacter());
    	dl.readyToDistribute(3);
    	player.chooseRole();
    	assertNotNull(player.getCharacter());
    }
    
    @Test
    void testReInitializeForNextTurn(){
    	player.setCharacter(new Warlord());
		player.setTargetToDestroyDistrict(new Player(2));
		player.setTargetToExchangeHandWith(new Player(3));
		player.setTargetToKill(new Bishop());
		player.setTargetToRob(new Wizard());
		
		player.reInitializeForNextTurn();
		assertNull(player.getCharacter());
		assertNull(player.getTargetToDestroyDistrict());
		assertNull(player.getTargetToExchangeHandWith());
		assertNull(player.getTargetToKill());
		assertNull(player.getTargetToRob());
    }
    
    @Test
    void testCollectMoneyFromDistricts() {
    	player.setCharacter(new King());
    	bank.withdraw(5, player);
    	player.addToTheCity(new District(3,3,"noblesse","Chateau"));
    	
    	assertEquals(2, player.getGold());
    	player.collectMoneyFromDistricts();   	
    	assertEquals(3, player.getGold());
    }
    
    //Update when counting system will change
    @Test
    void testPoints() {
    	assertEquals(0, player.points());
    	bank.withdraw(20, player);//assez d'argent
    	player.addToTheCity(new District(3,3,"noblesse","Chateau"));
		assertEquals(3, player.points());
		
		player.addToTheCity(new District(2, 2, "merveille", "Poudlard"));
		player.addToTheCity(new District(2, 2, "soldatesque", "Caserne"));
		player.addToTheCity(new District(2, 2, "commerce", "Casino"));
		player.addToTheCity(new District(2, 2, "religion", "Eglise"));
		assertEquals(14, player.points());
	}
	@Test
	void testChekFinishBuilding(){
		Player p3=new Player(3);
		
		Player p2=new Player(7);
		Player p1=new Player(4);
		board.setPlayers(p3,p2,p1);
		p1.setBoard(board);
		
		assertTrue(p1.isFirstToFinish());
		assertFalse(p1.checkFinishBuilding());
	}

	@Test
	void testCheckFinishBuilding2(){
		Player p3=new Player(3);
		Player p2=new Player(7);
		Player p1=new Player(4);
		board.setPlayers(p3,p2,p1);
		p1.setBoard(board);
		City c= mock(City.class);
		when(c.getSizeOfCity()).thenReturn(9);
		p1.setCity(c);
		
		assertTrue(p1.checkFinishBuilding());
	}

	@Test
	void testIsFirstToFinish3(){
		Player p3=new Player(3);
		Player p2=new Player(7);
		Player p1=new Player(4);
		board.setPlayers(p3,p2,p1);
		p1.setBoard(board);
		City c= mock(City.class);
		when(c.getSizeOfCity()).thenReturn(7);
		p1.setCity(c);
		
		assertFalse(p1.checkFinishBuilding());
	}

	@Test
	void isUsingFabricTest() {
		assertFalse(player.isUsingFabric());

		player.setBoard(new Board());
		player.takeCoinsFromBank(5);
		assertFalse(player.isUsingFabric());
				
		player.hand.add(new District(6,6, DistrictColor.Wonder,"rue"));
		player.setHand(player.hand);
		assertFalse(player.isUsingFabric());
		
		City c = mock(City.class);
		when(c.getSizeOfCity()).thenReturn(8);
		player.setCity(c);
		assertFalse(player.isUsingFabric());
	}
	
	@Test
	void usesGraveyardTest() {
		assertEquals(0, player.hand.size());
		
		District ex = new District(2, 2, "merveille", "Poudlard");
		player.isUsingGraveyard(ex);
		assertEquals(1, player.hand.size());
	}
	
    
}
