package com.greatgaming.client;

import com.greatgaming.comms.messages.LoginRequest;
import com.greatgaming.comms.messages.LoginResponse;
import com.greatgaming.comms.serialization.Serializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class LoginHelper {
    private static Integer WELCOME_PORT = 6789;

    public Integer getGamePort(Serializer serializer, String username, String serverAddress) {
        try {
            Socket clientSocket = new Socket(serverAddress, WELCOME_PORT);

            LoginRequest request = new LoginRequest();
            request.username = username;
            String loginPayload = serializer.serialize(LoginRequest.class, request);

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            outToServer.writeBytes(loginPayload + System.lineSeparator());
            String persistentPort = inFromServer.readLine();
            LoginResponse response = (LoginResponse)serializer.deserialize(persistentPort);
            clientSocket.close();
            return response.gamePort;
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }
}
