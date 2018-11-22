package com.greatgaming.client.networking;

import com.greatgaming.client.engine.GameEngine;
import com.greatgaming.comms.messages.DisconnectResponse;
import com.greatgaming.comms.messages.HeartbeatAcknowledge;

import java.util.List;

public class Syncer implements Runnable{

    private GameEngine handler;
    private boolean keepRunning = true;
    private MessageSender messageSender;
    private MessageReceiver messageReceiver;

    public Syncer(GameEngine handler, MessageSender sender, MessageReceiver receiver) {
        this.handler = handler;
        this.messageSender = sender;
        this.messageReceiver = receiver;
    }

    public <T> void sendMessage(Class<T> clazz, T messageObject) {
        this.messageSender.addMessage(clazz, messageObject);
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
                        System.out.println("The server acknowledged our shutdown");
                        stop();
                    } else if (message instanceof HeartbeatAcknowledge){
                        System.out.println("The server acked our heartbeat");
                    } else {
                        this.handler.handleData(message);
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
