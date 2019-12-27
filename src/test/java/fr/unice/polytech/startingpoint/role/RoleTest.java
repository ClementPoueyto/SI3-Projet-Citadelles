package fr.unice.polytech.startingpoint.role;

import fr.unice.polytech.startingpoint.board.Bank;
import fr.unice.polytech.startingpoint.board.Board;
import fr.unice.polytech.startingpoint.board.Deck;
import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.board.DistrictColor;
import fr.unice.polytech.startingpoint.game.DealRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import fr.unice.polytech.startingpoint.player.*;

import java.util.ArrayList;

public class RoleTest {
    Player player;
    Player target;
    Board board;
    Bank bank;
    Deck deck;
    DealRoles dealRoles;

    @BeforeEach
    void setUp() {
        player = new Player(1);
        target = new Player(2);
        deck = new Deck();
        bank=new Bank();
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        players.add(target);
        dealRoles = new DealRoles();
        board = new Board();
        board.setPlayers(players);
        bank.setBourses(players);
        players.forEach(p->p.setDealRoles(dealRoles));
    }

    

    @Test
    void murdererTest(){
        Role murderer = dealRoles.getRole("Murderer");
        Role merchant = dealRoles.getRole("Merchant");
        player.setCharacter(murderer);
        target.setCharacter(merchant);
        player.setTargetToKill(merchant);
        player.getCharacter().useSpecialPower();
        assertEquals(1,dealRoles.getRole("Murderer").getPosition());
        assertEquals(player,murderer.getPlayer());
        assertEquals(1,murderer.getNumberDistrictBuildable());
        assertEquals(2,murderer.getNumberGold());
        assertTrue( merchant.isMurdered());
    }

    @Test
    void thiefTest(){
        Role thief = dealRoles.getRole("Thief");
        Role merchant = dealRoles.getRole("Merchant");
        player.setCharacter(thief);
        target.setCharacter(merchant);
        target.setBoard(board);
        target.setDeck(deck);
        player.setDeck(deck);
        player.setTargetToRob(merchant);
        
        bank.withdraw(4, player);
        thief.useSpecialPower();
        target.playTurn();
        assertEquals(player,thief.getPlayer());
        assertEquals(2,thief.getPosition());
        assertEquals(player,thief.getPlayer());
        assertEquals(4,player.getGold());
        
    }

    @Test
    void wizardTest(){
        Role wizard = new Wizard();
        assertEquals(3,wizard.getPosition());
        
        
    }

    @Test
    void kingTest(){
        Role king = new King();
        assertEquals(4,king.getPosition());
    }

    @Test
    void bishopTest(){
        Role bishop = new Bishop();
        assertEquals(5,bishop.getPosition());
        assertNull(bishop.getPlayer());
        assertEquals(1,bishop.getNumberDistrictBuildable());
        assertEquals(2,bishop.getNumberGold());
    }

    @Test
    void merchantTest(){
        Role merchant = new Merchant();
        assertEquals(6,merchant.getPosition());
        assertNull(merchant.getPlayer());
        assertEquals(1,merchant.getNumberDistrictBuildable());
        assertEquals(3,merchant.getNumberGold());
    }

    @Test
    void architectTest(){
        Role architect = new Architect();
        assertEquals(7,architect.getPosition());
        assertNull(architect.getPlayer());
        assertEquals(3,architect.getNumberDistrictBuildable());
        assertEquals(2,architect.getNumberGold());
    }

    @Test
    void warlordTest(){
        Role warlord = new Warlord();
        assertEquals(8,warlord.getPosition());
        assertNull(warlord.getPlayer());
        assertEquals(1,warlord.getNumberDistrictBuildable());
        assertEquals(2,warlord.getNumberGold());
    }

    @Test
    void getNumberDistrictBibliothequeTest(){
        Role r=new Warlord();
        
        player.takeCoinsFromBank(8);
        player.setCharacter(r);

        assertEquals(2, r.getNumberDistrictPickable());
        assertEquals(1, r.getNumberDistrictKeepable());
        //Pour ne pas créer un board on récupère la city
        player.getCity().add(new District(2, 2, DistrictColor.Wonder, "Bibliotheque"));

        assertEquals(2, r.getNumberDistrictPickable());
        assertEquals(2, r.getNumberDistrictKeepable());
    }

    @Test
    void getNumberDistrictObservatoireTest(){
        Role r=new Warlord();
        player.takeCoinsFromBank(8);
        
        player.setCharacter(r);

        assertEquals(2, r.getNumberDistrictPickable());
        assertEquals(1, r.getNumberDistrictKeepable());
        //Pour ne pas créer un board on récupère la référence de city
        player.getCity().add(new District(2, 2, DistrictColor.Wonder, "Observatoire"));

        assertEquals(3, r.getNumberDistrictPickable());
        assertEquals(1, r.getNumberDistrictKeepable());

    }
}
