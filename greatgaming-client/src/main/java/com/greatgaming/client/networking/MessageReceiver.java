package com.greatgaming.client.networking;


import com.greatgaming.comms.serialization.Serializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageReceiver {
    private StreamFactory streamFactory;
    private Serializer serializer;
    public MessageReceiver(StreamFactory streamFactory, Serializer serializer) {
        this.streamFactory = streamFactory;
        this.serializer = serializer;
    }

    public List<Object> receiveMessages() throws ClientMisconfiiguredException {
        List<Object> messages = new ArrayList<>();
        BufferedReader inFromServer = this.streamFactory.getStreamFromServer();
        try {
            while (inFromServer.ready()) {
                String response = inFromServer.readLine();
                Object message = this.serializer.deserialize(response);
                messages.add(message);
            }
        } catch (IOException ex) {
            throw new ClientMisconfiiguredException(ex);
        }

        return messages;
    }
}
