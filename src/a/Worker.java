package a;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Worker {
	
	private Socket client;
	private TCPServer server;
	private OutputStream os;
	private OutputStreamWriter osw;
	private BufferedWriter bw;
	
	public Worker(Socket client, TCPServer server) {
		this.client = client;
		this.server = server;
	}

	/*
	 * Handle simply takes in the text that the client wants to send.
	 */
	public void handle() throws Exception {
		InputStream is = client.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		
		os = client.getOutputStream();
		osw = new OutputStreamWriter(os);
		bw = new BufferedWriter(osw);
		
		String message = "";
		while (!message.equals("Bye")) {
			message = br.readLine();
			System.out.println("Processing the request: " + message + "\n");
			String response;
			
			if (message.equals("getTime")) {
				response = this.getTime();
				bw.write(response + "\n");
				bw.flush();
			} else if (message.equals("Bye")) {
				response = "Goodbye!\n";
				bw.write(response);
				bw.flush();
				this.Bye();
			} else if (message.matches("Punch\\s([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])")) {
				response = "Adding IP: " + message.substring(6) + " to the whitelist.\n";
				bw.write(response);
				bw.flush();
				this.Punch(message.substring(6));
			} else if (message.matches("Plug\\s([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])")) {
				response = "Removing IP: " + message.substring(5) + " to the whitelist.\n";
				bw.write(response);
				bw.flush();
				this.Plug(message.substring(5));
			} else {
				response = "Please enter a valid request!\n";
				bw.write(response);
				bw.flush();
			}
		}
		this.Bye();
	}
	
	/*
	 * M1
	 */
	private String getTime() {
		Date d = new Date();
		return d.toString();
	}

	/*
	 * M2
	 */
	private void Bye() throws Exception {
		client.close();
	}
	
	/*
	 * M3
	 */
	private void Punch(String IP) throws Exception {
		String patternString = "([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(IP);
        if (matcher.matches()) {
        	String response = "Done!\n";
        	this.server.addToWhitelist(InetAddress.getByName(IP));
        	bw.write(response);
			bw.flush();
        } else {
        	String response = "Invalid IP! Not added.\n";
        	bw.write(response);
			bw.flush();
        }
		
	}
	
	/*
	 * M4
	 */
	private void Plug(String IP) throws Exception {
		String patternString = "([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(IP);
        if (matcher.matches()) {
        	String response = "Done!\n";
        	this.server.removeFromWhitelist(InetAddress.getByName(IP));
        	bw.write(response);
			bw.flush();
        } else {
        	String response = "Invalid IP! Not removed.\n";
        	bw.write(response);
			bw.flush();
        }
	}


}
