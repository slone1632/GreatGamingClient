package com.greatgaming.client;

import java.util.LinkedList;
import java.util.Queue;

public class DataHandler implements Runnable{
    private Queue<String> inputMessages = new LinkedList<>();
    private boolean keepRunning = true;

    public void handleData(String data) {
        this.inputMessages.add(data);
    }

    public void stop(){
        this.keepRunning = false;
    }

    @Override
    public void run() {
        while (this.keepRunning) {
            while (this.inputMessages.peek() != null) {
                String message = this.inputMessages.poll();
                if (!message.equals(Syncer.HEARTBEAT_STRING)) {
                    System.out.println(message);
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