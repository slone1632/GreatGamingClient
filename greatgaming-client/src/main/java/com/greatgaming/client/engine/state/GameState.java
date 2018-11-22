package com.greatgaming.client.engine.state;

public abstract class GameState {
    private boolean _isUpToDate = true;
    public abstract void merge(GameState other);
    public boolean isUpToDate(){
        return _isUpToDate;
    }
    protected void setUpToDate(boolean value){
        _isUpToDate = value;
    }
}
