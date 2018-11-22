package com.greatgaming.client.networking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class StreamFactory {
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;
    private String serverAddress;
    private SocketFactory socketFactory;
    private Integer port;

    public StreamFactory(
            String serverAddress,
            Integer port,
            SocketFactory factory) {
        this.port = port;
        this.serverAddress= serverAddress;
        this.socketFactory = factory;
    }
    DataOutputStream getStreamToServer() throws ServerUnavailableException  {
        if (this.outToServer == null || this.inFromServer == null) {
            try {
                openSocket(3);
            } catch (IOException ex) {
                throw new ServerUnavailableException(ex);
            }
        }
        return this.outToServer;
    }

    BufferedReader getStreamFromServer() throws ClientMisconfiiguredException {
        if (this.outToServer == null || this.inFromServer == null) {
            try {
                openSocket(3);
            } catch (IOException ex) {
                throw new ClientMisconfiiguredException(ex);
            }
        }
        return this.inFromServer;
    }

    private synchronized void openSocket(int numRetries) throws IOException {
        try {
            Socket clientSocket = socketFactory.getSocket(serverAddress, port);
            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            if (numRetries == 1) {
                throw ex;
            }
            openSocket(numRetries - 1);
        }
    }
}
