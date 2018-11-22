package com.greatgaming.client;

import com.greatgaming.comms.messages.Chat;
import com.greatgaming.comms.messages.DisconnectResponse;
import com.greatgaming.comms.serialization.Serializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Syncer implements Runnable{
    private Integer port;
    private Queue<String> outputMessages;
    private DataHandler handler;
    private boolean keepRunning = true;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;
    private Serializer serializer;
    private String serverAddress;

    public Syncer(Integer port, DataHandler handler, Serializer serializer, String serverAddress) {
        this.port = port;
        this.outputMessages = new LinkedList<>();
        this.handler = handler;
        this.serializer = serializer;
        this.serverAddress= serverAddress;
    }

    public <T> void sendMessage(Class<T> clazz, T messageObject) {
        String payload = this.serializer.serialize(clazz, messageObject);
        this.outputMessages.add(payload);
    }

    public void stop(){
        keepRunning = false;
    }

    private void openSocket(int numRetries) {
        if (numRetries == 0) {
            throw new RuntimeException("Could not open client socket");
        }
        try {
            Socket clientSocket = new Socket(this.serverAddress, this.port);
            clientSocket.setKeepAlive(true);
            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            openSocket(numRetries - 1);
        }
    }

    public void run() {
        openSocket(3);
        while (keepRunning || outputMessages.peek() != null) {
            try {
                sendMessagesToServer(outToServer);

                while (inFromServer.ready()) {
                    String response = inFromServer.readLine();
                    Object message = this.serializer.deserialize(response);
                    if (message instanceof Chat) {
                        this.handler.handleData((Chat)message);
                    } else if (message instanceof DisconnectResponse) {
                        System.out.println("The server acknowledged our shutdown");
                        stop();
                    }
                }
                Thread.sleep(10);
            } catch (IOException | InterruptedException ex) {
                System.out.println("sync failed");
                openSocket(3);
                run();
            }
        }
    }

    private void sendMessagesToServer(DataOutputStream outToServer) throws IOException {
        if (this.outputMessages.peek() == null) {
            return;
        }
        while (this.outputMessages.peek() != null) {
            outToServer.writeBytes(this.outputMessages.poll() + System.lineSeparator());
            outToServer.flush();
        }
    }
}
