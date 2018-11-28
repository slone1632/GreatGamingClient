package com.greatgaming.client.engine;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GameBridgeFactoryTest {
    @Test
    public void test() {
        GameBridgeFactory factory = new GameBridgeFactory();
        GameBridge bridge = factory.buildGameBridge(null);
        assertNotNull(bridge);
    }
}
