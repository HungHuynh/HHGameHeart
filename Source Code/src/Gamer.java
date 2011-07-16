/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaheart.Play;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javaheart.Game.Card;
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
    private int ID;
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
    public static boolean semaphore = Boolean.TRUE;
    private int point = 0;
    public static String[] indexCards;
    private String storeCards = "Card#0";
    public static int numSET = 1;
    private static ArrayList<JLabel> jLabelSet = new ArrayList<JLabel>();
    public ArrayList<String> rotateCards = new ArrayList<String>();
    
    public Gamer(int id, String name, POSITION position, JPanel boardPanel) {
        this.ID = id;        
        this.name = name;
	this.position = position;
	Gamer.boardPanel = boardPanel;
        indexCards();
    }
    
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
        loadGame();
        EnterBoard();
    }
    
    //Lấy quân bài được chọn : 
    public Card getPicksCard(){
        return pickedCard;
    }
    
    public String[] getStoreCard(){
        return storeCards.split("#");
    }
    
    public void indexCards(){
        String Cards = "TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,JACK,QUEEN,KING,ACE";
        indexCards = Cards.split(",");
    }
    
    public int getPoint(){
        return point;
    }
    
    public static int getNumSET(){
        return numSET;
    }
    
    public String getNameCard(int i){
        return cards.get(i).getName();
    }
    
    public int numCards(){
        return cards.size();
    }
    
    public boolean foundCard(String name){
        for(int i = 0; i < cards.size(); i++)
            if(cards.get(i).getName().equals(name))
                return true;
        return false;
    }
    
    public void setStoreCards(String card) {
        this.storeCards += card; 
    }
    
    public String getStoreCards(){
        return this.storeCards ; 
    }
    
    public void setPoint(int point){
        this.point += point;
    }
    
    public static void setCardTypeOnboard(char cardTypeOnboard) {
		Gamer.cardTypeOnboard = cardTypeOnboard;
    }
    
    public static char getCardTypeOnboard() {
	return Gamer.cardTypeOnboard;
    }
    
    public void setPOSITION(POSITION position){
        this.position = position;
    }
    
    public POSITION getPOSITION(){
        return this.position;
    }
    
    public void setIDName(int ID, String name){
            this.ID = ID;
            this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public String getName(int ID){
        if(this.ID == ID)
            return name;
        return "";
    }
    
    public void setID(int id){
        ID = id;
    }
    
    public void setPlay(boolean play){
        this.play = play;
    }
    
    public boolean getPlay(){
        return this.play;
    }
    
    public static void setResourceMap(ResourceMap resourceMap) {
        Gamer.resourceMap = resourceMap;
    }
    
    public ArrayList<JLabel> getCardLabels() {
         ArrayList<JLabel> jLabels = new ArrayList<JLabel>();
	for (int i = 0; i < cards.size(); i++) {
		jLabels.add(cards.get(i).getJLabel());
	}
	return jLabels;
    } 
    
    public ArrayList<Card> getAllCard(){
            return cards;
    }
    
   public void setAllCard(ArrayList<Card> cards){
            this.cards = cards;
   }
    
    public void loadGame(){
        Point[] poi = position(this.position);
        Point pos = poi[0], offset = poi[1];
        for(int i = 0; i < 13 ; i++){
             int cardOrder = i - 1;
             Point location = new Point(pos.x + offset.x*cardOrder, pos.y+offset.y*cardOrder);
             loadView(location);
        }
    }
    public void loadView(Point location){   
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
    
    //Mode : Client 
    public void prepareClient(String[] cards) {
        if(cards.length < 3) return;
        Point[] poi = position(this.position);
        Point pos = poi[0], offset = poi[1];
        int slot = this.ID;
        List card;
        for(int i = 2; i < cards.length; i++){
            int cardOrder = i - 1;
            Point location = new Point(pos.x + offset.x*cardOrder, pos.y+offset.y*cardOrder);
            addCard(cards[i],location);
        }
    }
// --------------end mode client-------------------------------------     
    	
    //Mode : Server 
    public void addCard(String name, Point location){
        ImageIcon icon = resourceMap.getImageIcon("Game." + name);
	icon.setDescription(name);
	Card card = new Card(icon,location);
	cards.add(card);
        
    }

    // lấy bài và vị trí của bài : 
	public void Prepare(List shuffledCards) {
		Point[] poi = position(this.position);
                Point pos = poi[0], offset = poi[1];
                if(ID == 0)
                    cards.removeAll(cards);
		int slot = this.ID + 1;
		for (int i = (slot-1)*13 ; i < slot*13; i++) {
			CARD_TYPES type = (CARD_TYPES) shuffledCards.get(i);
			int cardOrder = i - (slot-1) * 13;
			Point location = new Point(pos.x + offset.x*cardOrder, pos.y+offset.y*cardOrder);
                        addNamePoint(type,location);
		}
	}
        
    public String arrayCard(){
        String GAME = "GAME#" + this.ID ;
        if(cards.size() < 14)
            for(int i = 0; i < 13; i++)
                GAME += "#" + cards.get(i).getName(); 
        else
            for(int i = 13; i < 26; i++)
                GAME += "#" + cards.get(i).getName(); 
        return GAME;
    }
    
    public Point[] position(POSITION position){
        Point[] pos = new Point[2];
        switch(this.position) {
		case TOP   : 	pos[0] = new Point(430,30); pos[1] = new Point(-20, 0);  break;
		case RIGHT : 	pos[0] = new Point(200 + 13*20 + 140, 315); pos[1] = new Point(0,-20);break;
		case BOTTOM: 	pos[0] = new Point(430, 20 + 13*20 + 100); pos[1] = new Point(-20,0);break;
		case LEFT  : 	pos[0] = new Point(20 , 315); pos[1] = new Point(0,-20); break;
	}
        return pos;
    }
    // --------------end mode server-------------------------------------  
    
    public void addNamePoint(CARD_TYPES type, Point location){
            switch(type) {
				/* Spades */
				case SACE: addCard("SACE", location); break;
				case STWO: addCard("STWO", location); break;
				case STHREE: addCard("STHREE", location); break;
				case SFOUR: addCard("SFOUR", location); break;
				case SFIVE: addCard("SFIVE", location); break;
				case SSIX: addCard("SSIX", location); break;
				case SSEVEN: addCard("SSEVEN", location); break;
				case SEIGHT: addCard("SEIGHT", location); break;
				case SNINE: addCard("SNINE", location); break;
				case STEN: addCard("STEN", location); break;
				case SJACK: addCard("SJACK", location); break;
				case SQUEEN: addCard("SQUEEN", location); break;
				case SKING: addCard("SKING", location);  break;
				/* Clubs */
				case CACE: addCard("CACE", location); break;
				case CTWO: addCard("CTWO", location); break;
				case CTHREE: addCard("CTHREE", location); break;
				case CFOUR: addCard("CFOUR", location); break;
				case CFIVE: addCard("CFIVE", location); break;
				case CSIX: addCard("CSIX", location); break;
				case CSEVEN: addCard("CSEVEN", location); break;
				case CEIGHT: addCard("CEIGHT", location); break;
				case CNINE: addCard("CNINE", location); break;
				case CTEN: addCard("CTEN", location); break;
				case CJACK: addCard("CJACK", location); break;
				case CQUEEN: addCard("CQUEEN", location); break;
				case CKING: addCard("CKING", location);  break;
				/* Diamonds */
				case DACE: addCard("DACE", location); break;
				case DTWO: addCard("DTWO", location); break;
				case DTHREE: addCard("DTHREE", location); break;
				case DFOUR: addCard("DFOUR", location); break;
				case DFIVE: addCard("DFIVE", location); break;
				case DSIX: addCard("DSIX", location); break;
				case DSEVEN: addCard("DSEVEN", location); break;
				case DEIGHT: addCard("DEIGHT", location); break;
				case DNINE: addCard("DNINE", location); break;
				case DTEN: addCard("DTEN", location); break;
				case DJACK: addCard("DJACK", location); break;
				case DQUEEN: addCard("DQUEEN", location); break;
				case DKING: addCard("DKING", location);  break;
				/* Diamonds */
				case HACE: addCard("HACE", location); break;
				case HTWO: addCard("HTWO", location); break;
				case HTHREE: addCard("HTHREE", location); break;
				case HFOUR: addCard("HFOUR", location); break;
				case HFIVE: addCard("HFIVE", location); break;
				case HSIX: addCard("HSIX", location); break;
				case HSEVEN: addCard("HSEVEN", location); break;
				case HEIGHT: addCard("HEIGHT", location); break;
				case HNINE: addCard("HNINE", location); break;
				case HTEN: addCard("HTEN", location); break;
				case HJACK: addCard("HJACK", location); break;
				case HQUEEN: addCard("HQUEEN", location); break;
				case HKING : addCard("HKING", location); break;
			}
        }

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
                    }
                    else
                        if(!heartBroken) {
                            for (Card card : cards) {
				if (card.getName().charAt(0) == cardTypeOnboard) {
                                    addHelp("Trái tim chưa bị vỡ, chọn quân khác !");
                                    return;
                                }
                            }
                            heartsBroken("Trái tim đã bị vỡ !!!");
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
		} 
                else
                        pickCard(name);
    }
    public void checkHeartBroken(){
        if(pickedCard != null){
            char pickedCardType = pickedCard.getName().charAt(0);
            if(pickedCardType == 'H' && !heartBroken){
                heartBroken = true;
                addHelp("Trái tim đã bị vỡ !!!");
                heartsBroken("Trái tim đã bị vỡ !!!");
            }
        }
    }
    
    public void heartsBroken(String name){
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
    
    public void Click(String name){
        addHelp(name); 
    }
    
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
        boolean result = cards.removeAll(cards);
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
                /*
		new java.util.Timer().schedule(new java.util.TimerTask() {
		@Override
		public void run() {
                    removeJlabel(messageGame);
		}
		}, 8000);*/
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
}
// --------------end mode game------------------------------------- 

    
      
