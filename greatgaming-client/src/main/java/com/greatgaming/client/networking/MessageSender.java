package com.greatgaming.client.networking;


import com.greatgaming.comms.serialization.Serializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class MessageSender {
    private StreamFactory streamFactory;
    private Serializer serializer;
    private Queue<String> outputMessages;

    public MessageSender(StreamFactory streamFactory, Serializer serializer) {
        this.streamFactory =streamFactory;
        this.serializer = serializer;
        this.outputMessages = new LinkedList<>();
    }

    public boolean hasPendingMessages() {
        return this.outputMessages.peek() != null;
    }

    public <T> void addMessage(Class<T> clazz, T message) {
        String payload = this.serializer.serialize(clazz, message);
        this.outputMessages.add(payload);
    }

    public void sendMessages() throws ServerUnavailableException {
        if (!hasPendingMessages()) {
            return;
        }
        DataOutputStream outToServer = this.streamFactory.getStreamToServer();
        while (hasPendingMessages()) {
            try {
                outToServer.writeBytes(this.outputMessages.poll() + System.lineSeparator());
                outToServer.flush();
            } catch (IOException ex) {
                System.out.println("Failed to send a message... skipping it");
            }
        }
    }
}
