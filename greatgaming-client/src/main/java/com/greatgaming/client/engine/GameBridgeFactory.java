package com.greatgaming.client.engine;

import com.greatgaming.client.engine.state.ChatState;
import com.greatgaming.client.engine.state.RunState;
import com.greatgaming.client.engine.translator.ChatTranslator;
import com.greatgaming.client.engine.translator.RunTranslator;
import com.greatgaming.client.engine.translator.Translator;
import com.greatgaming.client.networking.Syncer;
import com.greatgaming.comms.messages.Chat;
import com.greatgaming.comms.messages.DisconnectResponse;

import java.util.HashMap;
import java.util.Map;

public class GameBridgeFactory {
    public GameBridge buildGameBridge(Syncer networkSyncer) {
        Map<Class, Translator> messageToTranslatorMap = new HashMap<>();
        Map<Class, Translator> stateToTranslatorMap = new HashMap<>();

        Translator chatTranslator = new ChatTranslator();
        messageToTranslatorMap.put(Chat.class, chatTranslator);
        stateToTranslatorMap.put(ChatState.class, chatTranslator);

        Translator runStateTranslator = new RunTranslator();
        messageToTranslatorMap.put(DisconnectResponse.class, runStateTranslator);
        stateToTranslatorMap.put(RunState.class, runStateTranslator);

        return new GameBridge(networkSyncer, messageToTranslatorMap, stateToTranslatorMap);
    }
}
