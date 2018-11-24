package com.greatgaming.client.engine.state;

public class RunState extends GameState {
    private boolean _keepRunning = true;
    private boolean _serverHasBeenNotifiedOfExit = false;
    public boolean keepRunning() {
        return _keepRunning;
    }

    public void shutDownGame() {
        _keepRunning = false;
    }

    public void serverHasBeenNotified() {
        _serverHasBeenNotifiedOfExit = true;
    }

    @Override
    public Boolean hasBeenChangedBy(ChangeSource source) {
        if (source == ChangeSource.CLIENT && !_keepRunning && !_serverHasBeenNotifiedOfExit) {
            return true;
        } else {
            return false;
        }
    }
}
