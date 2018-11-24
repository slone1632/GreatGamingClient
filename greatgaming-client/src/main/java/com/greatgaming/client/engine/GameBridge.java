package com.greatgaming.client.engine;

import com.greatgaming.client.engine.state.ChatState;
import com.greatgaming.client.engine.state.GameState;
import com.greatgaming.client.engine.state.RunState;
import com.greatgaming.client.networking.Syncer;
import com.greatgaming.client.ui.ConsoleUI;
import com.greatgaming.client.ui.GameUI;
import com.greatgaming.comms.messages.Chat;
import com.greatgaming.comms.messages.DisconnectRequest;

import java.util.List;

public class GameBridge implements Runnable {
    private Syncer networkSyncer;
    private GameUI ui;

    public GameBridge(
            Syncer networkSyncer,
            GameUI ui) {
        this.ui = ui;
        this.networkSyncer = networkSyncer;
    }
    @Override
    public void run() {
        while (this.ui.isAlive()) {
            sendServerChangesToUI();
            sendUIChangesToServer();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Java is drunk");
            }
        }
        this.networkSyncer.stop();
    }

    private void sendServerChangesToUI() {
        List<Object> messages = this.networkSyncer.getIncomingMessages();

        for (Object message : messages) {
            if (message instanceof Chat) {
                ChatState chatState = new ChatState();
                chatState.addToChatLog(((Chat)message).message );
                this.ui.addGameStateChange(chatState);
            }
        }
    }

    private void sendUIChangesToServer() {
        List<GameState> changes = this.ui.getGameStateChanges();

        for (GameState change : changes) {
            if (change instanceof ChatState) {
                for (String message : ((ChatState) change).getPendingChatLogChanges()) {
                    Chat chat = new Chat();
                    chat.message = message;
                    networkSyncer.sendMessage(Chat.class, chat);
                }
            } else if (change instanceof RunState) {
                networkSyncer.sendMessage(DisconnectRequest.class, new DisconnectRequest());
            }
        }
    }
}
