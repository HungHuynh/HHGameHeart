/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaheart.Network;

import java.io.*;
import java.net.*;
import javaheart.Game.Global.MESSAGE_TYPES;
import javax.swing.JOptionPane;
import javaheart.Game.Global;

public class Process {
	//Port server = 9000;
	private int id;
	private Socket s;
	private String localhost;
	private String name;
	private BufferedWriter bw;
	private BufferedReader br;
	private static boolean way;
	private boolean status;
	private String recvMessage;
	private String sendMessage;
	private String[] IDName;
	private static int numCardsonBoard = 0;
	private char typeCard;
	private int IdNext;
	public static int numGamer = 0;
	public static int numPlay = 0;
	public boolean newTimer;

	// Getters and setters
	public void setWay(boolean b) { Process.way = b; }
	public void setID(int id) { this.id = id; }
	public void setName(String name) { this.name = name; }
	public static void setNumCardonBoard(int numCardsonBoard) { Process.numCardsonBoard = numCardsonBoard; }

	public boolean getStatus() { return this.status; }
	public String getMessage() { return this.sendMessage; }
	public char getTypeCard() { return typeCard; }
	public static int getNumCardonBoard() { return numCardsonBoard; }
	public int getIdNext() { return IdNext; }
	public String[] getDataMessage() { return IDName; }
	public int getID() { return this.id; }
	public String getName() { return name; }


	// Constructors
	public Process(String clientName) {
		id = 0;
		way = Boolean.TRUE;
		name = clientName;
		status = Boolean.FALSE;
		sendMessage = "";
		recvMessage = "";
	}

	// Public methods
	// > Case Server : Create thread listen when client connect server game :
	public boolean Listenning() {
		try {
			while (way) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					status = Boolean.FALSE;
					return Boolean.FALSE;
				}
			}

			Process.way = Boolean.TRUE;
			numGamer++;
			ServerSocket S = new ServerSocket(9000);
			if (Global.debug) { System.out.println("Server Game begin Listen : "); }
			this.s = S.accept();
			this.CreateIOStream();
			this.SendMessage("BEGIN#Chào mừng bạn đã đến với sòng bài !");
			if (this.id == 0) { this.id = numGamer; }
			Process.way = Boolean.FALSE;
			this.status = Boolean.TRUE;
			S.close();
			return Boolean.TRUE;
		} catch (IOException e) {
			// "Sòng bài đã không chấp nhận người chơi vì một lý do nào đó !!!");
			Process.way = Boolean.FALSE;
			this.status = Boolean.FALSE;
			return Boolean.FALSE;
		}
	}

	//  > Case Client : Client connect Server Game.
	public boolean Connect(String hostName) {
		try {
			this.localhost = hostName;
			this.s = new Socket(hostName, 9000);
			this.CreateIOStream();
			return Boolean.TRUE;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Địa chỉ của sòng bài không đúng !!!");
			return Boolean.FALSE;
		}
	}

	public MESSAGE_TYPES ProcessMessage() {
		try {
			String[] sign = null;
			if (recvMessage != null || !"".equals(recvMessage)) {
				sign = recvMessage.split("#");
				if (sign.length > 1) {
					if (sendMessage.equals(sign[1])) {
						sendMessage = "";
					} else { sendMessage = sign[1]; }
				}
			} else { return MESSAGE_TYPES.NULL; }

			if ("ROTATE".equals(sign[0])) {
				this.id = Integer.parseInt(sign[1]);
				this.IDName = sign;
				return MESSAGE_TYPES.ROTATE;
			} else if ("SET".equals(sign[0])) {
				this.id = this.id = Integer.parseInt(sign[1]);
				sendMessage = sign[2];
				return MESSAGE_TYPES.SET;
			} else if ("NEXT".equals(sign[0])) {
				this.id = Integer.parseInt(sign[1]);
				sendMessage = sign[2];
				newTimer = Boolean.TRUE;
				return MESSAGE_TYPES.NEXT;
			} else if ("PICK".equals(sign[0])) {
				newTimer = Boolean.FALSE;
				this.id = Integer.parseInt(sign[1]);
				sendMessage = sign[2];
				numCardsonBoard = Integer.parseInt(sign[3]);
				if (numCardsonBoard == 1) { this.typeCard = sign[4].charAt(0); }
				return MESSAGE_TYPES.PICK;
			} else if ("PLAY".equals(sign[0])) {
				numCardsonBoard = 0;
				return MESSAGE_TYPES.PLAY;
			} else if ("GAME".equals(sign[0])) {
				this.IDName = sign;
				this.id = Integer.parseInt(sign[1]);
				return MESSAGE_TYPES.GAME;
			} else if ("CHAT".equals(sign[0])) {
				return MESSAGE_TYPES.CHAT;
			} else if ("BEGIN".equals(sign[0])) {
				return MESSAGE_TYPES.BEGIN;
			} else if ("ID".equals(sign[0])) {
				if (id == 0) { id = Integer.parseInt(sign[2]); }
				this.IDName = sign;
				return MESSAGE_TYPES.ID;
			} else if ("QUIT".equals(sign[0])) {
				status = Boolean.FALSE;
				return MESSAGE_TYPES.QUIT;
			} else if ("NAME".equals(sign[0])) {
				name = sign[1];
				sendMessage = name + " đã cầm thẻ và bước vào sòng bài.";
				if (sign.length > 2) {
					this.IDName = sign;
				}
				return MESSAGE_TYPES.NAME;
			}
			return MESSAGE_TYPES.NULL;
		} catch (Exception ex) { return MESSAGE_TYPES.NULL; }
	}

	public String ReceiveMessage() throws IOException {
		return this.recvMessage = br.readLine();
	}

	public void SendMessage(String message) throws IOException {
		try {
			bw.write(message);
			bw.newLine();
			bw.flush();
                } catch (IOException ex) {
                        if (Global.debug) { System.out.println("Server send message error "); }
		}
	}

	public void Exit() throws IOException {
		if (s != null) {
			s.close();
		}
		if (bw != null && br != null) {
			bw.close();
			br.close();
		}
	}

	// Helpers
	private void CreateIOStream() throws IOException {
		InputStream is = s.getInputStream();
		br = new BufferedReader(new InputStreamReader(is));
		OutputStream os = s.getOutputStream();
		bw = new BufferedWriter(new OutputStreamWriter(os));
	}
}
