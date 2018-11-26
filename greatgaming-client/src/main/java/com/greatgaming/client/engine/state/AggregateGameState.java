package com.greatgaming.client.engine.state;

import com.greatgaming.client.engine.state.player.PlayerKinematicState;
import com.greatgaming.client.engine.state.player.PlayerState;
import com.greatgaming.client.engine.state.player.PlayerStatisticsState;

import java.util.*;

public class AggregateGameState {
    private Map<Class, GameState> gameStates;
    public AggregateGameState(){
        this.gameStates = new HashMap<>();
        this.gameStates.put(ChatState.class, new ChatState());
        this.gameStates.put(RunState.class, new RunState());
        PlayerState playerState = new PlayerState();
        this.gameStates.put(PlayerState.class, playerState);
    }

    public <T extends GameState> T getState(Class<T> clazz) {
        return (T)gameStates.getOrDefault(clazz, null);
    }

    public List<GameState> getStatesChangedBy(ChangeSource changeSource) {
        List<GameState> changes = new ArrayList<>();
        for (GameState state : gameStates.values()) {
            if (state.hasBeenChangedBy(changeSource)){
                changes.add(state);
            }
        }
        return changes;
    }

    public Boolean isInSync() {
        return outgoingMessagesAreSynced() && getStatesChangedBy(ChangeSource.SERVER).size() == 0;
    }
    public Boolean outgoingMessagesAreSynced() {
        return getStatesChangedBy(ChangeSource.CLIENT).size() == 0;
    }
}
