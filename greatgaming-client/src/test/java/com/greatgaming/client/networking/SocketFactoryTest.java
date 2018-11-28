package com.greatgaming.client.networking;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketFactoryTest {

    private static final int TEST_PORT = 6788;

    @Test
    public void test() throws Exception {
        ServerSocket serverSocket = new ServerSocket(TEST_PORT);
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket testSocket = serverSocket.accept();
                    testSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();

        SocketFactory factory = new SocketFactory();
        Socket socket = factory.getSocket("localhost", TEST_PORT);
        socket.close();
        serverSocket.close();
    }
}
