package com.greatgaming.client.networking;

import com.greatgaming.client.engine.GameBridge;
import com.greatgaming.comms.messages.DisconnectResponse;
import com.greatgaming.comms.messages.HeartbeatAcknowledge;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Syncer implements Runnable{
    private boolean keepRunning = true;
    private Queue<Object> messagesToBridge;
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;

    public Syncer(MessageSender sender, MessageReceiver receiver) {
        this.messageSender = sender;
        this.messageReceiver = receiver;
        this.messagesToBridge = new LinkedList<>();
    }

    public <T> void sendMessage(Class<T> clazz, T messageObject) {
        this.messageSender.addMessage(clazz, messageObject);
    }
    public List<Object> getIncomingMessages() {
        List<Object> messages = new ArrayList<>();

        while (messagesToBridge.peek() != null) {
            messages.add(messagesToBridge.poll());
        }

        return messages;
    }

    public void stop(){
        keepRunning = false;
    }

    public void run() {
        List<Object> messages;
        while (keepRunning || this.messageSender.hasPendingMessages()) {
            try {
                this.messageSender.sendMessages();
                messages = this.messageReceiver.receiveMessages();
                for (Object message : messages) {
                    if (message instanceof DisconnectResponse) {
                        messagesToBridge.add(message);
                        stop();
                    } else if (message instanceof HeartbeatAcknowledge){
                        System.out.println("The server acked our heartbeat");
                    } else {
                        messagesToBridge.add(message);
                    }
                }
                Thread.sleep(10);
            } catch (ServerUnavailableException ex) {
                System.out.println("Server is drunk");
            } catch (ClientMisconfiiguredException ex){
                System.out.println("Client is drunk");
            } catch (InterruptedException ex) {
                System.out.println("Java is drunk");
            }
        }
    }
}
