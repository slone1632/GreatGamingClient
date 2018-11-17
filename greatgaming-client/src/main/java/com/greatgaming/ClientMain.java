package com.greatgaming;

import java.io.*;
import java.net.*;

public class ClientMain {
	public static void main(String argv[]) throws Exception {
		String sentence;
		String modifiedSentence;
		
		while (true) {
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			Socket clientSocket = new Socket("localhost", 6789);
			
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