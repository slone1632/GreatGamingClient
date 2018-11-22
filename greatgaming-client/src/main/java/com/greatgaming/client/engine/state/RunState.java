package com.greatgaming.client.engine.state;

public class RunState extends GameState {
    private boolean _keepRunning = true;
    public boolean keepRunning() {
        return _keepRunning;
    }

    public void shutDownGame() {
        setUpToDate(false);
        _keepRunning = false;
    }

    @Override
    public void merge(GameState other) {
        setUpToDate(false);
        this._keepRunning = ((RunState) other).keepRunning();
    }
}
