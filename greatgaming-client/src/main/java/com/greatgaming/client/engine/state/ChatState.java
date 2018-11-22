package com.greatgaming.client.engine.state;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ChatState extends GameState {
    private List<String> chatLog;
    private Queue<String> chatLogChanges;
    public List<String> getChatLog() {
        return chatLog;
    }

    public ChatState() {
        this.chatLog = new ArrayList<>();
        this.chatLogChanges  = new LinkedList<>();
    }

    public List<String> getPendingChatLogChanges() {
        String message;
        List<String> results = new ArrayList<>();
        while (chatLogChanges.peek() != null) {
            message = chatLogChanges.poll();
            chatLog.add(message);
            results.add(message);
        }
        setUpToDate(true);
        return results;
    }
    public void addToChatLog(String message) {
        setUpToDate(false);
        chatLogChanges.add(message);
    }

    @Override
    public void merge(GameState other) {
        ChatState otherState = (ChatState)other;
        for (String message : otherState.getPendingChatLogChanges()) {
            addToChatLog(message);
        }
    }
}
