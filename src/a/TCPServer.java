package a;

import java.io.File;	// Effective class for storing file names and paths.
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;	// Represents IP addresses and has methods to retrieve hostnames.
import java.net.ServerSocket;	// This class implements server sockets. A server socket waits for requests to come in over the network. It performs some operation based on that request, and then possibly returns a result to the requester. 
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TCPServer {
	Set<InetAddress> whitelist;
	
	public TCPServer(int port, PrintStream log, File file) throws Exception {
		
		ServerSocket server = new ServerSocket(port); // A server socket waits for a request over the network, does some action on that request and returns to the requester.
		System.out.println("Listening on: " + server.getLocalPort());
		
		whitelist = new HashSet<InetAddress>(); // Automatically hashes for you (?)
		whitelist.add(server.getInetAddress()); // I think we just added our own servers IP to our own white list.
		
		while (file.exists()) {
			Socket client = server.accept(); // A socket is an end-point for communication between two machines.
			// Check the client's IP and filter it (?) - Roumani
			log.println((new Date()).toString() + "| Connection |" + client.getInetAddress());
			
			Worker worker = (new Worker(client)); // An object that does some work in on eor more background threads.
			worker.handle();
			log.println((new Date()).toString()+ "| Disconnected |" + client.getInetAddress());
		}
		
		server.close();
		
	}
	
	public static void main(String[] args) throws Exception {
		File f = new File("/eecs/home/ragheban/eclipse-workspace/EECS4413/src/a/running.txt");
		PrintStream log = new PrintStream(f);
		int port = 1024;
		String hostname = "localhost";
		TCPServer s = new TCPServer(port, log, f); // I don't know what this is.
		
	}
}