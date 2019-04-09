package Server;

import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.List;

public class ClientHandler extends Thread {

	private final Socket clientSocket;
	private final Server server;
	private final Server otherServer;
	private String login = null;
	private int ttl;
	private OutputStream outputStream;
	private HashSet<String> groupSet = new HashSet<>();

	public ClientHandler(Server server, Server otherServer, Socket clientSocket) {
		this.server = server;
		this.otherServer = otherServer;
		this.clientSocket = clientSocket;
		this.groupSet.add("#all");
	}

	@Override
	public void run() {
		try {
			handleClientSocket();
		} catch (SocketException e) {
			System.out.println(login + " has logged off");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void handleClientSocket() throws IOException, InterruptedException {
		InputStream inputStream = clientSocket.getInputStream();
		this.outputStream = clientSocket.getOutputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] tokens = StringUtils.split(line);
			if (tokens != null && tokens.length > 0) {
				String cmd = tokens[0];
				if ("quit".equalsIgnoreCase(cmd)) {
					handleLogoff();
					break;
				} else if ("login".equalsIgnoreCase(cmd)) {
					handleLogin(outputStream, tokens);
				} else if ("msg".equalsIgnoreCase(cmd)) {
					String[] tokensMsg = StringUtils.split(line, null, 3);
					handleMsg(tokensMsg);
				} else if ("join".equalsIgnoreCase(cmd)) {
					handleJoin(tokens);
				} else if ("leave".equalsIgnoreCase(cmd)) {
					handleLeave(tokens);
				} else if ("showOnline".equalsIgnoreCase(cmd)) {
					handleOnline();
				} else {
					String msg = "unknown " + cmd + "\n";
					outputStream.write(msg.getBytes());
				}
			}
		}
		clientSocket.close();
	}

	private void handleOnline() throws IOException {
		List<ClientHandler> threadList = server.getThreadList();

		for (ClientHandler worker : threadList) {
			if (worker.getLogin() != null) {
				String msg2 = "online " + worker.getLogin() + "\n";
				this.send(msg2);

			}
		}
		List<ClientHandler> threadList2 = otherServer.getThreadList();

		for (ClientHandler worker2 : threadList2) {
			if (worker2.getLogin() != null) {
				String msg2 = "online " + worker2.getLogin() + "\n";
				this.send(msg2);

			}
		}
	}

	private void handleLeave(String[] tokens) {
		if (tokens.length > 1) {
			String group = tokens[1];
			groupSet.remove(group);
		}
	}

	public boolean isMemberOfGroup(String topic) {
		return groupSet.contains(topic);
	}

	private void handleJoin(String[] tokens) {
		if (tokens.length > 1) {
			String group = tokens[1];
			groupSet.add(group);
		}
	}

	private void handleMsg(String[] tokens) throws IOException {
		String to = tokens[1];
		String mesg = tokens[2];
		ttl = 2;
		System.out.println("ttl "+ttl);

		boolean isFound = false;
		boolean isGroup = to.charAt(0) == '#';
		List<ClientHandler> threadList = server.getThreadList();
		ttl--;
		for (int i = 0; i < threadList.size(); i++) {
			if (ttl < 1) {
				this.send("ERROR\n");
				ttl++;
				System.out.println("ttl "+ttl);
				return;
			}
			if (isGroup) {
				if (threadList.get(i).isMemberOfGroup(to)) {
					String outMsg = "msg from " + to + " by " + login + ": " + mesg + "\n";
					threadList.get(i).send(outMsg);
					isFound = true;
				}
			} else {
				if (to.equalsIgnoreCase(threadList.get(i).getLogin())) {
					String outMsg = "msg from " + login + " " + mesg + "\n";
					threadList.get(i).send(outMsg);
					isFound = true;
				}
			}
		}
		if (isFound) {
			ttl--;
			System.out.println("ttl "+ttl);
			return;
		}
		if (!isFound) {
			System.out.println(otherServer);
			List<ClientHandler> threadList2 = otherServer.getThreadList();
			for (int i = 0; i < threadList.size(); i++) {
				if (isGroup) {
					if (threadList2.get(i).isMemberOfGroup(to)) {
						String outMsg = "msg from " + to + " by " + login + ": " + mesg + "\n";
						threadList2.get(i).send(outMsg);
						isFound = true;
					}
				}

				else {
					try {
						if (threadList.size() != 0) {
							if (to.equalsIgnoreCase(threadList2.get(i).getLogin())) {
								String outMsg = "msg from " + login + " " + mesg + "\n";
								threadList2.get(i).send(outMsg);
								isFound = true;
							}
						}
					} catch (IndexOutOfBoundsException e) {
						this.send("Error sending the message\n");
						return;
					}
				}
			}
		}
		System.out.println(ttl);
		if (isFound) {
			ttl--;
			System.out.println("ttl "+ttl);
			return;
		}
		if (ttl == 0 || !isFound) {
			this.send("Error sending the message\n");
			return;
		}
	}

	private void handleLogoff() throws IOException {
		server.removeThread(this);
		List<ClientHandler> workerList = server.getThreadList();

		String onlineMsg = "offline " + login + "\n";
		for (ClientHandler worker : workerList) {
			if (!login.equals(worker.getLogin())) {
				worker.send(onlineMsg);
			}
		}
		clientSocket.close();
	}

	public String getLogin() {
		return login;
	}

	private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
		if (tokens.length == 2) {
			String enteredName = tokens[1];
			String msg = "Logged in\n";
			outputStream.write(msg.getBytes());
			this.login = enteredName;
			System.out.println("User logged in succesfully: " + enteredName);

			List<ClientHandler> threadList = server.getThreadList();

			// To send all the logged in user who has just logged in now
			for (ClientHandler worker : threadList) {
				if (worker.getLogin() != null) {
					if (!enteredName.equals(worker.getLogin())) {
						String msg2 = "online " + worker.getLogin() + "\n";
						send(msg2);
					}
				}
			}
			// lsa d5l gdyyd
			String onlineMsg = "online " + enteredName + "\n";
			for (ClientHandler worker : threadList) {
				if (!enteredName.equals(worker.getLogin())) {
					worker.send(onlineMsg);
				}
			}
		}
	}

	private void send(String msg) throws IOException {
		if (login != null) {
			try {
				outputStream.write(msg.getBytes());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
