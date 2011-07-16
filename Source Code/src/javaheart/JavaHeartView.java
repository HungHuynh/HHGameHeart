/*
 * JavaHeartView.java
 */
package javaheart;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javaheart.Game.Global;
import javaheart.Game.Global.MESSAGE_TYPES;
import javaheart.Game.Global.POSITION;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javaheart.Network.Process;
import javaheart.Game.Card;
import javaheart.Network.BOT;
import javaheart.Game.Gamer;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**
 * The application's main frame.
 */
public final class JavaHeartView extends FrameView implements MouseListener, KeyListener {

    //nxqd :
    private List shuffledCards;
    //MItHOt :
    private ArrayList<Gamer> gamers;
    private ArrayList<ArrayList<JLabel>> labels;
    private String IP;
    private String name;
    private int currentGameID = 0;
    private int numPlayer = 1;
    private Vector data = new Vector();
    private Card cardCurrent = null;
    private ArrayList<Process> serverProcess;
    private Process clientGame;
     //client = true | server = false
    private boolean clientMode = Boolean.FALSE;
    private boolean flagSet = Boolean.TRUE;
    private boolean rotate = Boolean.TRUE;
    private boolean loadRotate = Boolean.TRUE;
    private boolean viewGui = Boolean.FALSE;
    private boolean pick = Boolean.TRUE;
    private int numBOT = 0;
    //Thread game : 
    private Thread threadClient;
    private Thread gamer1, gamer2, gamer3;
    //GUI :
    private JTextArea textarea;
    private JTextField textSend;
    private JButton buttonPlay;
    private JButton createServerBtn;
    private JButton connectServerBtn;
    private JButton chatButton;
    private JButton outGameBtn;
    private JTextField nameField;
    private JTextField ipField;
    private JButton buttonBOT;

    public JavaHeartView(SingleFrameApplication app) {
        super(app);
        initComponents();

        /* Our program begin here */
        Gamer.setResourceMap(getResourceMap());
        /* Get the shuffled cards here */
        //ham soc bai cua dung phai khong ?
        shuffledCards = Global.getShuffledCards();

        /* -----------------------------------------------
        GUI
        ----------------------------------------------- */
        // textarea
        textarea = new JTextArea();
        textarea.setBounds(700, 150, 314, 170);
        textarea.setFont(new Font("Tahoma", Font.PLAIN, 10));
        textarea.setEditable(false);
        DefaultCaret caret = (DefaultCaret) textarea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        mainPanel.add(textarea);

        //textbox message : 
        textSend = new JTextField();
        textSend.setBounds(700, 325, 210, 50);
        textSend.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(textSend);

        //textbox ip & name : 
        nameField = new JTextField("Nhập Tên");
        nameField.setBounds(775, 20, 175, 25);
        mainPanel.add(nameField);

        ipField = new JTextField("127.0.0.1");
        ipField.setBounds(775, 55, 175, 25);
        mainPanel.add(ipField);

        //button send chat : 
        chatButton = new JButton("Chat");
        chatButton.setBounds(915, 325, 100, 50);
        chatButton.setEnabled(false);
        chatButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                SendChat();
            }
        });
        mainPanel.add(chatButton);

        //quit buttonPlay 
        outGameBtn = new JButton("Rời Sòng");
        outGameBtn.setBounds(920, 400, 100, 50);
        outGameBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                if (viewGui) {
                    try {
                        String message;
                        if (clientMode) {
                            message = "QUIT#" + name + " đã rời khỏi sòng bài, anh em ta chia tiền lật sòng.";
                            clientGame.SendMessage(message);
                        } else {
                            message = "QUIT#Sòng bài đã đóng cửa và chạy quịt nợ anh em rồi.";
                            ServerMessage(message, clientGame);
                        }
                        System.exit(0);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Quit game.");
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });
        mainPanel.add(outGameBtn);

        /* ADD BOT play game: */
        buttonBOT = new JButton("Tạo BOT");
        buttonBOT.setBounds(810, 400, 100, 50);
        // create BOT
        buttonBOT.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                if (numBOT < 4) {
                    numBOT++;
                    Thread createBOT = new Thread(CreateBOT);
                    createBOT.start();
                    if (numPlayer == 4) {
                        buttonBOT.setEnabled(false);
                        buttonPlay.setEnabled(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Đã đủ người chơi nên không thể tạo thêm BOT !!!");
                }
            }
        });
        mainPanel.add(buttonBOT);

        //create buttonPlay network :
        connectServerBtn = new JButton("Mở Sòng");
        connectServerBtn.setBounds(740, 90, 110, 40);
        createServerBtn = new JButton("Vào Sòng");
        createServerBtn.setBounds(870, 90, 110, 40);

        ActionListener actionConnect = new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                IP = ipField.getText();
                name = nameField.getText();

                if ("IP Server".equals(IP)) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập địa chỉ của sòng bài.");
                    return;
                }

                if ("Nhập Tên".equals(name)) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập tên của người chơi.");
                    return;
                }
                buttonBOT.setEnabled(false);
                viewGui = Boolean.TRUE;
                clientMode = Boolean.TRUE;
                threadClient = new Thread(connectClient);
                threadClient.start();
            }
        };
        createServerBtn.addActionListener(actionConnect);

        ActionListener actionListen = new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                name = nameField.getText();
                if ("Nhập Tên".equals(name)) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập tên của người chơi.");
                    return;
                }
                clientMode = false;
                SetChat("Sòng bài đã mở và chủ sòng đang cầm thẻ số 0.");
                serverProcess = new ArrayList<Process>();
                serverProcess.add(new Process(""));
                serverProcess.add(new Process(""));
                serverProcess.add(new Process(""));

                gamer1 = new Thread(threadPlayer1);
                gamer1.start();
                serverProcess.get(0).setWay(false);
                gamer2 = new Thread(threadPlayer2);
                gamer2.start();
                gamer3 = new Thread(threadPlayer3);
                gamer3.start();

                viewGui = Boolean.TRUE;
                setEnabled(false, true);
                buttonBOT.setEnabled(true);
                buttonPlay.setEnabled(false);
                MouseClickCardListener();
            }
        };
        connectServerBtn.addActionListener(actionListen);

        mainPanel.add(createServerBtn);
        mainPanel.add(connectServerBtn);

        /* -----------------------------------------------
        START GAME :
        ----------------------------------------------- */
        createGame(currentGameID, name);

        /* play buttonPlay */
        buttonPlay = new JButton("Đánh Bài");
        buttonPlay.setBounds(700, 400, 100, 50);
        buttonPlay.setEnabled(false);
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //begin play new SET : mode server
                try {
                    if (flagSet && !clientMode && numPlayer == 4) {
                        Gamer.semaphore = true;
                        buttonPlay.setEnabled(false);
                        buttonBOT.setEnabled(false);
                        flagSet = false;
                        setJLabel("");
                        ResetCards();
                        
                        for (Gamer gamer : gamers) {
                            if (gamer.getId() != 0) {
                                gamer.Prepare(shuffledCards);
                                sendMessageID(gamer.arrayCard(), gamer.getId());
                            }
                        }
                        ////begin rotate :
                        //Server tính toán nếu có hoán vị bài sẽ chuẩn bị để hoán bài :
                        //sau đó đợi nhận từ client message rotate và bắt đầu hoán vị trí gửi để
                        //Client nhận bài bằng message rotate
                        if (Gamer.numSET % 4 != 0) {
                            rotate = true;
                            Process.numPlay = 0;
                            Process.numGamer = 1;
                            gamers.get(currentGameID).addMessage("Bạn hãy chọn 3 quân bài để hoán đổi bài."); //end
                            return;
                        } else {
                            for (int i = 0; i < 4; i++) {
                                for (int j = 1; j < 4; j++) {
                                    if (j != i) {
                                        sendMessageID(gamers.get(i).arrayCard(), j);
                                    }
                                }
                            }
                            messagePlay();
                        }
                    } else {
                        Thread playgame = new Thread(playGamer);
                        playgame.start();
                    }
                } catch (Exception e) {
                    return;
                }
            }
        };
        buttonPlay.addActionListener(actionListener);
        mainPanel.add(buttonPlay);
    }
    /* Network play */
    static MESSAGE_TYPES messageType;
    //Process of player in case client : 
    Runnable connectClient = new Runnable() {

        public void run() {
            //setChat("đang liên lạc với chủ sòng bài ...");            
            clientGame = new Process(name);
            if (!clientGame.Connect(IP)) {
                return;
            }
            // Set buttonPlay properties
            setEnabled(false, true);
            try {
                String receMessage;
                while (true) {
                    receMessage = clientGame.ReceiveMessage();
                    messageType = clientGame.ProcessMessage();

                    switch (messageType) {
                        case PICK:
                            playGamePick(clientGame);
                            break;
                        case NEXT:
                            playGamePick(clientGame);
                            playGameNext();
                            break;
                        case SET:
                            playGamePick(clientGame);
                            playGameNext();
                            playGameSet();
                            buttonPlay.setEnabled(false);
                            break;
                        case END:
                            break;
                        case NULL:
                            break;
                        case BEGIN:
                            clientGame.SendMessage("NAME#" + name);
                            break;
                        case NAME:
                            setIDGame(clientGame.getDataMessage());
                            break;
                        case CHAT:
                            SetChat(clientGame.getMessage());
                            break;
                        case QUIT:
                            SetChat(clientGame.getMessage());
                            outGameBtn.setEnabled(true);
                            return;
                        case ROTATE: // get 3 cards an reload view
                            //SetChat(receMessage);
                            LoadExchangedCards(clientGame.getDataMessage(), currentGameID);
                            flagSet = false;
                            break;
                        case PLAY:
                            buttonPlay.setEnabled(true);
                            Gamer.semaphore = true;
                            gamers.get(currentGameID).addMessage("Đến lượt bạn đánh nước bài đầu tiên với 2 chuồn.");
                            break;
                        case ID:
                            setIDName(clientGame.getDataMessage());
                            setIDGame(clientGame.getDataMessage());
                            SetChat("Bạn đã vào sòng bài và đang cầm thẻ số : " + currentGameID);
                            SwapPosition();
                            MouseClickCardListener();
                            break;
                        case GAME:
                            if (clientGame.getID() == currentGameID) {
                                buttonPlay.setEnabled(false);
                                outGameBtn.setEnabled(false);
                                SetChat("Bắt đầu đánh bài, bạn nhấn vào vị trí bài của bạn để hiện bài");
                                setJLabel("");
                                //begin rotate :
                                if (Gamer.numSET % 4 != 0) {
                                    rotate = true;
                                    gamers.get(currentGameID).addMessage("Bạn hãy chọn 3 quân bài để hoán đổi bài.");
                                } else {
                                    clientGame.SendMessage("GAME#" + currentGameID);
                                }
                            }
                            SetChat(receMessage);
                            setCards(clientGame.getDataMessage(), clientGame.getID());
                            break;
                    }
                }
            } catch (Exception ex) {
                return;
            }
        }
    };
    //Create 3 thread for 3 player in case server : 
    Runnable threadPlayer1 = new Runnable() {

        public void run() {
            ProcessGame(serverProcess.get(0));
        }
    };
    Runnable threadPlayer2 = new Runnable() {

        public void run() {
            ProcessGame(serverProcess.get(1));
        }
    };
    Runnable threadPlayer3 = new Runnable() {

        public void run() {
            ProcessGame(serverProcess.get(2));
        }
    };
    Runnable CreateBOT = new Runnable() {

        public void run() {
            try {
                BOT newBOT;
                try {
                    newBOT = new BOT(numBOT);
                } catch (InterruptedException ex) {
                    if (Global.debug) { System.out.println("Error add BOT "); }
                }
            } catch (IOException ex) {
                if (Global.debug) { System.out.println("Error add BOT "); }
            }
        }
    };

    //Process of 3 player in case SERVER : 
    public void ProcessGame(Process game) {
        try {
            //waiting connect from payer :
            if (!game.Listenning()) {
                CreateGamer(game);
            }
            try {
                gamers.get(game.getID()).setIDName(game.getID(), game.getName());
            } catch (ArrayStoreException ex) {
                return;
            }
            //Begin create player game :
            if (numPlayer < 4) {
                numPlayer++;
                if (numPlayer == 4) {
                    buttonBOT.setEnabled(false);
                    buttonPlay.setEnabled(true);
                }
            } else {
                sendMessageID("QUIT#Sòng bài đã đủ tay chơi, mời bạn hãy sòng khác", game.getID());
            }

            String receiveMessage;
            while (true) {
                receiveMessage = game.ReceiveMessage();
                messageType = game.ProcessMessage();
                switch (messageType) {
                    case NULL:
                        break;
                    case END:
                        break;
                    case GAME:
                        ServerMessage(receiveMessage, game);
                        //if(Global.debug) SetChat(receiveMessage);
                        loadCardClient(game.getDataMessage(), game);
                        setJLabel("");
                        break;
                    case PICK:
                        if(Global.debug) SetChat(receiveMessage);
                        ServerMessage(receiveMessage, game);
                        while (!pick) Thread.sleep(1000);
                        playGamePick(game);
                        break;
                    case NEXT:
                        ServerMessage(receiveMessage, game);
                        playGamePick(game);
                        playGameNext();
                        break;
                    case SET:
                        ServerMessage(receiveMessage, game);
                        playGamePick(game);
                        playGameNext();
                        playGameSet();
                        buttonPlay.setEnabled(true);
                        break;
                    case NAME:
                        ServerMessage(receiveMessage + "#" + game.getID(), game);
                        game.SendMessage(SendIdName(game));
                        SetChat(game.getMessage());
                        break;
                    case CHAT:
                        SetChat(game.getMessage());
                        ServerMessage(receiveMessage, game);
                        break;
                    case QUIT:
                        ServerMessage(receiveMessage, game);
                        outGameBtn.setEnabled(true);
                        SetChat(game.getMessage());
                        CreateGamer(game);
                        numPlayer--;
                        break;
                    case ROTATE:
                        //if(Global.debug) SetChat(receiveMessage);
                        Process.numGamer++;
                        messageRotate(receiveMessage, game);
                        break; 
                }
            }
        } catch (Exception ex) {
            return;
        }
    }
    //pick card and send message :
    Runnable playGamer = new Runnable() {

        public void run() {
            try {
                //pick a card on your hand
                cardCurrent = gamers.get(currentGameID).getPicksCard();
                if (cardCurrent == null) {
                    Gamer.semaphore = true;
                    return;
                } else {
                    Gamer.semaphore = false;
                    gamers.get(currentGameID).MoveCard();
                    gamers.get(currentGameID).CheckHeartBroken();
                    gamers.get(currentGameID).removeMessage();//end NEXT
                }
                buttonPlay.setEnabled(false);

                if (Process.getNumCardonBoard() == 3) { //end of SET
                    int idNext = Process.getNumCardonBoard() + 1;
                    //end the SET :
                    if (gamers.get(currentGameID).getNumCards() == 1) {
                        //SET#ID#nameCard
                        sendPickNextSet("SET#", cardCurrent.getName(), idNext);
                        playGameNext();
                        playGameSet();
                    } else {
                        //NEXT#ID#nameCard
                        sendPickNextSet("NEXT#", cardCurrent.getName(), idNext);
                        playGameNext();
                    }
                } else {
                    //PICK#ID#nameCard#numCard#charType
                    sendPickNextSet("PICK#", cardCurrent.getName(), Process.getNumCardonBoard() + 1);
                    if (Process.getNumCardonBoard() == 0) {
                        Gamer.setCardTypeOnboard(gamers.get(currentGameID).getPicksCard().getName().charAt(0));
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Bạn bị lỗi đánh bài !!!");
                return;
            }
        }
    };

    //mode Client : 
    // 0 - 1 - 2 - 3 : Server send message rotate
    public void ExchangeCards() throws IOException {
        gamers.get(currentGameID).removeMessage(); //end MESSAGE
        if (clientMode) {
            Process.numGamer = 4;
        } else {
            Process.numGamer++;
        }
        viewGui = Boolean.FALSE;
        String exchangeMesssage = "ROTATE#" + currentGameID;
        for (String nameCard : gamers.get(currentGameID).rotateCards) {
            exchangeMesssage += "#" + nameCard;
            gamers.get(currentGameID).pickCard(nameCard);
            gamers.get(currentGameID).getPicksCard().getJLabel().setVisible(false);
            gamers.get(currentGameID).RemovePickedcard();
        }
        if (clientMode) //mode client
        {
            clientGame.SendMessage(exchangeMesssage);
        } else {
            if (Gamer.numSET == 1 || Gamer.numSET % 3 == 1) {
                sendMessageID(exchangeMesssage, 1);
                return;
            }
            if (Gamer.numSET == 2 || Gamer.numSET % 3 == 2) {
                sendMessageID(exchangeMesssage, 3);
                return;
            }
            if (Gamer.numSET == 3 || Gamer.numSET % 3 == 3) {
                sendMessageID(exchangeMesssage, 2);
                return;
            }
        }
    }

    //after receive card, remove old card and load new card :
    public void LoadExchangedCards(String[] card, int id) throws InterruptedException, IOException {
        if (clientMode) 
            while (Process.numGamer < 4) { Thread.sleep(2000); }
        else
             while (viewGui) { Thread.sleep(2000); }
        viewGui = Boolean.TRUE;
        //delete view and save name Card :
        String[] cards = new String[15];
        System.arraycopy(card, 0, cards, 0, 5);
        for (int i = 5; i < 15; i++) {
            cards[i] = gamers.get(id).getNameCard(i - 5);
        }
        for (int i = 0; i < 10; i++) {
            gamers.get(id).pickCard(cards[i + 5]);
            gamers.get(id).getPicksCard().getJLabel().setVisible(false);
            gamers.get(id).RemovePickedcard();
        }

        gamers.get(id).RemoveAllCard();
        //load card : 
        gamers.get(id).prepareClient(cards);
        labels.get(id).clear();
        labels.get(id).addAll(gamers.get(id).getCardLabels());
        gamers.get(id).EnterBoard();
        for (int j = 0; j < labels.get(id).size(); j++) {
            labels.get(id).get(j).addMouseListener(this);
        }

        //send new cards to server or client :
        if ( clientMode ) { clientGame.SendMessage(gamers.get(id).arrayCard()); } 
        else {   
            for (int j = 1; j < 4; j++) {
                if (j != id) {
                    sendMessageID(gamers.get(id).arrayCard(), j);
                }
            }
        }
    }

    //mỗi thread game có nhiệm vụ tính toán và gửi message : 
    public void messageRotate(String message, Process game) throws IOException, InterruptedException {
        // wait till 3 players send cards
        while ( Process.numGamer < 4 &&  loadRotate ) { Thread.sleep(2000); }
        
        if (Global.debug) { System.out.println("messageRotate : id = " + game.getID()); }
        
        if (Gamer.numSET == 1 || Gamer.numSET % 4 == 1) {
            if (game.getID() == 3) {
                LoadExchangedCards(game.getDataMessage(), currentGameID);
            } else {
                sendMessageID(message, game.getID() + 1);
            }

        } else if (Gamer.numSET == 2 || Gamer.numSET % 4 == 2) {
            if (game.getID() == 1) {
                LoadExchangedCards(game.getDataMessage(), currentGameID);
            } else {
                sendMessageID(message, game.getID() - 1);
            }

        } else if (Gamer.numSET == 3 || Gamer.numSET % 4 == 3) {
            if (game.getID() == 2) {
                LoadExchangedCards(game.getDataMessage(), currentGameID);
            }

            if (game.getID() == 1) {
                sendMessageID(message, game.getID() + 2);
            }

            if (game.getID() == 3) {
                sendMessageID(message, game.getID() - 2);
            }
        }
    }

    //after rotate card on server game, start play game :
    public void loadCardClient(String[] cards, Process game) throws IOException, InterruptedException {
        while (Process.numGamer < 4) { Thread.sleep(2000); }
        Process.numPlay += 1;
        int id = game.getID();

        for (int i = 0; i < 13; i++) {
            gamers.get(id).pickCard(gamers.get(id).getNameCard(13));
            gamers.get(id).RemovePickedcard();
        }

        gamers.get(id).prepareClient(cards);
        if (Process.numPlay == 3) {
            messagePlay();
        }
    }

    //Found client game has card CTWO to begin play game : 
    public void messagePlay() throws IOException {
        //Found gamer has CTWO to firts play game :
        buttonPlay.setEnabled(false);
        Gamer.semaphore = false;
        for (int id = 1; id < gamers.size(); id++) {
            // The gamer with TWO CLUB will play first
            if (gamers.get(id).foundCard("CTWO")) {
                for (Process server : serverProcess) {
                    if (server.getID() == id) {
                        server.SendMessage("PLAY#");
                        break;
                    }
                }
                //gamers.get(currentGameID).addMessage("Đến lượt đánh của " + gamers.get(id).getName());
                return;
            }
        }
        buttonPlay.setEnabled(true);
        Gamer.semaphore = true;
        gamers.get(currentGameID).addMessage("Đến lượt bạn đánh nước bài đầu tiên với 2 chuồn.");
    }

    //End the SET game to change new SET of game : 
    public void playGameSet() {
        //load cards :
        for (Gamer gamer : gamers) {
            if (!"Card#0".equals(gamer.getStoreCards())) {
                gamer.prepareClient(gamer.getStoreCard());
                gamer.EnterBoard();
            }
            gamer.nameClientJLabel(gamer.getName() + " : " + gamer.getPoint() + " điểm");
        }
        
        Gamer.reportSET(gamers);
        loadRotate = Boolean.TRUE;
        if (currentGameID != 0) {
            buttonPlay.setEnabled(false);
        }
        else {
            buttonPlay.setEnabled(true);
            shuffledCards = Global.getShuffledCards();
        }
        JOptionPane.showMessageDialog(null, "Đánh ván tiếp theo");
        //doan nay ket thuc va bao hieu choi ser moi
        
        //delete view :
        Gamer.deleteReportSET();
        for (Gamer gamer : gamers) {
            gamer.HeartBrokenMessage("");
            ArrayList<Card> temp = gamer.getAllCard();
            for (int i = 0; i < temp.size(); i++) {
                temp.get(i).getJLabel().setVisible(false);
            }
            gamer.resetSET();
            gamer.nameClientJLabel("");
        }
        flagSet = true;
    }

    //End of times : 
    public void playGameNext() throws InterruptedException {
        //NEXT#ID#nameCard
        Gamer.semaphore = false;
        int id = -1, max = - 1, point = 0;
        char type = Gamer.getCardTypeOnboard();
        //Found max card in times :
        for (int i = 0; i < 4; i++) {
            if (gamers.get(i).getPicksCard().getName().charAt(0) == type) {
                for (int j = max + 1; j < 13; j++) {
                    String temp = type + Gamer.indexCards[j];
                    if (gamers.get(i).getPicksCard().getName().equals(temp)) {
                        if (max < j) {
                            id = i;
                            max = j;
                        }
                    }
                }
            }
        }
        //Calculate point of times : 
        String card = "";
        for (Gamer game : gamers) {
            if (game.getPicksCard().getName().charAt(0) == 'H') {
                point += 1;
                card += "#" + game.getPicksCard().getName();
            }
            if ("SQUEEN".equals(game.getPicksCard().getName())) {
                point += 13;
                card += "#" + game.getPicksCard().getName();
            }
        }
        //Store card to end SET show on board :
        if (id > -1) {
            gamers.get(id).setStoreCards(card);
            gamers.get(id).setPoint(point);
        }
        //Semaphore GUI :
        viewGui = Boolean.TRUE;
        pick = Boolean.FALSE;
        //delete cards on board :
        Process.setNumCardonBoard(0);
        Gamer.setCardTypeOnboard('\u0000');
        new java.util.Timer().schedule(new java.util.TimerTask() {

            @Override
            public void run() {
                for (Gamer gamer : gamers) {
                    gamer.getPicksCard().getJLabel().setVisible(false);
                    gamer.RemovePickedcard();
                }
                gamers.get(currentGameID).updatePoint();
                viewGui = Boolean.FALSE;
                pick = Boolean.TRUE;
            }
        }, 2000);

        if (currentGameID == id) {
            while (this.viewGui) {
                Thread.sleep(1500);
            }
            buttonPlay.setEnabled(true);
            Gamer.semaphore = true;
            gamers.get(currentGameID).addMessage("Đến lượt đánh của bạn ..."); //end in playGamer
        } else {
            this.buttonPlay.setEnabled(false);
        }
    }

    //Receive pick card from server game :
    public void playGamePick(Process process) throws InterruptedException {
        //PICK#ID#nameCard#numCard#charType
        //gamers.get(currentGameID).removeMessage();
        String nameCard = process.getMessage();
        int id = process.getID(),nextID;

        gamers.get(id).pickCard("BLUE");
        gamers.get(id).getPicksCard().getJLabel().setVisible(false);
        gamers.get(id).RemovePickedcard();

        if (Process.getNumCardonBoard() == 1) {
            Gamer.setCardTypeOnboard(process.getTypeCard());
        }
        gamers.get(id).pickCard(nameCard);
        gamers.get(id).CheckHeartBroken();
        gamers.get(id).MoveCard();
        gamers.get(id).EnterCard(nameCard);
        gamers.get(currentGameID).Click(gamers.get(id).getName(id) + " đánh quân :");
        
        if ((process.newTimer == false) && ((currentGameID == (id + 1)) || (id == 3 && currentGameID == 0))) {
            buttonPlay.setEnabled(true);
            Gamer.semaphore = true;
            gamers.get(currentGameID).addMessage("Đến lượt đánh của bạn ...");//end
        } else {
            //gamers.get(currentGameID).addMessage("Đến lượt đánh của " + gamers.get(id + 1).getName());//end
            buttonPlay.setEnabled(false);
            Gamer.semaphore = false;
        }
    }

    //delete card view to show your cards :
    public void setCards(String[] card, int id) {
        if (this.currentGameID == id) {
            for (int i = 0; i < 13; i++) {
                gamers.get(id).pickCard("BLUE");
                gamers.get(id).getPicksCard().getJLabel().setVisible(false);
                gamers.get(id).RemovePickedcard();
            }

            gamers.get(id).setPlay(false);
            gamers.get(id).prepareClient(card);
            labels.get(id).clear();
            labels.get(id).addAll(gamers.get(id).getCardLabels());
            gamers.get(id).EnterBoard();

            for (int j = 0; j < labels.get(id).size(); j++) {
                labels.get(id).get(j).addMouseListener(this);
            }
        } else {
            gamers.get(id).setPlay(false);
            gamers.get(id).prepareClient(card);
        }
    }

    //Create object game to play game :
    public void createGame(int id, String name) {
        gamers = new ArrayList<Gamer>();
        gamers.add(new Gamer(id, name, POSITION.BOTTOM, mainPanel));
        gamers.add(new Gamer(-1, "", POSITION.LEFT, mainPanel));
        gamers.add(new Gamer(-1, "", POSITION.TOP, mainPanel));
        gamers.add(new Gamer(-1, "", POSITION.RIGHT, mainPanel));
        for (Gamer gamer : gamers) {
            gamer.LoadGame();
            gamer.EnterBoard();
        }
    }

    //Create message to send server game :
    public void sendPickNextSet(String MESSAGE, String nameCard, int numCard) {
        try {
            if (!"".equals(nameCard) && numCard != 0) {
                MESSAGE += currentGameID + "#" + nameCard + "#" + numCard + "#" + nameCard.charAt(0);
            }
            if (clientMode == true) {
                clientGame.SendMessage(MESSAGE);
            } else {
                ServerMessage(MESSAGE, clientGame);
            }
        } catch (IOException ex) {
            return;
        }
    }

    //Send message to gamer if know ID of gamer :
    public void sendMessageID(String message, int id) throws IOException {
        for (Process server : serverProcess) {
            if (server.getStatus() && server.getID() == id) {
                server.SendMessage(message);
            }
        }
    }

    public void ResetCards() {
        if (gamers.get(currentGameID).getPlay()) {
            for (int i = 0; i < 13; i++) {
                gamers.get(currentGameID).pickCard("BLUE");
                gamers.get(currentGameID).getPicksCard().getJLabel().setVisible(false);
                gamers.get(currentGameID).RemovePickedcard();
            }
            gamers.get(currentGameID).setPlay(false);
            gamers.get(currentGameID).Prepare(shuffledCards);
        }

        labels.get(currentGameID).clear();
        labels.get(currentGameID).addAll(gamers.get(currentGameID).getCardLabels());
        gamers.get(currentGameID).EnterBoard();

        for (int j = 0; j < labels.get(currentGameID).size(); j++) {
            labels.get(currentGameID).get(j).addMouseListener(this);
        }
    }

    public void MouseClickCardListener() {
        labels = new ArrayList<ArrayList<JLabel>>(4);
        int i = 0;
        for (Gamer gamer : gamers) {
            labels.add(gamer.getCardLabels());
            for (int j = 0; j < labels.get(i).size(); j++) {
                labels.get(i).get(j).addMouseListener(this);
            }
            i++;
        }
    }

    //Swap position of gamer with server game :
    public void SwapPosition() {
        POSITION position = gamers.get(currentGameID).getPOSITION();
        gamers.get(currentGameID).setPOSITION(gamers.get(0).getPOSITION());
        gamers.get(0).setPOSITION(position);

        ArrayList<Card> cards = gamers.get(currentGameID).getAllCard();
        gamers.get(currentGameID).setAllCard(gamers.get(0).getAllCard());
        gamers.get(0).setAllCard(cards);
    }

    /* IDName[1] = yourname, IDName[2] = yourId */
    public void setIDName(String[] IDName) {
        if (this.name.equals(IDName[1])) {
            this.currentGameID = Integer.parseInt(IDName[2]);
        }
    }

    public void setIDGame(String[] IDName) {
        for (int i = 1; i < IDName.length - 1; i += 2) {
            gamers.get(Integer.parseInt(IDName[i + 1])).setIDName(Integer.parseInt(IDName[i + 1]), IDName[i]);
        }
    }

    public String SendIdName(Process game) {
        gamers.get(game.getID()).setIDName(game.getID(), game.getName());
        String IDName = "ID#" + game.getName() + "#" + game.getID() + "#";
        IDName += this.name + "#" + this.currentGameID;
        for (Process server : serverProcess) {
            if (server.getStatus() && server != game) {
                IDName += "#" + server.getName() + "#" + server.getID();
            }
        }
        return IDName;
    }

    public void CreateGamer(Process game) throws IOException, InterruptedException {
        for (Process server : serverProcess) {
            if (server.getID() == game.getID()) {
                server.Exit();
                server.Listenning();
            }
        }
    }

    //Server Game send message 3 gamer :
    public void ServerMessage(String message, Process game) throws IOException {
        for (Process server : serverProcess) {
            if (server.getStatus() && server != game) {
                server.SendMessage(message);
            }
        }
    }

    // Helpers
    //Add message chat on Textbox Chat :
    private void SetChat(String chatLine) {
        textarea.append(chatLine + "\n");
        textarea.scrollRectToVisible(textarea.getVisibleRect());
        textarea.setCaretPosition(textarea.getDocument().getLength());
    }

    private void SendChat() {
        try {
            if ("".equals(textSend.getText())) {
                return;
            }
            SetChat(name + ": " + textSend.getText());
            String message = "CHAT#" + name + ": " + textSend.getText();
            textSend.setText("");
            if (clientMode == true) {
                clientGame.SendMessage(message);
            } else {
                ServerMessage(message, clientGame);
            }
        } catch (IOException ex) {
            SetChat("Lỗi gửi message ... !");
            return;
        }
    }

    //Enabled buttonPlay when app start connect :
    private void setEnabled(boolean value1, boolean value2) {
        createServerBtn.setEnabled(value1);
        connectServerBtn.setEnabled(value1);
        nameField.setEnabled(value1);
        ipField.setEnabled(value1);
        chatButton.setEnabled(value2);
        outGameBtn.setEnabled(value2);
    }

    public void setGamerId(int id) {
        currentGameID = id;
        gamers.get(0).setID(id);
    }

    public void setJLabel(String name) {
        for (Gamer game : gamers) {
            game.nameClientJLabel(name);
        }
    }

//-----------------------------End MIt HOt Add basic Network-------------------------------------------------- 
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = JavaHeartApp.getApplication().getMainFrame();
            aboutBox = new JavaHeartAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        JavaHeartApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(null);

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(javaheart.JavaHeartApp.class).getContext().getResourceMap(JavaHeartView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(javaheart.JavaHeartApp.class).getContext().getActionMap(JavaHeartView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    // End of variables declaration//GEN-END:variables
    private JDialog aboutBox;

    // Events
    public void mouseClicked(MouseEvent e) {
        JLabel label = (JLabel) e.getSource();

        if (Gamer.semaphore) {
            if (rotate) {
                gamers.get(currentGameID).Rotate(label.getName());
                if (gamers.get(currentGameID).getSizeRotate() == 3) {
                    Gamer.semaphore = Boolean.FALSE;
                    try {
                        ExchangeCards();
                        rotate = Boolean.FALSE;
                        loadRotate = Boolean.FALSE;
                    } catch (IOException ex) {
                    }
                }
            } else {
                gamers.get(currentGameID).Pick(label.getName());
                if (flagSet == false) {
                    Gamer.semaphore = false;
                    Thread playgame = new Thread(playGamer);
                    playgame.start();
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        JLabel label = (JLabel) e.getSource();
        label.setBorder(BorderFactory.createEtchedBorder(Color.lightGray, Color.yellow));
    }

    public void mouseExited(MouseEvent e) {
        JLabel label = (JLabel) e.getSource();
        label.setBorder(BorderFactory.createLineBorder(Color.yellow, 0));
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
}
