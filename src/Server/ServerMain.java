package Server;

public class ServerMain {
	static Server server = null;

	public static void main(String[] args) {
		int port = 1234;

		server = new Server(port, null);
		server.start();
	}
}
