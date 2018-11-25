package com.greatgaming.client.engine;

import com.greatgaming.client.UIApplication;
import com.greatgaming.client.engine.state.AggregateGameState;

public class GameBridgeLoop implements Runnable{
    private final UIApplication application;
    private final AggregateGameState aggregateGameState;
    private final GameBridge gameBridge;
    private boolean keepAlive = true;

    public GameBridgeLoop(AggregateGameState aggregateGameState, GameBridge gameBridge, UIApplication application) {
        this.aggregateGameState = aggregateGameState;
        this.gameBridge = gameBridge;
        this.application = application;
    }

    public void stop(){
        keepAlive = false;
    }

    public void run() {
        while (keepAlive || !aggregateGameState.outgoingMessagesAreSynced()) {
            syncWithServer();
            application.render();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void syncWithServer() {
        this.gameBridge.syncToServer(this.aggregateGameState);
    }
}
