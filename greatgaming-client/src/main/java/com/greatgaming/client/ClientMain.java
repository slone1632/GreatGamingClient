package com.greatgaming.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientMain {
	private static Integer WELCOME_PORT = 6789;
	
	private static Integer getPort() throws Exception {
		String persistentPort;
		Socket clientSocket = new Socket("localhost", WELCOME_PORT);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		outToServer.writeBytes("RequestingNewConnection" + System.lineSeparator());
		persistentPort = inFromServer.readLine();
		clientSocket.close();
		return Integer.valueOf(persistentPort);
	}
	
	public static void main(String argv[]) throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Hi, who are you?: ");
		String username = scanner.next();

		int port = getPort();

		DataHandler consoleWriter = new DataHandler();
		Syncer syncer = new Syncer(port, consoleWriter);

		Thread syncherThread = new Thread(syncer);
		syncherThread.start();
		Thread consoleThread = new Thread(consoleWriter);
		consoleThread.start();

		while(true) {
			String input = System.console().readLine();
			if (input.equals("exit")) {
				syncer.stop();
				consoleWriter.stop();
				return;
			} else {
				syncer.sendMessage(username + ": " + input);
			}
		}
	}
}