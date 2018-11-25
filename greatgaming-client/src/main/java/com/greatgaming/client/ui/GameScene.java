package com.greatgaming.client.ui;

import com.greatgaming.client.engine.state.AggregateGameState;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GameScene implements UIComponent {
    private final AggregateGameState aggregateGameState;
    private final ChatComponent chatComponent;

    public GameScene(AggregateGameState gameState) {
        this.aggregateGameState = gameState;
        this.chatComponent = new ChatComponent(gameState);
    }

    public void render() {
        this.chatComponent.render();
    }

    public Scene getScene() {
        Pane chatPane = this.chatComponent.getComponent();
        return new Scene(chatPane, 300, 250);
    }
}
