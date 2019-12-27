package fr.unice.polytech.startingpoint.role;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.unice.polytech.startingpoint.board.Bank;
import fr.unice.polytech.startingpoint.board.Deck;
import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.game.DealRoles;
import fr.unice.polytech.startingpoint.player.Player;

public class WarlordTest {
    Player player;
    Player target;
    DealRoles dealRoles;
    Deck deck;
    Bank bank;
    District d;

    @BeforeEach
    void setUp(){
        target=new Player(1);
        player=new Player(0){
            @Override
            public District processDistrictToDestroy(Player target){
                return d;
            }
            @Override
            public Player processWhoseDistrictToDestroy() {
                return target;
            }
        };
        target=new Player(2);
        dealRoles = new DealRoles();
        deck=new Deck();
        bank=new Bank();
        ArrayList<Player> players = new ArrayList<>();
        
        players.add(player);
        players.add(target);
        bank.setBourses(players);
        players.forEach(p->p.setDealRoles(dealRoles));
        players.forEach(p->p.setDeck(deck));
        d=deck.withdraw();
    }

    @Test
    void warlordTest(){
        Role warlord = dealRoles.getRole("Warlord");
        player.setCharacter(warlord);
        player.takeCoinsFromBank(10);
        target.takeCoinsFromBank(10);
        
        target.addToTheCity(d);
        ArrayList<District> city = target.getCity().getListDistricts();
        
        
        player.specialMove();
        assertEquals(8,warlord.getPosition());
        assertEquals(player,warlord.getPlayer());
        assertEquals(1,warlord.getNumberDistrictBuildable());
        assertEquals(0,city.size());
        assertEquals(0,target.getCity().getListDistricts().size());
    }
}
