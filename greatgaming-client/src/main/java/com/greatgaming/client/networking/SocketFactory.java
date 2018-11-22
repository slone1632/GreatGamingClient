package com.greatgaming.client.networking;

import java.io.IOException;
import java.net.Socket;

public class SocketFactory {
    Socket getSocket(String serverAddress, Integer port)
            throws IOException {
        Socket clientSocket = new Socket(serverAddress, port);
        clientSocket.setKeepAlive(true);
        return clientSocket;
    }
}
