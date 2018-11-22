package com.greatgaming.client.engine;

import com.greatgaming.comms.messages.Chat;

import java.util.LinkedList;
import java.util.Queue;

public class GameEngine implements Runnable{
    private Queue<Object> inputMessages = new LinkedList<>();
    private boolean keepRunning = true;

    public void handleData(Object message) {
        this.inputMessages.add(message);
    }

    public void stop(){
        this.keepRunning = false;
    }

    @Override
    public void run() {
        while (this.keepRunning) {
            while (this.inputMessages.peek() != null) {

                Object message = this.inputMessages.poll();
                if (message instanceof Chat) {
                    System.out.println(((Chat)message).message);
                }
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
