/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaheart.Game;

import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javaheart.Game.Global.CARD_TYPES;
import javaheart.Game.Global.POSITION;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author MItHOt
 */
public final class Gamer {
    private String name;
    private JLabel jLabelName;
    private JLabel heartsJlabel;
    private JLabel messageGame;
    private int id;
    private ArrayList<Card> cards = new ArrayList<Card>();
    private POSITION position;
    private Card pickedCard = null;
    private static ResourceMap resourceMap;
    private static char cardTypeOnboard;
    private static JPanel boardPanel;
    private static boolean hasHelp = Boolean.FALSE;
    private static boolean heartBroken = Boolean.FALSE;
    private boolean play = Boolean.TRUE;
    private boolean CTWO = Boolean.TRUE;
    private int point = 0;
    private String storeCards = "Card#0";
    private static ArrayList<JLabel> jLabelSet = new ArrayList<JLabel>();

    public static String[] indexCards;
    public ArrayList<String> rotateCards = new ArrayList<String>();
    public static int numSET = 1;
    public static boolean semaphore = Boolean.TRUE;
    
    // Constructors
    public Gamer(int id, String name, POSITION position, JPanel boardPanel) {
        this.id = id;        
        this.name = name;
	this.position = position;
	Gamer.boardPanel = boardPanel;
        indexCards();
    }

    // Getters and Setters
    public int getId() { return id; }
    public int getNumCards(){ return cards.size(); }
    public int getPoint(){ return point; }
    public String[] getStoreCard(){ return storeCards.split("#"); }
    public String getNameCard(int i){ return cards.get(i).getName(); }
    public String getStoreCards(){ return this.storeCards ; }
    public String getName(){ return name; }
    public boolean getPlay(){ return this.play; }
    public Card getPicksCard(){ return pickedCard; }
    public POSITION getPOSITION(){ return this.position; }
    public ArrayList<Card> getAllCard(){ return cards; }
    public static int getNumSET(){ return numSET; }
    public static char getCardTypeOnboard() { return Gamer.cardTypeOnboard; }
    public String getName(int ID){
	    if(this.id == ID)
		    return name;
	    return "";
    }
    public ArrayList<JLabel> getCardLabels() {
	    ArrayList<JLabel> jLabels = new ArrayList<JLabel>();
	    for (int i = 0; i < cards.size(); i++) {
		    jLabels.add(cards.get(i).getJLabel());
	    }
	    return jLabels;
    }

    public void setStoreCards(String card) { this.storeCards += card; }
    public void setPoint(int point){ this.point += point; }
    public void setPOSITION(POSITION position){ this.position = position; }
    public void setID(int id){ this.id = id; }
    public void setPlay(boolean play){ this.play = play; }
    public void setAllCard(ArrayList<Card> cards){ this.cards = cards; } 
    public static void setCardTypeOnboard(char cardTypeOnboard) { Gamer.cardTypeOnboard = cardTypeOnboard; }
    public static void setResourceMap(ResourceMap resourceMap) { Gamer.resourceMap = resourceMap; }
    public void setIDName(int ID, String name){
	    this.id = ID;
	    this.name = name;
    }
    
    public void indexCards(){
	    String Cards = "TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,JACK,QUEEN,KING,ACE";
	    indexCards = Cards.split(",");
    }

    // Public methods
    public boolean foundCard(String name){
	    for(int i = 0; i < cards.size(); i++)
		    if(cards.get(i).getName().equals(name))
			    return true;
	    return false;
    }
    
    //reset game sau khi ket thuc : 
    public void resetSET(){
        point = 0;
        numSET ++;
        play = Boolean.TRUE;
        CTWO = Boolean.TRUE;
        hasHelp = Boolean.FALSE;
        storeCards = "Card#0";
        semaphore = Boolean.TRUE;
        heartBroken = Boolean.FALSE;
        cards = new ArrayList<Card>();
        rotateCards = new ArrayList<String>();
        cards.clear();
        LoadGame();
        EnterBoard();
    }

    public void LoadGame(){
	    Point[] poi = position(this.position);
	    Point pos = poi[0], offset = poi[1];
	    for(int i = 0; i < 13 ; i++){
		    int cardOrder = i - 1;
		    Point location = new Point(pos.x + offset.x*cardOrder, pos.y+offset.y*cardOrder);
		    LoadView(location);
	    }
    }

    public void LoadView(Point location){
	    ImageIcon icon = resourceMap.getImageIcon("Card.BLUE");
	    icon.setDescription("BLUE");
	    Card card = new Card(icon,location);
	    cards.add(card);
    }
    
    public void EnterBoard() {
	    // generate random cards here
	    for (int i = 0; i< cards.size(); i++) {
		    boardPanel.add(cards.get(i).getJLabel());
	    }
    }
    
    public void EnterCard(String name) {
	    for(int i = 0; i < cards.size(); i++)
		    if(cards.get(i).getName().equals(name))
			    boardPanel.add(cards.get(i).getJLabel());
    }
    
    public void prepareClient(String[] cards) {
	    if(cards.length < 3) return;
	    Point[] points = position(this.position);
	    Point pos = points[0], offset = points[1];
	    for(int i = 2; i < cards.length; i++){
		    int cardOrder = i - 1;
		    Point location = new Point(pos.x + offset.x*cardOrder, pos.y+offset.y*cardOrder);
		    AddCard(cards[i],location);
	    }
    }
    
    // lấy bài và vị trí của bài :
    public void Prepare(List shuffledCards) {
	    Point[] poi = position(this.position);
	    Point pos = poi[0], offset = poi[1];
	    if(id == 0)
		    cards.removeAll(cards);
	    int slot = this.id + 1;
	    for (int i = (slot-1)*13 ; i < slot*13; i++) {
		    CARD_TYPES type = (CARD_TYPES) shuffledCards.get(i);
		    int cardOrder = i - (slot-1) * 13;
		    Point location = new Point(pos.x + offset.x*cardOrder, pos.y+offset.y*cardOrder);
		    addNamePoint(type,location);
	    }
    }
    
    public String arrayCard(){
	    String GAME = "GAME#" + this.id ;
	    if(cards.size() < 14)
		    for(int i = 0; i < 13; i++)
			    GAME += "#" + cards.get(i).getName();
	    else
		    for(int i = 13; i < 26; i++)
			    GAME += "#" + cards.get(i).getName();
	    return GAME;
    }
    
    // --------------end mode server-------------------------------------
    
    public void Pick(String name) {
	    char pickedCardType = name.charAt(0);
	    
	    if(foundCard("CTWO") == true && CTWO == true && !"CTWO".equals(name)){
		    addHelp("Lượt đầu bạn phải đánh quân hai chuồng (nhép) ");
		    return;
	    }
	    
	    if (pickedCardType == 'H'){
		    if(cardTypeOnboard == '\u0000' && !heartBroken){
			    addHelp("Trái tim chưa bị vỡ, chọn quân khác !");
			    return;
		    } else if(!heartBroken) {
				    for (Card card : cards) {
					    if (card.getName().charAt(0) == cardTypeOnboard) {
						    addHelp("Trái tim chưa bị vỡ, chọn quân khác !");
						    return;
					    }
				    }
				    HeartBrokenMessage("Trái tim đã bị vỡ !!!");
				    heartBroken = true;
				    pickCard(name);
				    return;
			    }
	    }
	    
	    if (pickedCardType != cardTypeOnboard) {
		    for (Card card : cards) {
			    if (card.getName().charAt(0) == cardTypeOnboard) {
				    addHelp("Đánh quân bài cùng loại với đối thủ !");
				    return;
			    }
		    }
	    }
	    
	    if (pickedCard != null) {
		    pickedCard.Pick(position);
		    pickedCard = null;
	    } else pickCard(name);
    }

    public void CheckHeartBroken(){
	    if(pickedCard != null){
		    char pickedCardType = pickedCard.getName().charAt(0);
		    if(pickedCardType == 'H' && !heartBroken){
			    heartBroken = true;
			    addHelp("Trái tim đã bị vỡ !!!");
			    HeartBrokenMessage("Trái tim đã bị vỡ !!!");
		    }
	    }
    }
    
    public void HeartBrokenMessage(String name){
	    if(heartsJlabel != null){
		    removeJlabel(heartsJlabel);
		    heartsJlabel = null;
	    }
	    heartsJlabel = new JLabel(name);
	    heartsJlabel.setBounds(0,480,200,11);
	    heartsJlabel.setFont(new Font("Tahoma", Font.BOLD, 11));
	    boardPanel.add(heartsJlabel);
	    boardPanel.revalidate();
	    boardPanel.repaint();
    }
    
    public void Click(String name){ addHelp(name); }
    
    public JLabel helpSET(String msg) {
	    JLabel helpLabel = new JLabel(msg);
	    helpLabel.setBounds(250, 160, 450, 11);
	    helpLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
	    boardPanel.add(helpLabel);
	    boardPanel.revalidate();
	    boardPanel.repaint();
	    return helpLabel;
    }
    
    public void deleteHelpSET(JLabel helpLabel){
	    boardPanel.remove(helpLabel);
	    boardPanel.revalidate();
	    boardPanel.repaint();
    }
    
    public void MoveCard() {
	    if (pickedCard != null) {
		    Point destination = null; // this is the position the picked card will be moved to
		    switch(this.position) {
			    case TOP   : destination = new Point (300,150); break;
			    case RIGHT : destination = new Point (350,200); break;
			    case BOTTOM: destination = new Point (300,250); break;
			    case LEFT  : destination = new Point (220,170); break;
		    }
		    pickedCard.Move(destination) ;
		    if("CTWO".equals(pickedCard.getName()))
			    CTWO = false;
	    }
    }
    //kiem tra du chua ? neu chua du add ten vao
    //neu du roi ma chon lại thì
    public void Rotate(String nameCard){
	    if(rotateCards.size() < 3 ){
		    if(rotateCards.size() > 0){
			    for(String namecard : rotateCards){
				    if(namecard.equals(nameCard)){
					    rotateCards.remove(namecard);
					    pickCard(nameCard);
					    return;
				    }
			    }
		    }
		    pickCard(nameCard);
		    pickedCard = null;
		    rotateCards.add(nameCard);
	    }
    }
    
    public int getSizeRotate(){
	    return rotateCards.size();
    }
    
    public void RemovePickedcard() {
	    if (pickedCard != null) {
		    cards.remove(pickedCard);
		    pickedCard = null;
	    }
    }
    
    public void RemoveAllCard(){
	    cards.removeAll(cards);
    }
    
    public  void pickCard(String name) {
	    for (Card card : cards) {
		    if (card.getName().equals(name)) {
			    pickedCard = card;
			    pickedCard.Pick(position);
			    break;
		    }
	    }
    }
    
    public void nameClientJLabel(String name){
	    if("".equals(name)) name = this.name;
	    if(jLabelName != null)
		    removeJlabel(jLabelName);
	    
	    Point destination = null; // this is the position the picked card will be moved to
	    switch(this.position) {
		    case TOP   : destination = new Point (310,0); break;
		    case RIGHT : destination = new Point (610,65); break;
		    case BOTTOM: destination = new Point (300,475); break;
		    case LEFT  : destination = new Point (30,65); break;
	    }
	    jLabelName = new JLabel(name);
	    jLabelName.setBounds(destination.x, destination.y, 150,30);
	    jLabelName.setFont(new Font("Tahoma", Font.BOLD, 12));
	    boardPanel.add(jLabelName);
	    boardPanel.revalidate();
	    boardPanel.repaint();
	    
    }
    
    public void updatePoint(){
	    if(jLabelName != null)
		    removeJlabel(jLabelName);
	    String nameClient = this.name + " : " + this.point + " điểm";
	    nameClientJLabel(nameClient);
    }
    
    public void removeJlabel(JLabel jLabelName){
	    boardPanel.remove(jLabelName);
	    boardPanel.revalidate();
	    boardPanel.repaint();
    }
    
    
    
    public void addMessage(String msg) {
	    if(messageGame != null){
		    removeJlabel(messageGame);
		    messageGame = null;
	    }
	    messageGame = new JLabel(msg);
	    messageGame.setBounds(200, 355, 310, 11);
	    messageGame.setFont(new Font("Tahoma", Font.BOLD, 11));
	    boardPanel.add(messageGame);
	    boardPanel.revalidate();
	    boardPanel.repaint();
    }
    
    public void removeMessage(){
	    if(messageGame != null)
		    removeJlabel(messageGame);
    }
    
    public static void reportSET(ArrayList<Gamer> gamers){
	    String report = "";
	    String tongket = "";
	    if(Gamer.getNumSET() == 1){
		    tongket += "SET " + "       " +
			    gamers.get(0).getName() + "       " +
			    gamers.get(1).getName() + "       " +
			    gamers.get(2).getName() + "       " +
			    gamers.get(3).getName() ;
		    JLabel set = new JLabel(tongket);
		    jLabelSet.add(set);
		    
	    }
	    for (int i = Gamer.getNumSET() - 1; i < Gamer.getNumSET(); i++) {
		    report += (Gamer.getNumSET()) +
			    "                " +  gamers.get(0).getPoint() +
			    "                " +  gamers.get(1).getPoint() +
			    "                " +  gamers.get(2).getPoint() +
			    "                " +  gamers.get(3).getPoint() ;
		    JLabel set = new JLabel(report);
		    jLabelSet.add(set);
	    }
	    int a = 140;
	    for(JLabel jlabel : jLabelSet){
		    a += 20;
		    jlabel.setBounds(250, a, 500, 11);
		    jlabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		    boardPanel.add(jlabel);
		    boardPanel.revalidate();
		    boardPanel.repaint();
	    }
    }
    
    public static void deleteReportSET(){
	    for(JLabel jlabel : jLabelSet){
		    boardPanel.remove(jlabel);
		    boardPanel.revalidate();
		    boardPanel.repaint();
	    }
    }
    

    // Helpers
    private void addNamePoint(CARD_TYPES type, Point location){
            switch(type) {
				/* Spades */
				case SACE: AddCard("SACE", location); break;
				case STWO: AddCard("STWO", location); break;
				case STHREE: AddCard("STHREE", location); break;
				case SFOUR: AddCard("SFOUR", location); break;
				case SFIVE: AddCard("SFIVE", location); break;
				case SSIX: AddCard("SSIX", location); break;
				case SSEVEN: AddCard("SSEVEN", location); break;
				case SEIGHT: AddCard("SEIGHT", location); break;
				case SNINE: AddCard("SNINE", location); break;
				case STEN: AddCard("STEN", location); break;
				case SJACK: AddCard("SJACK", location); break;
				case SQUEEN: AddCard("SQUEEN", location); break;
				case SKING: AddCard("SKING", location);  break;
				/* Clubs */
				case CACE: AddCard("CACE", location); break;
				case CTWO: AddCard("CTWO", location); break;
				case CTHREE: AddCard("CTHREE", location); break;
				case CFOUR: AddCard("CFOUR", location); break;
				case CFIVE: AddCard("CFIVE", location); break;
				case CSIX: AddCard("CSIX", location); break;
				case CSEVEN: AddCard("CSEVEN", location); break;
				case CEIGHT: AddCard("CEIGHT", location); break;
				case CNINE: AddCard("CNINE", location); break;
				case CTEN: AddCard("CTEN", location); break;
				case CJACK: AddCard("CJACK", location); break;
				case CQUEEN: AddCard("CQUEEN", location); break;
				case CKING: AddCard("CKING", location);  break;
				/* Diamonds */
				case DACE: AddCard("DACE", location); break;
				case DTWO: AddCard("DTWO", location); break;
				case DTHREE: AddCard("DTHREE", location); break;
				case DFOUR: AddCard("DFOUR", location); break;
				case DFIVE: AddCard("DFIVE", location); break;
				case DSIX: AddCard("DSIX", location); break;
				case DSEVEN: AddCard("DSEVEN", location); break;
				case DEIGHT: AddCard("DEIGHT", location); break;
				case DNINE: AddCard("DNINE", location); break;
				case DTEN: AddCard("DTEN", location); break;
				case DJACK: AddCard("DJACK", location); break;
				case DQUEEN: AddCard("DQUEEN", location); break;
				case DKING: AddCard("DKING", location);  break;
				/* Diamonds */
				case HACE: AddCard("HACE", location); break;
				case HTWO: AddCard("HTWO", location); break;
				case HTHREE: AddCard("HTHREE", location); break;
				case HFOUR: AddCard("HFOUR", location); break;
				case HFIVE: AddCard("HFIVE", location); break;
				case HSIX: AddCard("HSIX", location); break;
				case HSEVEN: AddCard("HSEVEN", location); break;
				case HEIGHT: AddCard("HEIGHT", location); break;
				case HNINE: AddCard("HNINE", location); break;
				case HTEN: AddCard("HTEN", location); break;
				case HJACK: AddCard("HJACK", location); break;
				case HQUEEN: AddCard("HQUEEN", location); break;
				case HKING : AddCard("HKING", location); break;
			}
        }

    private void AddCard(String name, Point location){
	    ImageIcon icon = resourceMap.getImageIcon("Game." + name);
	    icon.setDescription(name);
	    Card card = new Card(icon,location);
	    cards.add(card);
    }

    private Point[] position(POSITION position){
	    Point[] pos = new Point[2];
	    switch(this.position) {
		    case TOP   : 	pos[0] = new Point(430,30); pos[1] = new Point(-20, 0);  break;
		    case RIGHT : 	pos[0] = new Point(200 + 13*20 + 140, 315); pos[1] = new Point(0,-20);break;
		    case BOTTOM: 	pos[0] = new Point(430, 20 + 13*20 + 100); pos[1] = new Point(-20,0);break;
		    case LEFT  : 	pos[0] = new Point(20 , 315); pos[1] = new Point(0,-20); break;
	    }
	    return pos;
    }
    /* add help hints in the board */
    private void addHelp(String msg) {
	    if (!hasHelp) {
		    final JLabel helpLabel = new JLabel(msg);
		    helpLabel.setBounds(200, 140, 300, 11);
		    helpLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		    boardPanel.add(helpLabel);
		    boardPanel.revalidate();
		    boardPanel.repaint();
		    hasHelp = Boolean.TRUE;
		    
		    /* wait 3 seconds and then remove the hint help */
		    new java.util.Timer().schedule(new java.util.TimerTask() {
			    @Override
			    public void run() {
				    removeJlabel(helpLabel);
				    hasHelp = Boolean.FALSE;
			    }
		    }, 3000);
	    }
    } 
}


