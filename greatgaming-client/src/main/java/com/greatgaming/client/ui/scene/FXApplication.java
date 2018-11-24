package com.greatgaming.client.ui.scene;

import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.GameState;
import com.greatgaming.client.engine.state.RunState;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXApplication extends Application {
    private final Scene scene;
    private final AggregateGameState gameState;

    public FXApplication(Scene scene, AggregateGameState gameState) {
        this.scene = scene;
        this.gameState= gameState;
    }

    public void launchApp() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(scene);
        primaryStage.setTitle("GreatGaming!");
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.out.println("Shut down was called here");
        RunState runState = this.gameState.getState(RunState.class);
        runState.shutDownGame();
    }
}
