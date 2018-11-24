package com.greatgaming.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.greatgaming.client.engine.GameBridge;
import com.greatgaming.client.networking.*;
import com.greatgaming.client.ui.ConsoleUI;
import com.greatgaming.client.ui.GameUI;
import com.greatgaming.client.ui.SwinglUI;
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

		Serializer serializer = new Serializer();

		int port = getPort(serializer, username, serverAddress);

		StreamFactory streamFactory = new StreamFactory(serverAddress, port, new SocketFactory());
		MessageReceiver receiver = new MessageReceiver(streamFactory, serializer);
		MessageSender sender = new MessageSender(streamFactory, serializer);
		Syncer syncer = new Syncer(sender, receiver);

		GameUI ui = new SwinglUI();

		GameBridge bridge = new GameBridge(syncer, ui);
		Thread syncherThread = new Thread(syncer);
		syncherThread.start();

		Thread bridgeThread = new Thread(bridge);
		bridgeThread.start();
		ui.run();
	}
}