package com.greatgaming;

import java.io.*;
import java.net.*;

public class ClientMain {
	private static Integer WELCOME_PORT = 6789;
	
	private static Integer getPort() throws Exception {
		String persistentPort;
		Socket clientSocket = new Socket("localhost", WELCOME_PORT);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		outToServer.writeBytes("RequestingNewConnection" + System.lineSeparator());
		persistentPort = inFromServer.readLine();
		System.out.println("Server wants to establish connection on port " + persistentPort);
		clientSocket.close();
		return Integer.valueOf(persistentPort);
	}
	
	public static void main(String argv[]) throws Exception {
		int port = getPort();
		
		String sentence;
		String modifiedSentence;
		
		while (true) {
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			Socket clientSocket = new Socket("localhost", port);
			
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			sentence = inFromUser.readLine();
			
			outToServer.writeBytes(sentence + System.lineSeparator());
			modifiedSentence = inFromServer.readLine();
			System.out.println("FROM SERVER: " + modifiedSentence);
			clientSocket.close();
		}
	}
}