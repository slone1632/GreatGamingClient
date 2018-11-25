package com.greatgaming.client.ui.scene;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.ChatState;
import com.greatgaming.client.engine.state.GameState;
import com.greatgaming.client.engine.state.ChangeSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public class GameScene {
    private final AggregateGameState aggregateGameState;
    private ObservableList<String> chat;
    private ListView chatListView;

    public GameScene(AggregateGameState gameState) {
        this.aggregateGameState = gameState;
    }

    public void render() {

        for (GameState changed : this.aggregateGameState.getStatesChangedBy(ChangeSource.SERVER)) {
            if (chat != null) {
                if (changed instanceof ChatState) {
                    chat.addAll(((ChatState) changed).getPendingChatLogChanges(ChangeSource.SERVER));
                    chatListView.scrollTo(chat.size() - 1);
                }
            }
        }
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();

        TextField messageText = new TextField();

        chatListView = new ListView<>();
        chat = FXCollections.observableArrayList ();
        chatListView.setItems(chat);
        chatListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    {
                        prefWidthProperty().bind(chatListView.widthProperty().subtract(20)); // 1
                        setMaxWidth(Control.USE_PREF_SIZE); //2
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        if (item != null && !empty) {
                            this.setWrapText(true); // 3
                            setText(item);
                        } else {
                            setText(null);
                        }
                    }

                };
            }
        });

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
        pane.setCenter(chatListView);

        return new Scene(pane, 300, 250);
    }
}
