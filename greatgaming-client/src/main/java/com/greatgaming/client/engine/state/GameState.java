package com.greatgaming.client.engine.state;

public abstract class GameState {
    public abstract Boolean hasBeenChangedBy(ChangeSource source);
}
