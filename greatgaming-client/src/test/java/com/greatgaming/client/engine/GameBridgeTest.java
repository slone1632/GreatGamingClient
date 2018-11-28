package com.greatgaming.client.engine;

import com.greatgaming.client.engine.state.*;
import com.greatgaming.client.engine.translator.Translator;
import com.greatgaming.client.networking.Syncer;
import com.greatgaming.comms.messages.Chat;
import com.greatgaming.comms.messages.DisconnectRequest;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class GameBridgeTest {
    @Test
    public void syncToServer_noMessagesInOrOut_nothingHappens() {
        Syncer networkSyncer = mock(Syncer.class);
        when(networkSyncer.getIncomingMessages()).thenReturn(new ArrayList<>());
        AggregateGameState gameState = mock(AggregateGameState.class);
        when(gameState.getStatesChangedBy(ChangeSource.CLIENT)).thenReturn(new ArrayList<>());
        Translator mockTranslator = mock(Translator.class);

        GameBridge bridge = getTestObject(networkSyncer, mockTranslator);

        bridge.syncToServer(gameState);

        verify(mockTranslator, never()).mergeMessage(any(), any());
        verify(mockTranslator, never()).toMessages(any());
        verify(networkSyncer, never()).sendMessages(any(), any());
    }

    @Test
    public void syncToServer_notSupposedToHandleInOrOut_nothingHappens() {
        Syncer networkSyncer = mock(Syncer.class);
        List<Object> messages = getTestMessages();
        when(networkSyncer.getIncomingMessages()).thenReturn(messages);

        AggregateGameState gameState = mock(AggregateGameState.class);
        List<GameState> states = new ArrayList<>();
        states.add(new ChatState());
        when(gameState.getStatesChangedBy(ChangeSource.CLIENT)).thenReturn(states);
        Translator mockTranslator = mock(Translator.class);

        GameBridge bridge = getTestObject(networkSyncer, mockTranslator);

        bridge.syncToServer(gameState);

        verify(mockTranslator, never()).mergeMessage(any(), any());
        verify(mockTranslator, never()).toMessages(any());
        verify(networkSyncer, never()).sendMessages(any(), any());
    }

    private List<Object> getTestMessages() {
        List<Object> messages = new ArrayList<>();
        messages.add(new Chat());
        return messages;
    }

    @Test
    public void syncToServer_messages_sent() {
        Syncer networkSyncer = mock(Syncer.class);
        List<Object> messages = new ArrayList<>();
        messages.add(new DisconnectRequest());
        when(networkSyncer.getIncomingMessages()).thenReturn(messages);

        AggregateGameState gameState = mock(AggregateGameState.class);
        List<GameState> states = new ArrayList<>();
        states.add(new RunState());
        when(gameState.getStatesChangedBy(any())).thenReturn(states);
        Translator mockTranslator = mock(Translator.class);
        when(mockTranslator.toMessages(any())).thenReturn(getTestMessages());
        doNothing().when(mockTranslator).mergeMessage(any(), any());

        GameBridge bridge = getTestObject(networkSyncer, mockTranslator);

        bridge.syncToServer(gameState);

        verify(mockTranslator, times(1)).mergeMessage(any(), any());
        verify(mockTranslator, times(1)).toMessages(any());
        verify(networkSyncer, times(1)).sendMessages(any(), any());
    }

    private GameBridge getTestObject(Syncer networkSyncer, Translator mockTranslator) {

        Map<Class, Translator> mToSMap = new HashMap<>();
        mToSMap.put(DisconnectRequest.class, mockTranslator);
        Map<Class, Translator> sToMMap = new HashMap<>();
        sToMMap.put(RunState.class, mockTranslator);

        return new GameBridge(networkSyncer, mToSMap, sToMMap);
    }
}
