package com.greatgaming.client.ui;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.ChatState;
import com.greatgaming.client.engine.state.GameState;
import com.greatgaming.client.engine.state.RunState;

import java.util.LinkedList;
import java.util.Queue;

public class ConsoleUI extends GameUI {
    private final Queue<String> consoleQueue = new LinkedList<>();
    private final AggregateGameState gameState;

    public ConsoleUI(){
        this.gameState = new AggregateGameState();
    }

    private void render() {
        ChatState chatState = this.gameState.getState(ChatState.class);
        for (String message : chatState.getPendingChatLogChanges()) {
            System.out.println(message);
        }
    }

    @Override
    public void run() {
        startConsoleReader();
        while (isAlive()) {
            mergeChangesFromServer();
            render();
            sendChangesFromClient();
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
                RunState runState = new RunState();
                runState.shutDownGame();
                outgoingGameStateChanges.add(runState);
                keepAlive = false;
            } else {
                ChatState chatState = new ChatState();
                chatState.addToChatLog(input);
                outgoingGameStateChanges.add(chatState);
            }
        }
    }

    private void mergeChangesFromServer() {
        while (this.incomingGameStateChanges.peek() != null) {
            GameState change = this.incomingGameStateChanges.poll();
            GameState match = this.gameState.getState(change.getClass());
            match.merge(change);
        }
    }
}
