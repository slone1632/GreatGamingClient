package com.greatgaming.client;

import java.util.Scanner;

import com.greatgaming.client.engine.GameBridge;
import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.networking.*;
import com.greatgaming.client.ui.ConsoleUI;
import com.greatgaming.client.ui.GameUI;
import com.greatgaming.comms.serialization.Serializer;

public class ConsoleApp {

	
	public static void main(String argv[]) throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Hi, who are you?: ");
		String username = scanner.next();
		System.out.print("What's the IP address of the server?: ");
		String serverAddress = scanner.next();

		Serializer serializer = new Serializer();

		LoginHelper helper = new LoginHelper();
		int port = helper.getGamePort(serializer, username, serverAddress);

		StreamFactory streamFactory = new StreamFactory(serverAddress, port, new SocketFactory());
		MessageReceiver receiver = new MessageReceiver(streamFactory, serializer);
		MessageSender sender = new MessageSender(streamFactory, serializer);
		Syncer syncer = new Syncer(sender, receiver);


		GameBridge bridge = new GameBridge(syncer);
		Thread syncherThread = new Thread(syncer);
		syncherThread.start();

		GameUI ui = new ConsoleUI(new AggregateGameState(), bridge);
		ui.run();
	}
}