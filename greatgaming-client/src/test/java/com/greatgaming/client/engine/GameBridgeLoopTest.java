package com.greatgaming.client.engine;

import com.greatgaming.client.UIApplication;
import com.greatgaming.client.engine.state.AggregateGameState;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class GameBridgeLoopTest {
    @Test
    public void testStopWorksAndThingsCalled() throws InterruptedException {
        AggregateGameState gameState = mock(AggregateGameState.class);
        when(gameState.outgoingMessagesAreSynced()).thenReturn(true);
        GameBridge gameBridge = mock(GameBridge.class);
        doNothing().when(gameBridge).syncToServer(gameState);
        UIApplication application = mock(UIApplication.class);
        doNothing().when(application).render();

        GameBridgeLoop loop = new GameBridgeLoop(gameState, gameBridge, application);

        Thread thread = new Thread(loop);
        thread.start();
        Thread.sleep(5);

        loop.stop();
        verify(application, atLeastOnce()).render();
        verify(gameBridge, atLeastOnce()).syncToServer(gameState);
    }

    @Test
    public void testOutgaoingMessagesDelayStop() throws InterruptedException {
        AggregateGameState gameState = mock(AggregateGameState.class);
        when(gameState.outgoingMessagesAreSynced()).thenReturn(false);
        GameBridge gameBridge = mock(GameBridge.class);
        doNothing().when(gameBridge).syncToServer(gameState);
        UIApplication application = mock(UIApplication.class);
        doNothing().when(application).render();

        GameBridgeLoop loop = new GameBridgeLoop(gameState, gameBridge, application);
        loop.stop();

        Thread thread = new Thread(loop);
        thread.start();
        Thread.sleep(5);

        when(gameState.outgoingMessagesAreSynced()).thenReturn(false);

        verify(application, atLeastOnce()).render();
        verify(gameBridge, atLeastOnce()).syncToServer(gameState);
    }
}
