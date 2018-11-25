package com.greatgaming.client.ui.scene;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.ChatState;
import com.greatgaming.client.engine.state.GameState;
import com.greatgaming.client.engine.state.ChangeSource;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GameScene {
    private final AggregateGameState aggregateGameState;
    private VBox chat;

    public GameScene(AggregateGameState gameState) {

        this.aggregateGameState = gameState;
    }

    public void render() {

        for (GameState changed : this.aggregateGameState.getStatesChangedBy(ChangeSource.SERVER)) {
            if (chat != null) {
                if (changed instanceof ChatState) {
                    ChatState chatState = (ChatState)changed;
                    System.out.println("Render called");
                    for (String message : chatState.getPendingChatLogChanges(ChangeSource.SERVER)){
                        Label nextLabel = new Label();
                        nextLabel.setText(message);
                        nextLabel.setWrapText(true);
                        this.chat.getChildren().add(nextLabel);
                    }
                }
            }
        }
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();

        TextField messageText = new TextField();

        ScrollPane scrollPane = new ScrollPane();
        chat = new VBox();
        chat.heightProperty().addListener(observable -> scrollPane.setVvalue(1D));
        scrollPane.setContent(chat);
        scrollPane.setFitToWidth(true);

        messageText.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    ChatState chatState = aggregateGameState.getState(ChatState.class);
                    chatState.addToChatLog(messageText.getText(), ChangeSource.CLIENT);
                    messageText.setText("");
                }
            }
        });

        pane.setBottom(messageText);
        pane.setCenter(scrollPane);

        return new Scene(pane, 300, 250);
    }
}
