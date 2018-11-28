package com.greatgaming.client.engine;

import com.greatgaming.client.engine.state.*;
import com.greatgaming.client.engine.translator.Translator;
import com.greatgaming.client.networking.Syncer;

import java.util.List;
import java.util.Map;

public class GameBridge {
    private final Map<Class, Translator> messageToTranslatorMap;
    private final Map<Class, Translator> stateToTranslatorMap;
    private final Syncer networkSyncer;


    public GameBridge(
            Syncer networkSyncer,
            Map<Class, Translator> messageToTranslatorMap,
            Map<Class, Translator> stateToTranslatorMap) {
        this.networkSyncer = networkSyncer;
        this.messageToTranslatorMap = messageToTranslatorMap;
        this.stateToTranslatorMap =stateToTranslatorMap;
    }

    public void syncToServer(AggregateGameState gameState) {
        acceptChangesFromServer(gameState);
        sendChangesToServer(gameState);
    }

    private void acceptChangesFromServer(AggregateGameState gameState) {
        List<Object> messages = this.networkSyncer.getIncomingMessages();

        for (Object message : messages) {
            if (messageToTranslatorMap.containsKey(message.getClass())) {
                Translator translator = messageToTranslatorMap.get(message.getClass());
                translator.mergeMessage(gameState, message);
            }
        }
    }

    private void sendChangesToServer(AggregateGameState gameState) {
        for (GameState change : gameState.getStatesChangedBy(ChangeSource.CLIENT)) {
            if (stateToTranslatorMap.containsKey(change.getClass())) {
                Translator translator = stateToTranslatorMap.get(change.getClass());
                List<Object> messages = translator.toMessages(change);
                networkSyncer.sendMessages(messages.get(0).getClass(), messages);
            }
        }
    }
}
