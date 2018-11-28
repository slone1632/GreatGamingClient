package com.greatgaming.client.ui;

import com.greatgaming.client.engine.state.player.Coordinate;
import com.greatgaming.client.engine.state.player.PlayerKinematicState;
import com.greatgaming.client.engine.state.player.PlayerState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PlayerComponent implements GraphicsComponent {
    private static final Integer RADIUS_IN_PIXELS = 20;
    private final PlayerState playerState;

    public PlayerComponent(PlayerState playerState) {
        this.playerState= playerState;
    }
    @Override
    public void render(GraphicsContext graphicsContext) {
        PlayerKinematicState kinematicState = this.playerState.getKinematic();
        Coordinate currentPosition = kinematicState.getCurrentPosition();
        Coordinate destinationPosition = kinematicState.getDestination();
        graphicsContext.setFill(Color.GRAY);
        double centerX = currentPosition.getX() - (RADIUS_IN_PIXELS/2d);
        double centerY = currentPosition.getY() - (RADIUS_IN_PIXELS/2d);
        graphicsContext.fillOval(centerX,
                centerY,
                RADIUS_IN_PIXELS,
                RADIUS_IN_PIXELS);
        graphicsContext.strokeLine(currentPosition.getX(),
                currentPosition.getY(),
                destinationPosition.getX(),
                destinationPosition.getY());
    }
}
