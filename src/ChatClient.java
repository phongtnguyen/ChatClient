import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

public class ChatClient extends JFrame implements ActionListener, KeyListener {
	JButton sendB = new JButton("Send");
	JButton connectB = new JButton("Connect");
	JButton disconnectB = new JButton("Disconnect");
	//JTextArea write = new JTextArea(2, 40);
	JTextField write = new JTextField(40);
	JTextField displayName = new JTextField(15);
	JScrollPane rScroll = new JScrollPane();
	JPanel center, south;
	Socket socket;
	Thread thread;
	BufferedReader in;
	PrintWriter out;
	String name;
	JLabel n = new JLabel("Name: ");
	JTextPane jtp = new JTextPane();
	
	//20 Emotes
	JButton drool = new JButton("Drool");
	JButton bomb = new JButton("Bomb");
	JButton cat = new JButton("Cat");
	JButton cry = new JButton("Cry");
	JButton sob = new JButton("Sob");
	JButton mewl = new JButton("Mewl");
	JButton egg = new JButton("Egg");
	JButton good = new JButton("Good");
	JButton hot = new JButton("Hot");
	JButton knife = new JButton("Knife");
	JButton love = new JButton("Love");
	JButton rice = new JButton("Rice");
	JButton salute = new JButton("Salute");
	JButton what = new JButton("What");
	JButton yeah = new JButton("Yeah");
	JButton smile = new JButton("Smile");
	JButton shock = new JButton("Shock");
	JButton dream = new JButton("Dream");
	JButton shy = new JButton("Shy");
	JButton die = new JButton("Die");
	boolean smiley;
	
	public ChatClient() {
		setTitle("Chat Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int height = (int)screenSize.getHeight();
		setSize(700, height-450); 
		
		center = new JPanel(new FlowLayout());
		
		//when use JTextArea for write => rSrcoll doesn't work well
		//=> use JTextField for write but still unexpected behaviour often with Image
		jtp.setPreferredSize(new Dimension(650,height-650));
		rScroll.getViewport().add(jtp);
		rScroll.setWheelScrollingEnabled(true);
		DefaultCaret caret = (DefaultCaret) jtp.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); //inactive Windows doesn't update when insert Image
			
		center.add(rScroll);
		add(center, BorderLayout.CENTER);
		
		south = new JPanel(new BorderLayout());
		JPanel southU = new JPanel(new FlowLayout());
		JPanel southD = new JPanel(new FlowLayout());
		
		JPanel lowerThanSouthD = new JPanel(new GridLayout(2,10));
		
		lowerThanSouthD.add(bomb);
		bomb.setEnabled(false);
		bomb.addActionListener(this);
		lowerThanSouthD.add(cat);
		cat.setEnabled(false);
		cat.addActionListener(this);
		lowerThanSouthD.add(cry);
		cry.setEnabled(false);
		cry.addActionListener(this);
		lowerThanSouthD.add(die);
		die.setEnabled(false);
		die.addActionListener(this);
		lowerThanSouthD.add(dream);
		dream.setEnabled(false);
		dream.addActionListener(this);
		lowerThanSouthD.add(drool);
		drool.setEnabled(false);
		drool.addActionListener(this);
		lowerThanSouthD.add(egg);
		egg.setEnabled(false);
		egg.addActionListener(this);
		lowerThanSouthD.add(good);
		good.setEnabled(false);
		good.addActionListener(this);
		lowerThanSouthD.add(hot);
		hot.setEnabled(false);
		hot.addActionListener(this);
		lowerThanSouthD.add(knife);
		knife.setEnabled(false);
		knife.addActionListener(this);
		lowerThanSouthD.add(love);
		love.setEnabled(false);
		love.addActionListener(this);
		lowerThanSouthD.add(mewl);
		mewl.setEnabled(false);
		mewl.addActionListener(this);
		lowerThanSouthD.add(rice);
		rice.setEnabled(false);
		rice.addActionListener(this);
		lowerThanSouthD.add(salute);
		salute.setEnabled(false);
		salute.addActionListener(this);
		lowerThanSouthD.add(shock);
		shock.setEnabled(false);
		shock.addActionListener(this);
		lowerThanSouthD.add(shy);
		shy.setEnabled(false);
		shy.addActionListener(this);
		lowerThanSouthD.add(smile);
		smile.setEnabled(false);
		smile.addActionListener(this);
		lowerThanSouthD.add(sob);
		sob.setEnabled(false);
		sob.addActionListener(this);
		lowerThanSouthD.add(what);
		what.setEnabled(false);
		what.addActionListener(this);
		lowerThanSouthD.add(yeah);
		yeah.setEnabled(false);
		yeah.addActionListener(this);
		
		southU.add(n);
		southU.add(displayName);
		southU.add(connectB);
		southU.add(disconnectB);
		disconnectB.setEnabled(false);
		southD.add(write);
		southD.add(sendB);
		sendB.setEnabled(false);
		
		south.add(southU, BorderLayout.NORTH);
		south.add(southD, BorderLayout.CENTER);
		south.add(lowerThanSouthD, BorderLayout.SOUTH);
		add(south, BorderLayout.SOUTH);
		jtp.setEditable(false);
		write.setText("localhost 9876");
		write.addKeyListener(this); //use keylistener => enter sends message but create a new line
		//=> use JTextField, doesn't create new line
		
		//quick names
		Random random = new Random();
		int r = random.nextInt(100) + 1;
		displayName.setText("Client "+r);
		
		connectB.addActionListener(this);
		disconnectB.addActionListener(this);
		sendB.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent evt) {
		try {
			if (evt.getActionCommand().equals("Connect")) {
				name = displayName.getText();
				if (name.length()<1) {
					jtp.setText("Please enter a user name");
					return;
				}
				Scanner scanner = new Scanner(write.getText());
				if (!scanner.hasNext()) return;
				String host = scanner.next(); //192.168.2.5
				if (!scanner.hasNextInt()) return;
				int port = scanner.nextInt();
				
				socket = new Socket(host, port);
				disconnectB.setEnabled(true);
				connectB.setEnabled(false);
				sendB.setEnabled(true);
				displayName.setEditable(false);
				write.setText("");
				
				smiley = true;
				smileyEnable();
				jtp.setText("Successful connection.\n");
				
				//thread to read data from server
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				thread = new ClientThread(in, jtp);
				thread.start();
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Disconnect")) { //System.out.println("Disconect from the host");
				thread.interrupt();
				socket.close();
				in.close();
				try {
					out.close();
				} catch (NullPointerException n) { //happen if disconnect right after connect
				}
				
				smiley = false;
				smileyEnable();
				
				sendB.setEnabled(false);
				connectB.setEnabled(true);
				disconnectB.setEnabled(false);
				displayName.setEditable(true);
				jtp.setText("");
				write.setText("localhost 9876");
				displayName.setText("");
				displayName.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Send")) {
				out = new PrintWriter(socket.getOutputStream());
				out.println(name + ": " + write.getText());
				out.flush();
				write.setText("");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Drool")) {
				//write.append(":drool:");
				write.setText(write.getText()+":drool:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Bomb")) {
				write.setText(write.getText()+":bomb:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Cat")) {
				write.setText(write.getText()+":cat:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Cry")) {
				write.setText(write.getText()+":cry:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Egg")) {
				write.setText(write.getText()+":egg:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Good")) {
				write.setText(write.getText()+":good:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Hot")) {
				write.setText(write.getText()+":hot:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Knife")) {
				write.setText(write.getText()+":knife:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Love")) {
				write.setText(write.getText()+":love:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Mewl")) {
				write.setText(write.getText()+":mewl:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Rice")) {
				write.setText(write.getText()+":rice:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Salute")) {
				write.setText(write.getText()+":salute:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Sob")) {
				write.setText(write.getText()+":sob:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("What")) {
				write.setText(write.getText()+":what:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Yeah")) {
				write.setText(write.getText()+":yeah:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Smile")) {
				write.setText(write.getText()+":smile:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Shock")) {
				write.setText(write.getText()+":shock:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Dream")) {
				write.setText(write.getText()+":dream:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Shy")) {
				write.setText(write.getText()+":shy:");
				write.requestFocusInWindow();
			}
			if (evt.getActionCommand().equals("Die")) {
				write.setText(write.getText()+":die:");
				write.requestFocusInWindow();
			}
		} catch (UnknownHostException uhe) { //System.out.println("Can't find the host server");
			jtp.setText(uhe.getMessage());
		} catch (IOException io) {
			jtp.setText(io.getMessage());
		}
	}
	
	public void keyPressed(KeyEvent k) {
		if (k.getKeyCode()==KeyEvent.VK_ENTER)
			try {
				out = new PrintWriter(socket.getOutputStream());
				out.println(name + ": " + write.getText());
				out.flush();
				write.setText("");
			} catch (IOException e) {}
	}

	public void keyReleased(KeyEvent k) { 
		//if (k.getKeyCode()==KeyEvent.VK_ENTER) {
		//	write.setText("");
		//}
	}

	public void keyTyped(KeyEvent k) {}
	
	void smileyEnable() {
		if (smiley) {
			bomb.setEnabled(true);
			cat.setEnabled(true);
			cry.setEnabled(true);
			drool.setEnabled(true);
			egg.setEnabled(true);
			good.setEnabled(true);
			hot.setEnabled(true);
			knife.setEnabled(true);
			love.setEnabled(true);
			mewl.setEnabled(true);
			rice.setEnabled(true);
			salute.setEnabled(true);
			sob.setEnabled(true);
			what.setEnabled(true);
			yeah.setEnabled(true);
			smile.setEnabled(true);
			shock.setEnabled(true);
			dream.setEnabled(true);
			shy.setEnabled(true);
			die.setEnabled(true);
		}
		else {
			bomb.setEnabled(false);
			cat.setEnabled(false);
			cry.setEnabled(false);
			drool.setEnabled(false);
			egg.setEnabled(false);
			good.setEnabled(false);
			hot.setEnabled(false);
			knife.setEnabled(false);
			love.setEnabled(false);
			mewl.setEnabled(false);
			rice.setEnabled(false);
			salute.setEnabled(false);
			sob.setEnabled(false);
			what.setEnabled(false);
			yeah.setEnabled(false);
			smile.setEnabled(false);
			shock.setEnabled(false);
			dream.setEnabled(false);
			shy.setEnabled(false);
			die.setEnabled(false);
		}
	}
	
	public static void main(String[] args) {
		ChatClient windows = new ChatClient();
		windows.setVisible(true);
	}
}

/***************************************************************************/

/*		MULTIPLE SMILEYS SOLUTION
 * Put every smiley code in a String ArrayList.
 * 	(Maximum characters for message => message not too long)
 * 	(no Java method for this, must create method)
 * 	(or use this after getText(): message.substring(0, CHARACTER_LIMIT))
 * 	(but not very nice)
 * Put indexOf every code in an Integer ArrayList.
 * Sort Integer ArrayList in ascending order.
 * Loop i < Integer ArrayList size.
 * Substring(start, indexOf(first smiley)), insert first smiley.
 * Substring (indexOf(first smiley)+1, indexOf(second smiley)), insert second smiley.
 * So on.
*/

/***************************************************************************/

class ClientThread extends Thread {
	BufferedReader in;
	JTextPane tp;
	
	public ClientThread(BufferedReader br, JTextPane jtp) {
		in = br;
		tp = jtp;
	}
	
	public void run() {
		String s;
		StyledDocument doc = tp.getStyledDocument();
		
		//get Smiley
		ImageIcon drool = new ImageIcon("images/drool.gif", "drool");
		ImageIcon bomb = new ImageIcon("images/bomb.gif", "bomb");
		ImageIcon cat = new ImageIcon("images/cat.gif", "cat");
		ImageIcon mewl = new ImageIcon("images/mewl.gif", "cry");
		ImageIcon sob = new ImageIcon("images/sob.gif", "cry1");
		ImageIcon cry = new ImageIcon("images/cry.gif", "cry2");
		ImageIcon egg = new ImageIcon("images/egg.gif", "egg");
		ImageIcon good = new ImageIcon("images/good.gif", "good");
		ImageIcon hot = new ImageIcon("images/hot.gif", "hot");
		ImageIcon knife = new ImageIcon("images/knife.gif", "knife");
		ImageIcon love = new ImageIcon("images/love.gif", "love");
		ImageIcon rice = new ImageIcon("images/rice.gif", "rice");
		ImageIcon salute = new ImageIcon("images/salute.gif", "salute");
		ImageIcon what = new ImageIcon("images/what.gif", "what");
		ImageIcon yeah = new ImageIcon("images/yeah.gif", "yeah");
		ImageIcon smile = new ImageIcon("images/smile.gif", "smile");
		ImageIcon shock = new ImageIcon("images/shock.gif", "shock");
		ImageIcon dream = new ImageIcon("images/dream.gif", "dream");
		ImageIcon shy = new ImageIcon("images/shy.gif", "shy");
		ImageIcon die = new ImageIcon("images/die.gif", "die");
		
		//scrollpanel doesn't work very well
		try {
			while ((s=in.readLine()) != null) { //System.out.println("Client " + s);
				
				if (s.indexOf(":bomb:")>=0) {
					String name = s.substring(0, s.indexOf(":bomb:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(bomb);
					int pos = s.indexOf(":bomb:") + ":bomb:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":cat:")>=0) {
					String name = s.substring(0, s.indexOf(":cat:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(cat);
					int pos = s.indexOf(":cat:") + ":cat:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":cry:")>=0) {
					String name = s.substring(0, s.indexOf(":cry:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(cry);
					int pos = s.indexOf(":cry:") + ":cry:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":die:")>=0) {
					String name = s.substring(0, s.indexOf(":die:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(die);
					int pos = s.indexOf(":die:") + ":die:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":dream:")>=0) {
					String name = s.substring(0, s.indexOf(":dream:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(dream);
					int pos = s.indexOf(":dream:") + ":dream:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":drool:")>=0) {
					String name = s.substring(0, s.indexOf(":drool:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(drool);
					int pos = s.indexOf(":drool:") + ":drool:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":egg:")>=0) {
					String name = s.substring(0, s.indexOf(":egg:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(egg);
					int pos = s.indexOf(":egg:") + ":egg:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":good:")>=0) {
					String name = s.substring(0, s.indexOf(":good:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(good);
					int pos = s.indexOf(":good:") + ":good:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":hot:")>=0) {
					String name = s.substring(0, s.indexOf(":hot:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(hot);
					int pos = s.indexOf(":hot:") + ":hot:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":knife:")>=0) {
					String name = s.substring(0, s.indexOf(":knife:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(knife);
					int pos = s.indexOf(":knife:") + ":knife:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":love:")>=0) {
					String name = s.substring(0, s.indexOf(":love:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(love);
					int pos = s.indexOf(":love:") + ":love:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":mewl:")>=0) {
					String name = s.substring(0, s.indexOf(":mewl:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(mewl);
					int pos = s.indexOf(":mewl:") + ":mewl:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":rice:")>=0) {
					String name = s.substring(0, s.indexOf(":rice:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(rice);
					int pos = s.indexOf(":rice:") + ":rice:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":salute:")>=0) {
					String name = s.substring(0, s.indexOf(":salute:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(salute);
					int pos = s.indexOf(":salute:") + ":salute:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":shock:")>=0) {
					String name = s.substring(0, s.indexOf(":shock:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(shock);
					int pos = s.indexOf(":shock:") + ":shock:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":shy:")>=0) {
					String name = s.substring(0, s.indexOf(":shy:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(shy);
					int pos = s.indexOf(":shy:") + ":shy:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":smile:")>=0) {
					String name = s.substring(0, s.indexOf(":smile:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(smile);
					int pos = s.indexOf(":smile:") + ":smile:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":sob:")>=0) {
					String name = s.substring(0, s.indexOf(":sob:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(sob);
					int pos = s.indexOf(":sob:") + ":sob:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":what:")>=0) {
					String name = s.substring(0, s.indexOf(":what:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(what);
					int pos = s.indexOf(":what:") + ":what:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else if (s.indexOf(":yeah:")>=0) {
					String name = s.substring(0, s.indexOf(":yeah:"));
					doc.insertString(doc.getLength(), name, null);
					tp.setCaretPosition(doc.getLength());
					tp.insertIcon(yeah);
					int pos = s.indexOf(":yeah:") + ":yeah:".length();
					doc.insertString(doc.getLength(), s.substring(pos)+"\n", null);
				}
				else doc.insertString(doc.getLength(), s+"\n", null);
			}
		} catch (IOException io) {
			//System.out.println("Error reading from socket");
		} catch (BadLocationException e) {
			//System.out.println("Bad Location Exception");
		}
	}
}
