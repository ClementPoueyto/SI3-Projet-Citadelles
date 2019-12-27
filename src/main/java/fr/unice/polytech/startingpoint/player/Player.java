package fr.unice.polytech.startingpoint.player;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.unice.polytech.startingpoint.board.Bank;
import fr.unice.polytech.startingpoint.board.Board;
import fr.unice.polytech.startingpoint.board.Deck;
import fr.unice.polytech.startingpoint.board.District;
import fr.unice.polytech.startingpoint.game.DealRoles;
import fr.unice.polytech.startingpoint.role.Role;

/**
 * @author Patrick Anagonou 
 * @author Heba Bouzidi
 * @author Loic Bertolotto
 * @author Clement Poueyto
 *
 * */

public class Player {
	private final int id;
	private Role character;
	protected Hand hand;
	protected City city;
	protected Player nextPlayer;
	protected Board board;


	/*Attributs qui permettront à l'IA de designer ses cibles*/
	protected Role targetToKill;
	protected Role targetToRob;
	protected Player targetToDestroyDistrict;
	protected District districtToDestroy;
	protected List<District> cardsToExchange;
	protected Player targetToExchangeHandWith;
	protected PropertyChangeSupport support;

	/*Attributs permettants de savoir si on a déja joué ou choisi son personnage */
	protected boolean alreadyChosenRole;

	/*Attribut permettant de savoir si le joueur a posé son huitième quartier en premier */
	private boolean firstToFinish=false;

	/*Attribut permettant d'attribuer des probabilités de possession de personnage*/
	protected MatchingProb matches;
	private boolean usingFabricPower=false;
	private boolean hasUsedLabo = false;
	protected Bank bank;
	protected Deck deck;
	protected DealRoles dealRoles;
	private Logger logger = Logger.getLogger("Running");

	

	/**
	 * 
	 * @param id
	 */
	public Player(int id){
		support = new PropertyChangeSupport(this);
		this.id = id;
		hand = new Hand();
		city = new City();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
 
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
	
	public void takeCardsAtBeginning(){
		hand.addAll(deck.withdrawMany(4));//constante à retirer
	}
	
	public void pickNewDistrict(District d) {
		hand.add(d);
	}
	
	/**
	 * Ajout d'un district dans la city
	 * @param le district
	 * @return true si assez d'argent et s'il n'y a pas de district de même nom deja present, 
	 * false sinon.
	 */
	public boolean addToTheCity(District theDistrict) {
		if(city.alreadyContains(theDistrict)){
			return false;
		}
		if(bank.deposit(theDistrict.getCost(),this)) {
			city.add(theDistrict);
			hand.remove(theDistrict);
			logger.log(Level.INFO,"Joueur "+id+" construit \n"+theDistrict.toString());
			return true;
		}
		return false;
	}
	

	public int getGold(){
		return bank.getPlayerMoney(this);
	}

	/**
	 * Destruction de district dans city
	 * @RoleInterface
	 * @param toDelete
	 */
	public final boolean deleteDistrictFromCity(District toDelete){
		if(character!=null){
			if(!character.toString().equals("Bishop") && !toDelete.getName().equals("Donjon")){
				 return deletion(toDelete);
			}
		}
		else{
			if(!toDelete.getName().equals("Donjon")){
				return deletion(toDelete);
			}
		}
		return false;
	}

	/**
	 * Destruction concrete du district d dans city,
	 * verifiant aussi l'existence d'un joueur pouvant utiliser le cimetiere 
	 * @param district visé
	 * @return true si la suppression a ete effectuee, false sinon.
	 */
	boolean deletion(District d) {
		Player p;
		if(board!=null && board.existsGraveyardPlayer() != null) {
			p = board.existsGraveyardPlayer();
			p.isUsingGraveyard(d);
		}
		return city.removeDistrict(d);
	}
	
	/**
	 * Avant d'appeler cette méthode il faut appeler 
	 * bank.canWithdraw(nb) et vérifier la valeur retournée
	 * par celle ci cela permet de savoir si l'argent voulue est disponible
	 */
	public void takeCoinsFromBank(int nb){
		if(bank.withdraw(nb,this)){
			logger.log(Level.INFO,"Joueur "+id+" retire " + nb + " de la banque. ");
		}
	}

	public void exchangeHands() {
		Hand tmpHand=this.hand;
		
		this.hand=targetToExchangeHandWith.hand;

		targetToExchangeHandWith.hand=tmpHand;
		
	}

	
	
	final void surrenderToThief(){
			logger.log(Level.INFO,"Moi le joueur "+id+" j'ai été volé");
			Player thief=dealRoles.getRole("Thief").getPlayer();
			if(thief!=null){
				bank.transferFromTo(this, thief);//donne l'argent au player de Thief
			}
	}

	/**
	 * Méthode de choix de Personnages
	 * pour le moment il choisit le premier Personnage
	 *  puis appelle la méthode sur nextPlayer
	 */
	public final void chooseRole(){
		if(!alreadyChosenRole){
			List<Role> toConsiderRoles=new ArrayList<>(this.dealRoles.getLeftRoles());
			
			if(this.nextPlayer!=null && this.nextPlayer.alreadyChosenRole){
				toConsiderRoles.add(this.dealRoles.getHidden());
			}
			Role tmpRole=processChooseRole(toConsiderRoles);
			setCharacter(tmpRole);
			dealRoles.getLeftRoles().remove(tmpRole);
			alreadyChosenRole=true;
			//appelle le prochain player
			if(nextPlayer!=null){
				nextPlayer.chooseRole();
			}
			
		}
	}
	
	/*    ---------------------------------------To Override--------------------------------- */

	/**
	 * @ToOverride by Bots
	 * @return
	 */
	public Role processChooseRole(List<Role> toConsiderRoles){
		return toConsiderRoles.get(0);
	}

    public boolean coinsOrDistrict(){
		return false;
	}

	
	/**
	 * 
	 * @return true si le joueur construit en premier, false si il utilise le pouvoir de rôle en premier.
	 */
	protected boolean isBuildingFirst() {
		if(getCharacter().toString().equals("Architect")){ //pioche 2 cartes avant de jouer
			return false;
		}
		else if(getCharacter().toString().equals("Wizard")){ //si la main du magicien est mauvaise active son pouvoir, sinon il construit avant
			int countBadCards=getHand().badCards(getGold()).size();
			return countBadCards<=getHand().size()/2;// si plus de la moitié des cartes sont "mauvaises" active son pouvoir
		}
		else {
			return true;
		}
		
	}
	
	public void discard(List<District> d) {
		d.sort((a,b)->
                Integer.compare(a.getCost(),b.getCost())
            );
            while(d.size()>this.getCharacter().getNumberDistrictKeepable()){
                this.deck.putbackOne(d.remove(d.size()-1));
                
            }
	}

	/**
	 * Souhait d'utilisation du laboratoire.
	 * @ToOverride
	 * @return un optional d'un district de la main s'il a un homonyme dans la city,
	 *  un optional empty sinon
	 */
	public Optional<District> wantsToUseLabo(){
		for(District handDis : hand.toList()) {
			if(cityHasTheDistrict(handDis.getName())){
				return Optional.of(handDis);
			}
		}
        return Optional.empty();
	}

	public Role processWhoToKill(){
		return null;
	}

	public Role processWhoToRob(){
		return null;
	}

	public Player processWhoToExchangeHandWith(){
		return null;
	}
	public Player processWhoseDistrictToDestroy(){
		return null;
	}
	public District processDistrictToDestroy(Player target){
		return null;
	}
	/**
	 * Souhait d'utilisation de la manufacture.
	 * @return false
	 * @ToOverride
	 */
	public boolean wantsToUseFabric() {
		return false;
	}

	public List<District> processWhatToBuild() {
		return new ArrayList<>();
	}

	public List<District> processCardsToExchange(){
		return new ArrayList<>();
	}
	/*---------------------------------End ToOverride--------------------------------------*/


	/**
	 * check if murdered
	 * check if stolen
	 * pick money from Bank or pick card from Deck
	 * collect rentMoney
	 * use special power of Role if any
	 * 
	 * 
	 * 
	 * */
	public final void playTurn(){
		this.city.nextDay();
		if(character.isMurdered()){
			logger.log(Level.INFO,"Joueur "+id+" passe son tour car il a été tué");
			return; //on sort de la fonction sans plus rien faire
		}
		else if(character.isStolen()){
			this.surrenderToThief();
			
		}

		this.collectMoneyFromDistricts();
		this.isUsingLabo();
		
		
		if(coinsOrDistrict()){
			this.takeCoinsFromBank(this.character.getNumberGold());
		}
		else{
			List<District> districts=this.deck.withdrawMany(this.character.getNumberDistrictPickable());
			this.discard(districts);
			hand.addAll(districts);
			logger.log(Level.INFO,"Joueur "+id+" prend "+districts.size()+" districts. \n" +
					"Il reste "+this.deck.numberOfCards()+" districts dans le deck.");
		}
		this.isUsingLabo();
		
		if(getCharacter().toString().equals("Architect")){
			this.specialMove();
		}
	
		this.isUsingLabo();
		
		if(isBuildingFirst()) {
			building();
			specialMove();
		}
		else{
			specialMove();
			building();
		}
	}

	

	/**
	 * Cette fonction renvoie un boolean utilisé par 
	 * Role pour déterminer le  nombre que renverra les 
	 * méthodes getNumberDistrictPickable et getNumberDistrictKeepableus
	 * @RoleInterface
	 * @return true
	 */
	
	public boolean isUsingFabric() {
		if(!this.usingFabricPower){
			boolean resultat=getCity().containsWonder("Manufacture")
			&& this.wantsToUseFabric();
			if(resultat){
				this.bank.deposit(3,this);
				this.usingFabricPower=true;
			}
			
		}
		return this.usingFabricPower;
	}
 
	/**
	 * Fonction appliquant l'utilisation du laboratoire
	 * @PlayTurnInterface
	 */
	public final void isUsingLabo() {
		Optional<District> od=wantsToUseLabo();
		if(city.containsWonder("Laboratoire")&& od.isPresent() && !hasUsedLabo) {
			if(hand.remove(od.get())){
				logger.log(Level.INFO,"Joueur " + getId() + " possède et peut utiliser le laboratoire");
				this.deck.putbackOne(od.get());
				takeCoinsFromBank(1);
				hasUsedLabo = true;
			}
	   }
	}
	/**
	 * Souhait d'utilisation du cimetiere.
	 * @return true
	 * @ToOverride
	 */
	protected boolean wantsToUseGraveyard(District d) {
		return true;
	}
	
	/**
	 * Fonction appliquant l'utilisation du cimetiere
	 * @param Le district a recuperer
	 */
	protected void isUsingGraveyard(District d) {
		if(wantsToUseGraveyard(d)) {
			logger.log(Level.INFO,"Joueur " + getId() + " possède et peut utiliser le cimetière");
			bank.deposit(1, this);
			hand.add(d);
		}
	}
	/**
	 * Méthode pour remettre au default les valeurs 
	 * changées par le tour qui vient d'être joué
	 * Il peut y avoir une méthode pareille pour une 
	 * partie complète 
	 *
	 */
	public final void reInitializeForNextTurn(){
		usingFabricPower=false;
		hasUsedLabo=false;
		alreadyChosenRole=false;
		character=null;
		targetToDestroyDistrict=null;
		targetToExchangeHandWith=null;
		targetToKill=null;
		targetToRob=null;
		cardsToExchange=null;
		if(matches!=null){
			matches.reInitialize();
		}
		
		// forgot something ??
	}
	

	protected final void building() {
		if(!getHand().isEmpty()) {
			var toBuild = this.processWhatToBuild();
			int maxbuild=this.getCharacter().getNumberDistrictBuildable();
			while(maxbuild>0 && !toBuild.isEmpty()){
				if(addToTheCity(toBuild.remove(0))){
					maxbuild--;
				}
				
			}
		}

		if(checkFinishBuilding() || this.deck.numberOfCards()<=0){
			/*
			Notez que si il reste encore des cartes dans le deck et
			que le joueur a bien atteint  les 8 districts sans être le premier à
			l'avoir fait, ce bloc n'est pas executé
			Cela ne pose pas problème puisque comme ça le Manager n'est notifié qu'une
			seule fois du fait que le jeu doit prendre fin au lieu de plusieurs
			fois
			*/
			support.firePropertyChange("gameOver",false , true);
			
		}
		this.isUsingLabo();
	}

	

	

	
	public final void specialMove() {
		targetToKill=processWhoToKill();
        targetToRob=processWhoToRob();
        targetToExchangeHandWith=processWhoToExchangeHandWith();
        targetToDestroyDistrict = processWhoseDistrictToDestroy();
		districtToDestroy = processDistrictToDestroy(this.targetToDestroyDistrict);
		cardsToExchange=processCardsToExchange();
		logger.log(Level.INFO,"Joueur "+id+" active son effet de rôle");
		character.useSpecialPower();	
		this.isUsingLabo();
	}
	
	/**
	 * Méthode pour collecter l'argent des districts
	 */
	protected void collectMoneyFromDistricts(){
		character.collectRentMoney();
	}
	
	@Override
	public String toString() {
		return "**********\n"
			+ "Player #" + id + "\n"
			+ "Current role: " + character +"\n"
			+ "Amount of gold: " + getGold() + "\n"
			+ "City :" + city +"\n";
	}
	/**
	 * Contrairement a ce que son nom peut laisser croire cette méthode ne se contente
	 * pas de vérifier si le joueur a fini de construire  8 districts
	 * Elle vérifie également si le joueur est bien le premier à avoir fini
	 * avec isFirstToFinish()
	 * @return true if the player is the first to reach at least 8 districts 
	 * 
	 */
	public boolean checkFinishBuilding()
	{
		if(this.isFirstToFinish() && city.getSizeOfCity()>=8){
			this.firstToFinish=true;
		}
		return this.firstToFinish;
	}
	/**
	 * Cette fonction doit permettre d'initialiser l'attribut
	 * firstToFinish, attribut qui sera utilisé plus tard dans la méthode
	 * points() pour attribuer ou non les bonus
	 * Le board et des joueurs doivent etre défini avant de lancer cette fonction
	 * @return un boolean selon que le joueur est le premier a avoir fini ou pas
	 */
	public boolean isFirstToFinish(){
		ArrayList<Player> players =board.getPlayers();

		for (Player player : players) {
			if(player.firstToFinish && player.id!=this.id){
				return false;
			}
		}
		
		return true;

	}


	/**
	 * Cette méthode permet de compter les points en fin de partie
	 * @return le nombre de points
	 */
	public int points(){
		int points=0;
         points+=city.totalValue();
		 if(this.firstToFinish){
			//Le premier joueur à avoir fini sa city reçoit +4
			points+=4;
		 }
		 else if(!this.firstToFinish && city.getSizeOfCity()>=8){
			 // + 2 pour les autres joueurs ayant huit quartiers.
			 points+=2;
		 }
		 
         return points;
	}

	public boolean cityHasTheDistrict(String districtName){
		return this.city.containsWonder(districtName);
	}
	
	public boolean handHasTheDistrict(String districtName){
		return this.hand.containsWonder(districtName);
	}

	/**
	 * Pour qu'on puisse utiliser HashMap<Player,Integer>
	 */
	@Override
	public int hashCode(){
		return id;
	}

	/* ----- setters et getters ----*/

	
	public Player getNextPlayer() {
		return nextPlayer;
	}

	public void setNextPlayer(Player nextPlayer) {
		this.nextPlayer = nextPlayer;
	}

	
	public void setHand(Hand liste) {
		this.hand=liste;
	}

	public Board getBoard() {
		return board;
	}


	public Role getTargetToKill() {
		return targetToKill;
	}

	public void setTargetToKill(Role targetToKill) {
		this.targetToKill = targetToKill;
	}

	public Role getTargetToRob() {
		return targetToRob;
	}

	
	public void setTargetToRob(Role targetToRob) {
		this.targetToRob = targetToRob;
	}

	public Player getTargetToDestroyDistrict() {
		return targetToDestroyDistrict;
	}

	public void setTargetToDestroyDistrict(Player targetToDestroyDistrict) {
		this.targetToDestroyDistrict = targetToDestroyDistrict;
	}

	public District getDistrictToDestroy() {
		return districtToDestroy;
	}

	public void setDistrictToDestroy(District districtToDestroy) {
		this.districtToDestroy = districtToDestroy;
	}
	
	public Player getTargetToExchangeHandWith() {
		return targetToExchangeHandWith;
	}

	public void setTargetToExchangeHandWith(Player targetToExchangeHandWith) {
		this.targetToExchangeHandWith = targetToExchangeHandWith;
	}

	public List<District> getCardsToExchange(){
		return new ArrayList<>(cardsToExchange);
	}
	

	public Role getCharacter() {
		return character;
	}

	public void setCharacter(Role character) {
		this.character = character;
		character.setPlayer(this);
	}

	/**Dans cette méthode on crée et on initialise le MatchingProb
	 * 
	 */
	public void setBoard(Board board) {
		this.board = board;
		if(this.board.getPlayers()!=null){
			setMatches(new MatchingProb(this.board.getPlayers()));
		}
		
	}

	public void setMatches(MatchingProb matches){
		this.matches = matches;
	}

	public Hand getHand(){return hand;}
	
	public int getId() {
		return id;
	}
	
	public City getCity() {
		return city;
	}

	/**
	 * Pour les tests uniquement, ne devrait pas etre
	 * utilisé ailleurs
	 * @param city
	 */
	
	public void setCity(City city) {
		this.city = city;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public void setDeck(Deck d) {
		this.deck=d;
	}

	

	public void setDealRoles(DealRoles dealRoles) {
		this.dealRoles = dealRoles;
	}

	
	public Deck getDeck() {
		return deck;
	}

	public int sizeOfHand(){
		return hand.size();
	}

	public int sizeOfCity(){
		return city.getSizeOfCity();
	}
	
	public int totalValueOfCity(){
		return city.totalValue();
	}

	public MatchingProb getMatches() {
		return matches;
	}

}