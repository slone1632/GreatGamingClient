package com.greatgaming.client.ui;

import com.greatgaming.client.engine.GameBridge;
import com.greatgaming.client.engine.state.*;

import java.util.LinkedList;
import java.util.Queue;

public class ConsoleUI extends GameUI {
    private final Queue<String> consoleQueue = new LinkedList<>();
    private Boolean keepAlive = true;

    public ConsoleUI(AggregateGameState gameState, GameBridge bridge){
        super(gameState, bridge);
    }

    private void render() {
        ChatState chatState = this.aggregateGameState.getState(ChatState.class);
        for (String message : chatState.getPendingChatLogChanges(ChangeSource.SERVER)) {
            System.out.println(message);
        }
    }

    @Override
    public void run() {
        startConsoleReader();
        while (keepAlive || !aggregateGameState.isInSync()) {
            render();
            sendChangesFromClient();
            syncWithServer();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Java is drunk");
            }
        }
    }

    private void startConsoleReader() {
        Thread thread = new Thread(new ConsoleReader(consoleQueue));
        thread.start();
    }

    private void sendChangesFromClient() {
        while(consoleQueue.peek() != null) {
            String input = consoleQueue.poll();
            if (input.equals("exit")) {
                RunState runState = this.aggregateGameState.getState(RunState.class);
                runState.shutDownGame();
                keepAlive = false;
                System.out.println("Console UI requested shutdown");
            } else {
                ChatState chatState = this.aggregateGameState.getState(ChatState.class);
                chatState.addToChatLog(input, ChangeSource.CLIENT);
            }
        }
    }
}
