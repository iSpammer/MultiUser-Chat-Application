package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
	private int serverPort;
	private ArrayList<ClientHandler> threadList = new ArrayList<ClientHandler>();
	Socket clientSocket;
	String sentence;
	String serverSentence;
	Server server2;

	public Server(int serverPort, Server server) {
		this.serverPort = serverPort;
		this.server2 = server;
	}

	public void setServer(Server server) {
		this.server2 = server;
	}

	public List<ClientHandler> getThreadList() {
		return threadList;
	}

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(serverPort);
			while (true) {
				System.out.println("Waiting clients");
				Socket clientSocket = serverSocket.accept();
				ClientHandler thread = new ClientHandler(this, server2, clientSocket);
				threadList.add(thread);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeThread(ClientHandler serverThread) {
		threadList.remove(serverThread);
	}
}
