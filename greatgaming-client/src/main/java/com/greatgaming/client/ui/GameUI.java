package com.greatgaming.client.ui;

import com.greatgaming.client.engine.state.GameState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class GameUI implements Runnable {
    protected final Queue<GameState> incomingGameStateChanges;
    protected final Queue<GameState> outgoingGameStateChanges;
    protected boolean keepAlive = true;

    public GameUI() {
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
        return keepAlive
                || this.outgoingGameStateChanges.peek() != null
                || this.incomingGameStateChanges.peek() != null;
    }
    public abstract void run();
}
