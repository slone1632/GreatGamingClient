package com.greatgaming.client;

import com.greatgaming.comms.messages.Chat;

import java.util.LinkedList;
import java.util.Queue;

public class DataHandler implements Runnable{
    private Queue<Chat> inputMessages = new LinkedList<>();
    private boolean keepRunning = true;

    public void handleData(Chat message) {
        this.inputMessages.add(message);
    }

    public void stop(){
        this.keepRunning = false;
    }

    @Override
    public void run() {
        while (this.keepRunning) {
            while (this.inputMessages.peek() != null) {
                Chat message = this.inputMessages.poll();
                System.out.println(message.message);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                System.out.println("Couldn't sleep coach");
            }
        }
        System.out.println("see ya!");
    }
}
