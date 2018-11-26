package com.greatgaming.client.engine.state.player;

import com.greatgaming.client.engine.state.ChangeSource;
import com.greatgaming.client.engine.state.GameState;

public class PlayerState extends GameState {
    private final PlayerStatisticsState statistic;
    private final PlayerKinematicState kinematic;

    public PlayerState() {
        this.kinematic = new PlayerKinematicState();
        this.statistic = new PlayerStatisticsState();
    }

    public PlayerKinematicState getKinematic() {
        return kinematic;
    }

    public PlayerStatisticsState getStatistic() {
        return statistic;
    }

    @Override
    public Boolean hasBeenChangedBy(ChangeSource source) {
        return this.statistic.hasBeenChangedBy(source)
                || this.kinematic.hasBeenChangedBy(source);
    }
}
