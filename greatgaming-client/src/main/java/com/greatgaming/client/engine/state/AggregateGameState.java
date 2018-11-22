package com.greatgaming.client.engine.state;

import java.util.*;

public class AggregateGameState {
    private Map<Class, GameState> gameStates;
    public AggregateGameState(){
        this.gameStates = new HashMap<>();
        this.gameStates.put(ChatState.class, new ChatState());
        this.gameStates.put(RunState.class, new RunState());
    }

    public <T extends GameState> T getState(Class<T> clazz) {
        return (T)gameStates.getOrDefault(clazz, null);
    }

    public List<GameState> getStatesThatHaveChanged() {
        List<GameState> changes = new ArrayList<>();
        for (GameState state : gameStates.values()) {
            if (!state.isUpToDate()){
                changes.add(state);
            }
        }
        return changes;
    }

    public void processingComplete() {
        for (GameState state : gameStates.values()) {
            state.setUpToDate(true);
        }
    }
}
