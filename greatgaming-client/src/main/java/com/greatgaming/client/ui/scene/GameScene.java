package com.greatgaming.client.ui.scene;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.ChatState;
import com.greatgaming.client.engine.state.GameState;
import com.greatgaming.client.engine.state.ChangeSource;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class GameScene {
    private final AggregateGameState aggregateGameState;

    public GameScene(AggregateGameState gameState) {
        this.aggregateGameState = gameState;
    }

    public void render() {
        for (GameState changed : this.aggregateGameState.getStatesChangedBy(ChangeSource.SERVER)) {
            if (changed instanceof ChatState) {

            }
        }
    }

    public Scene getScene() {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        return new Scene(root, 300, 250);
    }
}
