package com.greatgaming.client.engine.state.player;

import com.greatgaming.client.engine.state.ChangeSource;
import com.greatgaming.client.engine.state.GameState;

public class PlayerStatisticsState extends GameState {
    public float getSpeed() {
        return 5f;
    }

    @Override
    public Boolean hasBeenChangedBy(ChangeSource source) {
        return false;
    }
}
