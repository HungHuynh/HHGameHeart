/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaheart.Network;

import java.io.IOException;
import java.util.ArrayList;
import javaheart.Game.Gamer;
import javaheart.Game.Gamer;
import javaheart.Game.Global;
import javaheart.Game.Global;
import javaheart.Game.Global.MESSAGE_TYPES;
import javaheart.Network.Process;
import javax.swing.JOptionPane;

public final class BOT {

    private int currentIDBOT;
    private String name;
    private Gamer gamerBOT;
    private Process processBOT;
    private ArrayList<String> cards;
    private String[] indexCards;
    private char typeCard;
    private int numcardOnBoard = 0;
    private static int playGame = 0;
    private static int numBOT = 0;
    private ArrayList<String> timesCards = new ArrayList<String>();
    private String indexTimes;
    private Boolean heartBroken = Boolean.FALSE;
    private char[] cardType = {'H', 'D', 'C', 'S'};

    public BOT(int id) throws IOException, InterruptedException {
        this.currentIDBOT = id;
        name = "BOT " + id;
        cards = new ArrayList<String>();
        if (StartBOT()) {
            indexCards();
            JOptionPane.showMessageDialog(null, "đã tạo BOT");
            PlayGame();
        }
    }

    private boolean StartBOT() {
        //Connect server game as client game : 
        processBOT = new Process("BOT " + currentIDBOT);
        if (!processBOT.Connect("127.0.0.1")) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private void PlayGame() throws IOException, InterruptedException {
        MESSAGE_TYPES messageType;
        String receMessage;
        while (true) {
            receMessage = processBOT.ReceiveMessage();
            messageType = processBOT.ProcessMessage();

            switch (messageType) {
                case END:
                    break;
                case NULL:
                    break;
                /*cách chơi qua mạng như sau : 
                 * để Dũng dễ code nên hùng ghi rõ các bước Dũng cần làm ra như sau :
                 * Đây là thứ tự message gửi và nhận trên client và server :
                 * PLAY -> PICK -> PICK -> PICK -> NEXT -> PICK -> PICK -> PICK -> NEXT -> PICK -> PICK ...
                 * 
                 * - Bước 1 : sau khi trao đổi bài thì server tìm client nào có clubs two để send message PLAY.
                 * <Hùng đã xử lý bước 1 của message PLAY, tiếp theo BOT nhận message và gửi message PICK, NEXT>
                 * - Bước 2 : Server nhận các nước đánh của Client BOT và sau đó send lại cho các CLient BOT khác
                 * vậy nên các nước bài tiếp theo là BOT sẽ nhận các message như PICK, đến khi người thứ 4 đánh 
                 * Tức ở lượt đánh cuối thì sẽ send message NEXT để client và server nhận biết. BOT cần xử lý tại 
                 * bước 3 này .
                 * - bước 3 : + nhận message PICK : phân tích message lấy tên card và kiểu bài, 
                 *              kiểm tra ID trong message xem nếu ID + 1 = currentIDBOT của BOT 
                 *              Không ??? nếu bằng thì BOT phải đánh bài và gửi PICK, nếu ID = 3
                 *              thì BOT đánh bài và gửi message NEXT. (gửi PICK Hoặc NEXT)
                 *            + nhận message NEXT : message này nhằm tính điểm nên BOT phải
                 *              tính toán điểm để biết mình có thắng trong lượt (times) này 
                 *              không ??? nếu thắng thì chọn bài đánh với message PICK.
                 *            + nhận message SET : kết thúc SET và reset lại BOT để tiếp tục SET mới.
                 */

                case PICK: //BOT receive card with message : 
                    //Message PICK : PICK#ID#nameCard#numCard#charType
                    if (Global.debug) {
                        System.out.println(name + " receive message PICK: " + receMessage);
                    }
                    GetMessagePICK(processBOT.getDataMessage());

                    break;
                case NEXT: //BOT calculator to know who play next card in new times :
                    //Message NEXT : NEXT#ID#nameCard
                    if (Global.debug) {
                        System.out.println(name + " receive message NEXT : " + receMessage);
                    }
                    GetMessageNEXT(processBOT.getDataMessage());

                    break;
                case SET: //end a SET of BOT and BOT reset to start new SET : 
                    //message SET : SET#ID#nameCard
                    GetMessageSET();
                    break;
                case BEGIN: //Send name BOT for server :
                    processBOT.SendMessage("NAME#" + name);
                    break;
                case ROTATE: // get 3 cards from Server and send server all cards of BOT :
                    SetRotateCards(processBOT.getDataMessage());
                    SendMessage("GAME#" + currentIDBOT + GetAllCards());
                    //if(Global.debug) { System.out.println(name + " all card " + GetAllCards() ); }
                    break;
                case PLAY:
                    //PLAY : BOT first play and choice card Club TWO :
                    SendCards("PICK", "CTWO");
                    typeCard = 'C';
                    indexTimes = "CTWO";
                    RemoveCard("CTWO");
                    //if(Global.debug) { System.out.println(name + " play : " + receMessage); }
                    break;
                case ID:
                    //ID : get ID to BOT
                    if (name.equals(processBOT.getName())) {
                        currentIDBOT = processBOT.getID();
                    }
                    numBOT++;
                    break;
                case GAME:
                    if (processBOT.getID() == currentIDBOT) {
                        //GAME : Get card from Server :
                        GetCards(processBOT.getDataMessage());
                        if (Global.debug) {
                            System.out.println(name + " get cards : " + receMessage);
                        }
                        playGame++;
                        //ROTATE : Get 3 cards to rotate :
                        if (Gamer.numSET % 4 != 0) {
                            while (playGame < numBOT) {
                                Thread.sleep(1000);
                            }
                            String message = GetRotateCards();
                            SendMessage(message);
                            if (Global.debug) {
                                System.out.println(name + "' card rotate send : " + message);
                            }
                        } else {
                            SendMessage("GAME#" + currentIDBOT);
                        }
                    }
                    break;
            }
        }
    }

    public void GetMessageSET() { 
        ResetBOT();
        ResetGame();
    }

    public void ResetGame() {
        playGame = 0;
    }

    //BOT process message PICK in here: 
    private void GetMessagePICK(String[] message) throws IOException, InterruptedException {
        //Message PICK : PICK#ID#nameCard#numCard#charType
        timesCards.add(processBOT.getMessage());
        numcardOnBoard = Process.getNumCardonBoard();
        //set type card on board : 
        if (numcardOnBoard == 1) {
            typeCard = processBOT.getTypeCard();
        }

        //BOT play pick card :
        if (processBOT.getID() + 1 == currentIDBOT) {
            Thread.sleep(2000);
            if (Global.debug) {
                System.out.println(name + "play pick");
            }
            int i = 0;
            for (String card : cards) {
                if (card.charAt(0) == typeCard || i == cards.size() - 1) {
                    timesCards.add(card);
                    indexTimes = card;

                    if (numcardOnBoard < 3) {
                        SendCards("PICK", card);
                    } else {
                        if (cards.size() < 2) {
                            SendCards("SET", card);
                            ResetBOT();
                            return;
                        }
                        SendCards("NEXT", card);

                        //if BOT win in times, BOT play pick card :
                        if (card.equals(FoundMaxCard())) {
                            Thread.sleep(4000);

                            /*BOT choice card play pick in here after the times :
                             * nó chọn con đầu tiên trên bộ bài để đánh
                             * Luot nay ta danh sau : chọn quân bài thấp để đánh
                             * không giành lượt để nhường các quân bài lớn cho đối phương.
                             * 
                             *
                            for(String myCard : cards){
                                if(myCard.charAt(0) != 'H'){
                                }
                            }
                             *
                             */
                            indexTimes = cards.get(0);
                            playCard(indexTimes);

                        }
                    }
                    cards.remove(card);
                    break;
                } else {
                    i++;
                }
            }
        }
    }

    public void playCard(String card) throws IOException {
        //Play card : 
        typeCard = card.charAt(0);
        SendCards("PICK", card);
        timesCards.add(card);
        cards.remove(card);
    }

    //BOT process message NEXT in here : 
    private void GetMessageNEXT(String[] message) throws InterruptedException, IOException {
        //Message NEXT : NEXT#ID#nameCard#numCard#charType
        timesCards.add(processBOT.getMessage());
        Thread.sleep(2000);

        //found a gamer who win in the old times  : 
        if (indexTimes.equals(FoundMaxCard())) {
            /*BOT play pick card in here :
             * BOT chọn con bài đầu tiên để đánh tại đây
             * luot nay ta danh dau : 
             */
            indexTimes = cards.get(0);
            playCard(indexTimes);
        } else {
            if (Global.debug) {
                System.out.println(name + " not play pick ");
            }
        }
    }

    private String FoundMaxCard() {
        int max = 0;
        String myCard = "";
        for (String card : timesCards) {
            if (card.charAt(0) == typeCard) {
                for (int i = max; i < 13; i++) {
                    if (card.equals(typeCard + indexCards[i])) {
                        max = i;
                        myCard = card;
                        break;
                    }
                }
            }
            if (card.charAt(0) == 'H' && !heartBroken) {
                heartBroken = Boolean.TRUE;
            }
        }
        //after found max 
        ResetBOT();
        return myCard;
    }

    public void ResetBOT() {
        //reset BO in times : 
        timesCards.removeAll(timesCards);
        numcardOnBoard = 0;
        typeCard = ' ';
    }

    private void GetCards(String[] cards) {
        for (int i = 2; i < 15; i++) {
            this.cards.add(cards[i]);
        }
    }

    //Send card with 3 card on board, in times of BOT :
    private void SendCards(String TypeMessage, String nameCard) throws IOException {
        numcardOnBoard++;
        String message = TypeMessage + "#" + currentIDBOT + "#" + nameCard + "#" + numcardOnBoard + "#" + nameCard.charAt(0);
        processBOT.SendMessage(message);
        if (Global.debug) {
            System.out.println(message);
        }
    }

    private void SendMessage(String message) throws IOException {
        processBOT.SendMessage(message);
    }

    private void RemoveCard(String nameCard) {
        this.cards.remove(nameCard);
    }

    public void indexCards() {
        String Cards = "TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE,TEN,JACK,QUEEN,KING,ACE";
        indexCards = Cards.split(",");
    }

    //BOT set 3 card receive from Server game :  
    private void SetRotateCards(String[] cardRotate) {
        for (int i = 2; i < 5; i++) {
            cards.add(cardRotate[i]);
        }
    }

    private String GetAllCards() {
        String allCards = "";
        for (String card : cards) {
            allCards += "#" + card;
        }
        return allCards;
    }

    //BOT get 3 card to send Server game :  
    private String GetRotateCards() {
        String rotate = "ROTATE#" + currentIDBOT;
        int numCards = 0, max = 12, min = 7;
        while (true) {
            for (int k = 0; k < 4; k++) {
                for (int i = 0; i < cards.size(); i++) {
                    if (cards.get(i).charAt(0) == cardType[k]) {
                        for (int j = max; j > min; j--) {
                            if (cards.get(i).lastIndexOf(indexCards[j]) > -1) {
                                rotate = rotate + "#" + cards.get(i);
                                cards.remove(cards.get(i));
                                numCards++;
                                if (numCards == 3) {
                                    return rotate;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (numCards < 3) {
                max = 7;
                min = 0;
            }
        }
    }
}
