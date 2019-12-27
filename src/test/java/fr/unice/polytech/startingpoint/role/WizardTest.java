package fr.unice.polytech.startingpoint.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.unice.polytech.startingpoint.board.Bank;
import fr.unice.polytech.startingpoint.board.Board;
import fr.unice.polytech.startingpoint.board.Deck;
import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.game.DealRoles;
import fr.unice.polytech.startingpoint.player.Player;

public class WizardTest {
    Player player;
    Player target;
    Board board;
    DealRoles dealRoles;
    Deck deck;
    Bank bank;

    @BeforeEach
    void setUp(){
        player=new Player(1){
            @Override
            public List<District> getCardsToExchange() {
                ArrayList<District> cardsToExchange = new ArrayList<District>(player.getHand().toList());
                cardsToExchange.remove(0);
                cardsToExchange.remove(0);
                return cardsToExchange;
            }
        };
        target = new Player(2);
        deck=new Deck();
        bank=new Bank();
        dealRoles = new DealRoles();
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        players.add(target);
        
        board = new Board();
        
        player.setBoard(board);
        target.setBoard(board);
        players.forEach(p->p.setDealRoles(dealRoles));
        players.forEach(p->p.setDeck(deck));
    }


    @Test
    void exchangeWithPlayerTest(){
        Role wizard = dealRoles.getRole("Wizard");
        Role merchant =dealRoles.getRole("Merchant");
        player.setCharacter(wizard);
        target.setCharacter(merchant);
        target.takeCardsAtBeginning();
        player.setTargetToExchangeHandWith(target);
        wizard.useSpecialPower();
        assertEquals(3,wizard.getPosition());
        assertTrue(target.getHand().isEmpty());
        assertFalse( player.getHand().isEmpty());
    }

    @Test
    void exchangeWithDeckTest(){
        Role wizard = dealRoles.getRole("Wizard");
        player.setCharacter(wizard);
        player.takeCardsAtBeginning();
        ArrayList<District> hand = new ArrayList<District>(player.getHand().toList());
        
        
        wizard.useSpecialPower();
        assertTrue(player.getHand().toList().contains(hand.get(0)));
        assertTrue(player.getHand().toList().contains(hand.get(1)));
        assertNotSame(hand.get(2),player.getHand().toList().get(2));
        assertNotSame(hand.get(3),player.getHand().toList().get(3));
    }
}
