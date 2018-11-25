package com.greatgaming.client.ui;

import com.greatgaming.client.UIApplication;
import com.greatgaming.client.engine.GameBridge;
import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.ChangeSource;
import com.greatgaming.client.engine.state.GameState;

public class JavaFXUI extends GameUI {
    private final UIApplication application;

    public JavaFXUI(AggregateGameState aggregateGameState, GameBridge gameBridge, UIApplication application) {
        super(aggregateGameState, gameBridge);
        this.application = application;
    }

    public void stop(){
        keepAlive = false;
    }

    @Override
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
}
