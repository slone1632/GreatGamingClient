package com.greatgaming.client.engine.state;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ChatState extends GameState {
    private List<String> chatLog;
    private Queue<String> chatLogChangesMadeByServer;
    private Queue<String> chatLogChangesMadeByClient;

    public ChatState() {
        this.chatLog = new ArrayList<>();
        this.chatLogChangesMadeByServer  = new LinkedList<>();
        this.chatLogChangesMadeByClient  = new LinkedList<>();
    }

    public List<String> getChatLog() {
        return chatLog;
    }
    public List<String> getPendingChatLogChanges(ChangeSource source) {
        if (source.equals(ChangeSource.SERVER)) {
            return extractChanges(chatLogChangesMadeByServer);
        } else {
            return extractChanges(chatLogChangesMadeByClient);
        }
    }

    private List<String> extractChanges(Queue<String> queue) {
        String message;
        List<String> results = new ArrayList<>();
        while (queue.peek() != null) {
            message = queue.poll();
            chatLog.add(message);
            results.add(message);
        }
        return results;
    }

    public void addToChatLog(String message, ChangeSource source) {
        if (source.equals(ChangeSource.SERVER)) {
            this.chatLogChangesMadeByServer.add(message);
        } else {
            this.chatLogChangesMadeByClient.add(message);
        }
    }

    @Override
    public Boolean hasBeenChangedBy(ChangeSource source) {
        if (source.equals(ChangeSource.SERVER)) {
            return this.chatLogChangesMadeByServer.peek() != null;
        } else {
            return this.chatLogChangesMadeByClient.peek() != null;
        }
    }
}
