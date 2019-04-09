package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Client {
	Socket clientSocket;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	BufferedReader inFromUser;

	public Client(String ip) {
		Gui g = new Gui();
		g.frame.setVisible(true);
		g.frame.setTitle("Enter server port");
		int port = 0;
		while (true) {
			if (g.textField != null && g.textField.getText() != null && g.isTextNotEmpty()) {
				port = Integer.parseInt(g.textField.getText());
				System.out.println(port);
				System.out.println(g.textField.getText());

				g.setTextNotEmpty(false);
				String[] info = g.textField.getText().split(" ");
				if (info.length > 0) {
						g.frame.setTitle("login please!");
						g.textField.setText("");
						break;
				}
			}
		}
		System.out.println(port);
		String sentence;
		String serverSentence;
		try {
			clientSocket = new Socket(ip, port);
			g.textArea.append("The Client is connected to port num"+port+"\n");
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			inFromUser = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				while (true) {
					if (g.textField != null && g.textField.getText() != null && g.isTextNotEmpty()) {
						sentence = g.textField.getText();
						g.setTextNotEmpty(false);
						String[] info = g.textField.getText().split(" ");
//						System.out.println(Arrays.toString(info));
						if (info.length > 0 && info[0].equalsIgnoreCase("login")) {
							if (info.length > 1) {
								g.frame.setTitle(info[1]+"is connected to "+port);
							}
						}
						g.textField.setText("");
						break;
					}

				}
				outToServer.writeBytes(sentence + '\n');
				if (sentence.equalsIgnoreCase("quit")) {
					clientSocket.close();
					g.textArea.append("connection closed");
					break;
				}
				new Thread() {
					public void run() {
						try {
							while (true) {
								String rcvd = inFromServer.readLine();
								System.out.println(rcvd);
								g.textArea.append(rcvd+ "\n");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		
		new Client("127.0.0.1");
	}
}
