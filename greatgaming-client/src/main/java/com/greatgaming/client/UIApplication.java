package com.greatgaming.client;

import com.greatgaming.client.engine.GameBridge;
import com.greatgaming.client.engine.state.AggregateGameState;
import com.greatgaming.client.engine.state.RunState;
import com.greatgaming.client.networking.*;
import com.greatgaming.client.ui.JavaFXUI;
import com.greatgaming.client.ui.scene.GameScene;
import com.greatgaming.client.ui.scene.LoginScene;
import com.greatgaming.comms.serialization.Serializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class UIApplication extends Application {
    private Stage primaryStage;
    private AggregateGameState gameState;
    private GameScene gameScene;
    private JavaFXUI javaFXUI;

    public UIApplication() {
        super();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Login for great gaming");
        primaryStage.setScene(new LoginScene(this).getScene());
        primaryStage.show();
        System.out.println("Got here");
    }

    @Override
    public void stop(){
        System.out.println("Shut down was called here");
        if (this.gameState != null) {
            RunState runState = this.gameState.getState(RunState.class);
            runState.shutDownGame();
            this.javaFXUI.stop();
        }
    }

    public void render() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameScene.render();
            }
        });
    }

    public void initializeGame(String username, String serverAddress) {
        this.gameState = new AggregateGameState();
        primaryStage.hide();
        primaryStage.setTitle("GreatGaming");

        Serializer serializer = new Serializer();

        LoginHelper helper = new LoginHelper();
        int port = helper.getGamePort(serializer, username, serverAddress);

        StreamFactory streamFactory = new StreamFactory(serverAddress, port, new SocketFactory());
        MessageReceiver receiver = new MessageReceiver(streamFactory, serializer);
        MessageSender sender = new MessageSender(streamFactory, serializer);
        Syncer syncer = new Syncer(sender, receiver);

        this.gameScene = new GameScene(this.gameState);
        GameBridge gameBridge = new GameBridge(syncer);
        this.javaFXUI = new JavaFXUI(this.gameState, gameBridge, this);

        this.primaryStage.setScene(gameScene.getScene());
        this.primaryStage.show();

        Thread syncherThread = new Thread(syncer);
        syncherThread.start();

        Thread bridgeThread = new Thread(this.javaFXUI);
        bridgeThread.start();
    }
}
