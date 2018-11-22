package com.greatgaming.client.ui;

import java.util.Queue;

public class ConsoleReader implements Runnable {
    private Queue<String> messageQueue;
    public ConsoleReader(Queue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        while (true) {
            String message = System.console().readLine();
            messageQueue.add(message);
            if ("exit".equals(message)) {
                break;
            }
        }
    }
}
