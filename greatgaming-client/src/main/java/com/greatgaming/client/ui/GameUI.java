package com.greatgaming.client.ui;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.ChatState;
import com.greatgaming.client.engine.state.GameState;
import com.greatgaming.client.engine.state.RunState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameUI implements Runnable{
    private final Queue<GameState> incomingGameStateChanges;
    private final Queue<GameState> outgoingGameStateChanges;
    private final Queue<String> consoleQueue = new LinkedList<>();
    private final AggregateGameState gameState;
    private boolean keepAilve = true;

    public GameUI(){
        this.gameState = new AggregateGameState();
        incomingGameStateChanges = new LinkedList<>();
        outgoingGameStateChanges = new LinkedList<>();
    }

    public synchronized void addGameStateChange(GameState gameState) {
        synchronized (this.incomingGameStateChanges) {
            this.incomingGameStateChanges.add(gameState);
        }
    }

    public synchronized List<GameState> getGameStateChanges() {
        List<GameState> result = new ArrayList<>();
        synchronized (this.outgoingGameStateChanges) {
            while (outgoingGameStateChanges.peek() != null) {
                result.add(outgoingGameStateChanges.poll());
            }
            return result;
        }
    }

    public boolean isAlive() {
        return keepAilve
                || this.outgoingGameStateChanges.peek() != null
                || this.incomingGameStateChanges.peek() != null;
    }

    private void render() {
        ChatState chatState = this.gameState.getState(ChatState.class);;
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
                keepAilve = false;
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
