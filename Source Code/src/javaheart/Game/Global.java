package javaheart.Game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Global {

	private static List shuffledCards;
	public static boolean debug = Boolean.TRUE;

	public enum CARD_TYPES {

		SACE, STWO, STHREE, SFOUR, SFIVE, SSIX, SSEVEN, SEIGHT, SNINE, STEN, SJACK, SQUEEN, SKING, /* Spades   */
		CACE, CTWO, CTHREE, CFOUR, CFIVE, CSIX, CSEVEN, CEIGHT, CNINE, CTEN, CJACK, CQUEEN, CKING, /* Clubs    */
		DACE, DTWO, DTHREE, DFOUR, DFIVE, DSIX, DSEVEN, DEIGHT, DNINE, DTEN, DJACK, DQUEEN, DKING, /* Diamonds */
		HACE, HTWO, HTHREE, HFOUR, HFIVE, HSIX, HSEVEN, HEIGHT, HNINE, HTEN, HJACK, HQUEEN, HKING /* Hearts   */
	}

	public static List getShuffledCards() {
		CARD_TYPES[] types = CARD_TYPES.values();
		shuffledCards = Arrays.asList(types);
		Collections.shuffle(shuffledCards);
		return shuffledCards;
	}

	public enum POSITION { TOP, RIGHT, BOTTOM, LEFT }

	/* use for network play */
	public enum MESSAGE_TYPES {
		BEGIN,CHAT,ID,NULL,QUIT,NAME,//protocol chat
                GAME,ROTATE,PLAY,            //protocol game 
                PICK,NEXT,SET,              //protocol play
                END,                   //protocol mark
	}	
        
        public enum TYPE_CARDS {
            Spades, Clubs, Diamonds, Hearts
        }
}
