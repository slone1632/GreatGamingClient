package com.greatgaming.client.ui;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.player.Coordinate;
import com.greatgaming.client.engine.state.player.PlayerState;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GameScene {
    public static final Integer DEFAULT_HEIGHT = 380;
    public static final Integer DEFAULT_WIDTH = 380;
    private final AggregateGameState aggregateGameState;
    private final ChatComponent chatComponent;
    private final PlayerComponent playerComponent;
    private GraphicsContext graphicsContext;

    public GameScene(AggregateGameState gameState) {
        this.aggregateGameState = gameState;
        this.chatComponent = new ChatComponent(gameState);
        this.playerComponent = new PlayerComponent(gameState.getState(PlayerState.class));
    }

    public void render() {
        this.chatComponent.render();
        redraw();
        this.playerComponent.render(graphicsContext);
    }

    public Scene getScene() {
        BorderPane pane = new BorderPane();
        Pane chatPane = this.chatComponent.getComponent();

        Canvas canvas = new Canvas(DEFAULT_WIDTH, DEFAULT_HEIGHT - ChatComponent.TOTAL_HEIGHT);
        this.graphicsContext = canvas.getGraphicsContext2D();
        canvas.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                Coordinate newDest = new Coordinate(
                        (float)event.getX(),
                        (float)event.getY()
                );
                this.aggregateGameState.getState(PlayerState.class)
                        .getKinematic()
                        .setDestination(newDest);
            }
        });
        redraw();

        pane.setCenter(canvas);
        pane.setBottom(chatPane);

        return new Scene(pane, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void redraw() {
        graphicsContext.setFill(Color.GREEN);
        graphicsContext.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - ChatComponent.TOTAL_HEIGHT);
    }
}
