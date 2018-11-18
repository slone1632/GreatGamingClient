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
    private Integer port;
    private Queue<String> outputMessages;
    private DataHandler handler;
    private static final Long HEARTBEAT_TIME_MILLIS = 2000l;
    private boolean keepRunning = true;

    public Syncer(Integer port, DataHandler handler) {
        this.port = port;
        this.outputMessages = new LinkedList<>();
        this.handler = handler;
    }

    public void sendMessage(String message) {
        this.outputMessages.add(message);
    }

    public void stop(){
        sendMessage(Syncer.DISCONNECT_STRING);
        keepRunning = false;
    }

    public void run() {
        Long startTime = System.currentTimeMillis();
        while (keepRunning || outputMessages.peek() != null) {
            try {
                Socket clientSocket = new Socket("localhost", this.port);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                sendMessagesToServer(outToServer);
                String response = inFromServer.readLine();
                if (!response.equals(HEARTBEAT_STRING)) {
                    this.handler.handleData(response);
                }

                clientSocket.close();

                Long endTime = System.currentTimeMillis();
                Long elapsed = endTime - startTime;
                if (elapsed < HEARTBEAT_TIME_MILLIS) {
                    Thread.sleep(HEARTBEAT_TIME_MILLIS - elapsed);
                }
                startTime = System.currentTimeMillis();

            } catch (IOException | InterruptedException ex) {
                System.out.println("sync failed");
            }
        }
    }

    private void sendMessagesToServer(DataOutputStream outToServer) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (this.outputMessages.peek() != null) {
            builder.append(this.outputMessages.poll());
            builder.append(System.lineSeparator());
        }

        String payload = builder.toString();
        if (!"".equals(payload)) {
            outToServer.write(payload.getBytes());
        } else{
            outToServer.write((HEARTBEAT_STRING + System.lineSeparator()).getBytes());
        }
    }
}
