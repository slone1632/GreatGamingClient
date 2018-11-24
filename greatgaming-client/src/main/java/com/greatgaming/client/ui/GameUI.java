package com.greatgaming.client.ui;

import com.greatgaming.client.engine.GameBridge;
import com.greatgaming.client.engine.state.AggregateGameState;

public abstract class GameUI implements Runnable {
    protected boolean keepAlive = true;
    protected AggregateGameState aggregateGameState;
    private GameBridge gameBridge;

    public GameUI(AggregateGameState aggregateGameState, GameBridge gameBridge) {
        this.aggregateGameState = aggregateGameState;
        this.gameBridge = gameBridge;
    }
    public abstract void run();

    protected void syncWithServer() {
        this.gameBridge.syncToServer(this.aggregateGameState);
    }

}
