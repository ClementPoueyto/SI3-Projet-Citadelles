package fr.unice.polytech.startingpoint.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.board.DistrictColor;
import fr.unice.polytech.startingpoint.role.Role;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class BotSmart extends Player {
    

    public BotSmart(int id) {
        super(id);
    }

    /**
	 * Fonction pour récupérer le Role permettant d'avoir le plus d'argent lors de
	 * la collecte d'argent des quartiers On utilisera hidden que si le joueur est
	 * le dernier à choisir son role ie nextPlayer.alreadyChosenRole==true
	 */
    public Optional<Role> roleToOptimizeCoins(List<Role> toConsider) {

        if (city.getSizeOfCity() == 0) {
            // une autre
            return Optional.empty();
        } else {
            ArrayList<String> availableColors = new ArrayList<>();
            toConsider.stream().map(d -> d.getColor()).forEach(s -> {
                if (s.equals("soldatesque") || s.equals("commerce") || s.equals("religion") || s.equals("noblesse")) {
                    availableColors.add(s);
                }
            });
            String bestColor = this.city.mostPotentiallyPayingColor(availableColors);

            for (Role r : toConsider) {
                if (r.getColor().equals(bestColor)) {
                    return Optional.of(r);
                }
            }
            return Optional.empty();
        }
    }

    /**
     * 
     * @param roles
     * @param color
     * @return détermine le rôle à choisir par le bot parmi la liste de rôles.
     */
    Role bestRoleToChoose(List<Role> roles, String color){
        Optional<Role> optWizard=roles.stream().filter(r->r.toString().equals("Architect")).findAny();
        if(optWizard.isPresent()){
            return optWizard.get();
        }
        optWizard=roles.stream().filter(r->r.toString().equals("Thief")).findAny();
        if(optWizard.isPresent()){
            return optWizard.get();
        }
        optWizard=roles.stream().filter(r->r.toString().equals("Wizard")).findAny();
        if(hand.badCards(getGold()).size()>hand.size()/2&& optWizard.isPresent()){
            return optWizard.get();
        }
        int position;
        switch (color){
            case "religion": position=5;
                break;
            case "soldatesque": position =8;
                break;
            case "noblesse": position =4;
                break;
            default : position =6;
        }
        for(Role role : roles){
            if(role.getPosition()==position){
                return role;
            }
        }
        return roles.get(0);
        }

    

	public TupleIntDistrictsList buildables(List<District> toConsider,int limit){
        if(toConsider.isEmpty() || limit<=0){
            return new TupleIntDistrictsList(0,new ArrayList<District>());
        }
        else if(toConsider.get(0).getCost()>limit){
            //Explore right branch only
            return buildables(toConsider.subList(1,toConsider.size()), limit);
        }
        else{
            District nextItem=toConsider.get(0);
            //Explore left branch
            TupleIntDistrictsList resultLeft=buildables(toConsider.subList(1, toConsider.size()), limit-nextItem.getCost());
            resultLeft.addVal(nextItem.getValue());
            //Explore right branch
            TupleIntDistrictsList resultRight=buildables(toConsider.subList(1, toConsider.size()), limit);

            if(resultLeft.getVal()>resultRight.getVal()){
                resultLeft.getDistricts().add(nextItem);
                
                return resultLeft;
            }
            else{
                return resultRight;
            }
        }
    }

    /* -------------OVERRIDING ------------------*/

    /**
     * Défini les districts de la main à échanger avec le pouvoir du magicien.
     */
    @Override
    public List<District> processCardsToExchange() {
        return this.hand.badCards(this.getGold());
    }

    /**
     * Défini le joueur à cibler quand le bot joue la magicien.
     */
    @Override
    public Player processWhoToExchangeHandWith() {
        if((this.hand.size()-this.hand.badCards(this.getGold()).size())<=(this.hand.size()/2)){
            return null;
        }
        if(this.getBoard().playerWithTheBiggestHand(this).getHand().size()>=this.hand.size()){
            return this.getBoard().playerWithTheBiggestHand(this);
        }
        return null;
    }

    /**
     * Défini la cible quand le bot joue la condottière.
     */
    @Override
    public Player processWhoseDistrictToDestroy() {
        return board.playerWithTheBiggestCity(this);
    }

    /**
     * Défini le district à détruire quand le bot joue le condottière.
     */
    @Override
    public District processDistrictToDestroy(Player target) {
        Optional<District> tmp=target.city.cheapestDistrict();
        if(tmp.isPresent()){
            return tmp.get();
        }
        else{
            return null;
        }
    }

    /**
	 * 
	 * @return true si le joueur prend l'argent, false si il pioche les districts.
	 */
    @Override
    public boolean coinsOrDistrict() {
        return getGold() < 8;
    }

    /**
	 * 
	 * @return le rôle a tué choisi par une stratégie.
	 */
    @Override
    public Role processWhoToKill() {
        this.attributeProbsToPlayer();
        Set<String> targets = matches.possibleRolesFor(this.board.playerWithTheBiggestCity(this).getId());
        targets.remove(this.getCharacter().toString());//on exclut son propre role
        return this.dealRoles.getRole(targets.stream().findFirst().get());
    }

    /**
	 * 
	 * @return le rôle a volé choisi par une stratégie.
	 */
    @Override
    public Role processWhoToRob() {
        Set<String> targets = matches.possibleRolesFor(board.richestPlayer(this).getId());
        targets.remove(this.getCharacter().toString());
        return this.dealRoles.getRole(targets.stream().findFirst().get());
    }

    /**
     * avant de commencer à trier
     * On garde les cartes les moins cheres
     * Bien sûr on doit toujours avoir getNumberDistrictKeepable() cartes à la fin
     */
    @Override
    public void discard(List<District> d){
        d.sort((a,b)->
                Integer.compare(a.getCost(),b.getCost())
        );
        while(d.size()>this.getCharacter().getNumberDistrictKeepable()){
            this.deck.putbackOne(d.remove(d.size()-1));

        }
    }

    /**
     * Défini les districts à construire pour les stratégies.
     */
    @Override
    public List<District> processWhatToBuild() {
        List<District> toConsider;
        if(city.getSizeOfCity()==7){//si on est sur le point de finir 
            //on peut construire des cartes de cout 1 puisque le condotierre
            // ne peut pas détruire
            //les cités finies
            toConsider=getHand().toList().stream()
            .filter(d->!city.alreadyContains(d))
            .collect(Collectors.toList());
        }
        else{//ne pas construire des cartes de cout 1 sinon le condotierre peut les détruire facilement
            //sans rien payer
            toConsider=getHand().toList().stream()
            .filter(d->!city.alreadyContains(d)&& d.getCost()>1)
            .collect(Collectors.toList());
        }
        return buildables(
                        toConsider,
                        getGold()
                )
            .getDistricts().stream()
            .sorted((a,b)->-Integer.compare(a.getValue(), b.getValue()))
            .collect(Collectors.toList());
    }

    /* ---------------------------------------------*/



    /**
     * Cette fonction permet d'attribuer
     * des probabilités pour permettre au joueur 
     * de deviner le rôle qu'ont les autres joueurs
     *  Ex: Au cours de la distribution
     *  si on a à choisir parmi des personnages (p1,p2,p3)
     * Cela veut dire que tous les joueurs ayant choisi
     * avant n'ont pas pris (p1,p2,p3) d'où
     * la probabilité pour qu'ils aient (p1,p2 ou p3)
     * comme role est 0
     * 
     */
    public void attributeProbsToPlayer(){
        ArrayList<Player> pl=board.getPlayers();
        ArrayList<Role> toConsider=new ArrayList<>(this.dealRoles.getLeftRoles());
        var hiddenRole=this.dealRoles.getHidden();
        ArrayList<Role> visibles=this.dealRoles.getVisible();

        if(this.nextPlayer!=null && this.nextPlayer.alreadyChosenRole){
            toConsider.add(hiddenRole);
        }
        for (Player player : pl) {
            //tous ceux qui ont déja choisi leurs roles n'ont évidemment pas 
            //pu prendre ce qui reste
            if(player.alreadyChosenRole){
                for (Role role : toConsider) {
                    matches.setProbability(player.getId(), role.toString(), 0);
                }
            }
            //personne n'a les roles visbibles
            for (Role role : visibles) {
                matches.setProbability(player.getId(), role.toString(), 0);
            }
            
            
        }
    }

    /**
     * 
     * @return les couleurs manquantes dans la cité et la main du bot.
     */
    public ArrayList<DistrictColor> missingColors(){
        Set <DistrictColor> tmpHand=hand.colorsOfHand();
        Set <DistrictColor> tmpCity=city.colorsOfCity();
        
        Set<DistrictColor> tmp=new HashSet<>(Set.of(DistrictColor.values()));
        tmp.removeAll(tmpHand);
        tmp.removeAll(tmpCity);
        
        return new ArrayList<>(tmp);
    }




    

}