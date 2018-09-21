package a;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import projA.Course;
import projA.Student;
import projA.Util;

public class Worker implements Runnable {
	
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
	public void handle() throws Exception  {
		try {
			InputStream is = client.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			os = client.getOutputStream();
			osw = new OutputStreamWriter(os);
			bw = new BufferedWriter(osw);
			
			String message = "";
			while (!message.matches("[bB][yY][eE]")) {
				message = br.readLine();
				System.out.println("Processing the request: " + message);
				String response;
				
				if (message.matches("[gG][eE][tT][tT][iI][mM][eE]")) {
					response = this.getTime();
					bw.write(response + "\n");
					bw.flush();
				} else if (message.matches("[bB][yY][eE]")) {
					response = "Goodbye!\n";
					bw.write(response);
					bw.flush();
					this.Bye();
				} else if (message.matches("[Pp][Rr][Ii][Mm][Ee]\\s[0-9]*[0-9]+")) {
					//bw.write("Finding your prime number..." + "\n");
					BigInteger primeNumber = this.Prime(message.substring(6));
					bw.write(primeNumber + "\n");
					bw.flush();
				} else if (message.matches("[Pp][Uu][Nn][Cc][Hh]\\s([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])")) {
					response = "Adding IP: " + message.substring(6) + " to the whitelist.\n";
					bw.write(response);
					bw.flush();
					this.Punch(message.substring(6));
				} else if (message.matches("[Pp][Ll][Uu][Gg]\\s([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])\\.([0-2]?[0-5]?[0-5])")) {
					response = "Removing IP: " + message.substring(5) + " to the whitelist.\n";
					bw.write(response);
					bw.flush();
					this.Plug(message.substring(5));
				} else if (message.matches("[Aa][Uu][Tt][Hh]\\s([0-9A-Za-z])+\\s([0-9A-Za-z])+")) {
					String[] split = message.substring(5).split(" ");
					response = this.Auth(split[0], split[1]);
					bw.write(response);
					bw.flush();
				} else if (message.matches("[Rr][Oo][Ss][Tt][Ee][Rr]\\s[a-zA-Z]{3,4}(\\s)?[0-9]{4}\\s(([Xx][Mm][Ll])|([Jj][Ss][Oo][Nn]))")) {
					String[] split = message.substring(7).split(" ");
					if (split.length == 2) {
						this.Roster(split[0], split[1]);
					} else {
						String course = split[0] + "" + split[1];
						this.Roster(course, split[2]);
					}
				} else {
					response = "Don't understand \"" + message + "\"\n";
					bw.write(response);
					bw.flush();
				}
			}
			this.Bye();
		} catch (Exception e) {
			this.server.addToLog((new Date()).toString() + "| Worker exception thrown |" + e.getMessage());
		}
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
		try {
			client.close();
			this.server.addToLog((new Date()).toString() + "| Disconnected |" + client.getInetAddress());
		} catch (Exception e){
			this.server.addToLog((new Date()).toString() + "| Bye exception thrown |" + e.getMessage());
		}
	}
	
	/*
	 * M3
	 */
	private void Punch(String IP) throws Exception {
		try {
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
		} catch (Exception e) {
			this.server.addToLog((new Date()).toString() + "| Punch exception thrown |" + e.getMessage());
		}
		
	}
	
	/*
	 * M4
	 */
	private void Plug(String IP) throws Exception {
		try {
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
		} catch (Exception e) {
			this.server.addToLog((new Date()).toString() + "| Plug exception thrown |" + e.getMessage());
		}
	}
	
	/*
	 * M5
	 */
	private BigInteger Prime(String substring) throws Exception {
		try {
			int digits = Integer.parseInt(substring);
			if (digits < 2) {
				bw.write("Please enter a number that is larger than 1!\n");
				bw.flush();
				return BigInteger.ZERO;
			}
			BigInteger primeNumber  = BigInteger.probablePrime((int) Math.floor(3.3*Integer.parseInt(substring)), new Random());
			return primeNumber;
		} catch (Exception e) {
			this.server.addToLog((new Date()).toString() + "| Prime exception thrown |" + e.getMessage());
			return BigInteger.ZERO;
		}
	}
	
	/*
	 * M6
	 */
	private String Auth(String username, String password) {
		if (this.server.validateUser(username, password)) {
			return "You are in!\n";
		} else {
			return "Auth Failure\n";
		}
	}
	
	/*
	 * M7
	 */
	private void Roster(String course, String format) throws JAXBException, IOException {
		try {	
			Course c = Util.getCourse(course);
			
			if (format.matches("[Xx][Mm][Ll]")) {
				JAXBContext context = JAXBContext.newInstance(Course.class);
				Marshaller m = context.createMarshaller();
				JAXBElement<Course> root = new JAXBElement<Course>(new QName("Course"), Course.class, c);
				m.marshal(root, bw);
				bw.write("\n");
				bw.flush();
			} else {
				// Gson gson = new Gson();
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String json = gson.toJson(c);
				bw.write(json + "\n");
				bw.flush();
			}
		} catch (JAXBException e) {
			this.server.addToLog((new Date()).toString() + "| JAXB exception thrown |" + e.getMessage());
		} catch (IOException e) {
			this.server.addToLog((new Date()).toString() + "| IO exception thrown |" + e.getMessage());
		}
		
		
		
	}
	
	@Override
	public void run() {
		try {
			this.handle();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


}
