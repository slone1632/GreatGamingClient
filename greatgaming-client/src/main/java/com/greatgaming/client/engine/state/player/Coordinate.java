package com.greatgaming.client.engine.state.player;

public class Coordinate {
    private final float y;
    private final float x;

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public float getX(){
        return x;
    }
    public float getY() {
        return y;
    }
}
