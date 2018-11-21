package com.greatgaming;

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
    public static final String CUSTOM_SEPARATOR = "ROTAPAPES";
    private Integer port;
    private Queue<String> outputMessages;
    private Queue<String> inputMessages;
    private DataHandler handler;
    private boolean keepRunning = true;
    private Socket clientSocket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;

    public Syncer(Integer port, DataHandler handler) {
        this.port = port;
        this.outputMessages = new LinkedList<>();
        this.inputMessages = new LinkedList<>();
        this.handler = handler;
    }

    public void sendMessage(String message) {
        this.outputMessages.add(message);
    }

    public void stop(){
        sendMessage(Syncer.DISCONNECT_STRING);
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
                    if (!response.equals(HEARTBEAT_STRING)) {
                        this.handler.handleData(response);
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
