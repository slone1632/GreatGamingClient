package com.greatgaming.client.engine.translator;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.GameState;

import java.util.List;

public interface Translator {
    List<Object> toMessages(GameState gameState);
    void mergeMessage(AggregateGameState gameState, Object message);
}
