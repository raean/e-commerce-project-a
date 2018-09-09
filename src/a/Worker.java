package a;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class Worker {
	
	Socket client;
	
	public Worker(Socket client) {
		this.client = client;
	}

	/*
	 * Handle simply takes in the text that the client wants to send.
	 */
	public void handle() throws Exception {
		Scanner input = new Scanner(System.in);
		System.out.println("Start writing: \n");System.out.println("Okay?");
		String in = input.nextLine();
		while (in.equals("Bye")) {
			in = input.nextLine();
			System.out.println(in);
		}
		this.Bye();
	}
	
	/*
	 * M1
	 */
	public void Bye() throws Exception {
		
		client.close();
	}

}
