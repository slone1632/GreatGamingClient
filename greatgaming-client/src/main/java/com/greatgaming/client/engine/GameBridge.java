package com.greatgaming.client.engine;

import com.greatgaming.client.engine.state.*;
import com.greatgaming.client.networking.Syncer;
import com.greatgaming.comms.messages.Chat;
import com.greatgaming.comms.messages.DisconnectRequest;
import com.greatgaming.comms.messages.DisconnectResponse;

import java.util.List;

public class GameBridge {
    private Syncer networkSyncer;

    public GameBridge(Syncer networkSyncer) {
        this.networkSyncer = networkSyncer;
    }

    public void syncToServer(AggregateGameState gameState) {
        acceptChangesFromServer(gameState);
        sendChangesToServer(gameState);
    }

    private void acceptChangesFromServer(AggregateGameState gameState) {
        List<Object> messages = this.networkSyncer.getIncomingMessages();

        for (Object message : messages) {
            if (message instanceof Chat) {
                ChatState chatState = gameState.getState(ChatState.class);
                chatState.addToChatLog(((Chat)message).message, ChangeSource.SERVER);
            } else if (message instanceof DisconnectResponse) {
                RunState runState = gameState.getState(RunState.class);
                runState.serverHasBeenNotified();
            }
        }
    }

    private void sendChangesToServer(AggregateGameState gameState) {
        for (GameState change : gameState.getStatesChangedBy(ChangeSource.CLIENT)) {
            if (change instanceof ChatState) {
                for (String message : ((ChatState) change).getPendingChatLogChanges(ChangeSource.CLIENT)) {
                    Chat chat = new Chat();
                    chat.message = message;
                    networkSyncer.sendMessage(Chat.class, chat);
                }
            } else if (change instanceof RunState) {
                RunState runState = (RunState)change;
                networkSyncer.sendMessage(DisconnectRequest.class, new DisconnectRequest());
                System.out.println("Sending disconnect request");
            }
        }
    }
}
