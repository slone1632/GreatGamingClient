package com.greatgaming.client.ui;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.ChangeSource;
import com.greatgaming.client.engine.state.ChatState;
import com.greatgaming.client.engine.state.GameState;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ChatComponent {
    public static final Integer TOTAL_HEIGHT = 130;
    private final AggregateGameState aggregateGameState;
    private VBox chat;

    public ChatComponent(AggregateGameState gameState) {
        this.aggregateGameState = gameState;
    }

    public void render() {
        for (GameState changed : this.aggregateGameState.getStatesChangedBy(ChangeSource.SERVER)) {
            if (chat != null) {
                if (changed instanceof ChatState) {
                    ChatState chatState = (ChatState)changed;
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

    public Pane getComponent() {
        BorderPane pane = new BorderPane();

        TextField messageText = new TextField();

        ScrollPane scrollPane = new ScrollPane();
        chat = new VBox();
        chat.heightProperty().addListener(observable -> scrollPane.setVvalue(1D));
        scrollPane.setContent(chat);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(100);
        scrollPane.setMinHeight(100);

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

        return pane;
    }
}
