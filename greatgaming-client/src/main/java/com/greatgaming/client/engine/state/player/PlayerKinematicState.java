package com.greatgaming.client.engine.state.player;

import com.greatgaming.client.engine.state.ChangeSource;
import com.greatgaming.client.engine.state.GameState;

public class PlayerKinematicState extends GameState {
    private ChangeSource positionChangeSource = ChangeSource.NOT_CHANGED;
    private ChangeSource destinationChangeSource = ChangeSource.NOT_CHANGED;
    private Coordinate currentPosition = new Coordinate(100f, 100f);
    private Coordinate destination = currentPosition;

    public void setCurrentPosition(Coordinate coordinate) {
        currentPosition = coordinate;
        positionChangeSource = ChangeSource.SERVER;
    }

    public void setDestination(Coordinate coordinate) {
        destination = coordinate;
        destinationChangeSource = ChangeSource.CLIENT;
    }

    public Coordinate getCurrentPosition() {
        positionChangeSource = ChangeSource.NOT_CHANGED;
        return currentPosition;
    }

    public Coordinate getDestination() {
        destinationChangeSource = ChangeSource.NOT_CHANGED;
        return destination;
    }

    public Coordinate getUnitDirection() {
        float deltaX = destination.getX() - currentPosition.getX();
        float deltaY = destination.getY() - currentPosition.getY();

        float magnitude = (float)Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        return new Coordinate(deltaX / magnitude, deltaY / magnitude);
    }

    @Override
    public Boolean hasBeenChangedBy(ChangeSource source) {
        if (source == ChangeSource.CLIENT) {
            return destinationChangeSource.equals(ChangeSource.CLIENT);
        }
        if (source == ChangeSource.SERVER) {
            return positionChangeSource.equals(ChangeSource.SERVER);
        }
        return false;
    }
}
