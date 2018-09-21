package a;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	
	Socket clientSocket;
	
	public TCPClient(String hostname, int port) throws Exception, Exception {
		this.clientSocket = new Socket(hostname, port);
	}
	
}
