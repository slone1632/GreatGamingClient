package com.greatgaming.client.networking;

public class ClientMisconfiiguredException extends Exception {
    private Exception nestedException;
    ClientMisconfiiguredException(Exception nestedException) {
        this.nestedException = nestedException;
    }
}
