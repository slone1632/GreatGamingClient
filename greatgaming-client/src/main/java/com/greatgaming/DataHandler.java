package com.greatgaming;

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
                System.out.println(this.inputMessages.poll() + System.lineSeparator());
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
