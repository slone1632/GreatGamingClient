package com.greatgaming.client;

import com.greatgaming.comms.messages.Chat;
import com.greatgaming.comms.messages.DisconnectRequest;
import com.greatgaming.comms.messages.DisconnectResponse;
import com.greatgaming.comms.messages.HeartbeatAcknowledge;
import com.greatgaming.comms.serialization.Serializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Syncer implements Runnable{
    public static final String HEARTBEAT_STRING = "HEARTBEAT";
    public static final String DISCONNECT_STRING = "TCENNOCSID";
    private Integer port;
    private Queue<String> outputMessages;
    private Queue<String> inputMessages;
    private DataHandler handler;
    private boolean keepRunning = true;
    private Socket clientSocket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;
    private Serializer serializer;

    public Syncer(Integer port, DataHandler handler, Serializer serializer) {
        this.port = port;
        this.outputMessages = new LinkedList<>();
        this.inputMessages = new LinkedList<>();
        this.handler = handler;
        this.serializer = serializer;
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
            this.clientSocket = new Socket("localhost", this.port);
            this.clientSocket.setKeepAlive(true);
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
