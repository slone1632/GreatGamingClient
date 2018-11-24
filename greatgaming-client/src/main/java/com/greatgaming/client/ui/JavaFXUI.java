package com.greatgaming.client.ui;

import com.greatgaming.client.engine.GameBridge;
import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.ui.scene.FXApplication;
import com.greatgaming.client.ui.scene.GameScene;

public class JavaFXUI extends GameUI {
    public JavaFXUI(AggregateGameState aggregateGameState, GameBridge gameBridge) {
        super(aggregateGameState, gameBridge);
    }

    @Override
    public void run() {
        GameScene gameScene = new GameScene(aggregateGameState);
        FXApplication application = new FXApplication(gameScene.getScene(), aggregateGameState);
        application.launchApp();

        while (keepAlive || !aggregateGameState.isInSync()) {
            syncWithServer();
            gameScene.render();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
