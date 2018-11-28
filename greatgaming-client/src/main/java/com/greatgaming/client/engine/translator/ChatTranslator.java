package com.greatgaming.client.engine.translator;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.ChangeSource;
import com.greatgaming.client.engine.state.ChatState;
import com.greatgaming.client.engine.state.GameState;
import com.greatgaming.comms.messages.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatTranslator implements Translator {
    @Override
    public List<Object> toMessages(GameState gameState) {
        List<Object> result = new ArrayList<>();
        for (String message : ((ChatState) gameState).getPendingChatLogChanges(ChangeSource.CLIENT)) {
            Chat chat = new Chat();
            chat.message = message;
            result.add(chat);
        }
        return result;
    }

    @Override
    public void mergeMessage(AggregateGameState aggregateGameState, Object message) {
        ChatState chatState = aggregateGameState.getState(ChatState.class);
        chatState.addToChatLog(((Chat)message).message, ChangeSource.SERVER);
    }
}
