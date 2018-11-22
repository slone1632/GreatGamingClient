package com.greatgaming.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.greatgaming.comms.messages.Chat;
import com.greatgaming.comms.messages.DisconnectRequest;
import com.greatgaming.comms.messages.LoginRequest;
import com.greatgaming.comms.messages.LoginResponse;
import com.greatgaming.comms.serialization.Serializer;

public class ClientMain {
	private static Integer WELCOME_PORT = 6789;
	
	private static Integer getPort(Serializer serializer, String username, String serverAddress) throws Exception {
		Socket clientSocket = new Socket(serverAddress, WELCOME_PORT);

		LoginRequest request = new LoginRequest();
		request.username = username;
		String loginPayload = serializer.serialize(LoginRequest.class, request);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		outToServer.writeBytes(loginPayload + System.lineSeparator());
		String persistentPort = inFromServer.readLine();
		LoginResponse response = (LoginResponse)serializer.deserialize(persistentPort);
		clientSocket.close();
		return response.gamePort;
	}
	
	public static void main(String argv[]) throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Hi, who are you?: ");
		String username = scanner.next();
		System.out.print("What's the IP address of the server?: ");
		String serverAddress = scanner.next();
		System.out.println(serverAddress);
		if (serverAddress.equals("")) {
			serverAddress = "localhost";
		}

		Serializer serializer = new Serializer();

		int port = getPort(serializer, username, serverAddress);

		DataHandler consoleWriter = new DataHandler();
		Syncer syncer = new Syncer(port, consoleWriter, serializer, serverAddress);

		Thread syncherThread = new Thread(syncer);
		syncherThread.start();
		Thread consoleThread = new Thread(consoleWriter);
		consoleThread.start();

		while(true) {
			String input = System.console().readLine();
			if (input.equals("exit")) {
				syncer.sendMessage(DisconnectRequest.class, new DisconnectRequest());
				consoleWriter.stop();
				return;
			} else {
				Chat chat = new Chat();
				chat.message = input;
				syncer.sendMessage(Chat.class, chat);
			}
		}
	}
}