package com.greatgaming.client.networking;

public class ServerUnavailableException extends Exception {
    private Exception nestedException;
    ServerUnavailableException(Exception nestedException) {
        this.nestedException = nestedException;
    }
}
