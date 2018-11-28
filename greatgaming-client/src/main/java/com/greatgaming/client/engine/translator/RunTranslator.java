package com.greatgaming.client.engine.translator;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.GameState;
import com.greatgaming.client.engine.state.RunState;
import com.greatgaming.comms.messages.DisconnectRequest;

import java.util.ArrayList;
import java.util.List;

public class RunTranslator implements Translator {
    @Override
    public List<Object> toMessages(GameState gameState) {
        RunState runState = (RunState)gameState;
        List<Object> returnList = new ArrayList<>(1);
        if (!runState.keepRunning()) {
            returnList.add(new DisconnectRequest());
        }
        return returnList;
    }

    @Override
    public void mergeMessage(AggregateGameState gameState, Object message) {
        RunState runState = gameState.getState(RunState.class);
        runState.serverHasBeenNotified();
    }
}
