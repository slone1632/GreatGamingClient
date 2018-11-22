package com.greatgaming.client.networking;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StreamFactoryTest {
    @Test
    public void getStreams_noException_streamReturned() throws Exception {
        SocketFactory factory = mock(SocketFactory.class);
        Socket socket = mock(Socket.class);
        InputStream stream = mock(InputStream.class);
        OutputStream out = mock(OutputStream.class);
        when(socket.getInputStream()).thenReturn(stream);
        when(socket.getOutputStream()).thenReturn(out);
        when(factory.getSocket(any(), any())).thenReturn(socket);

        StreamFactory streamFactory = new StreamFactory("",0,factory);
        assertNotNull(streamFactory.getStreamFromServer());
        assertNotNull(streamFactory.getStreamToServer());
    }

    @Test
    public void getStreams_exception_successOnRetry() throws Exception {
        SocketFactory factory = mock(SocketFactory.class);
        Socket socket = mock(Socket.class);
        InputStream stream = mock(InputStream.class);
        OutputStream out = mock(OutputStream.class);
        when(socket.getInputStream()).thenThrow(new IOException()).thenReturn(stream);
        when(socket.getOutputStream()).thenThrow(new IOException()).thenReturn(out);
        when(factory.getSocket(any(), any())).thenReturn(socket);

        StreamFactory streamFactory = new StreamFactory("",0,factory);
        assertNotNull(streamFactory.getStreamFromServer());
        assertNotNull(streamFactory.getStreamToServer());
    }

    @Test(expected = ClientMisconfiiguredException.class)
    public void getStreams_clientException_maxExceptions() throws Exception {
        SocketFactory factory = mock(SocketFactory.class);
        Socket socket = mock(Socket.class);
        InputStream stream = mock(InputStream.class);
        OutputStream out = mock(OutputStream.class);
        when(socket.getInputStream()).thenThrow(new IOException());
        when(socket.getOutputStream()).thenReturn(out);
        when(factory.getSocket(any(), any())).thenReturn(socket);

        StreamFactory streamFactory = new StreamFactory("",0,factory);
        streamFactory.getStreamFromServer();
    }

    @Test(expected = ServerUnavailableException.class)
    public void getStreams_serverException_maxExceptions() throws Exception {
        SocketFactory factory = mock(SocketFactory.class);
        Socket socket = mock(Socket.class);
        InputStream stream = mock(InputStream.class);
        OutputStream out = mock(OutputStream.class);
        when(socket.getInputStream()).thenReturn(stream);
        when(socket.getOutputStream()).thenThrow(new IOException());
        when(factory.getSocket(any(), any())).thenReturn(socket);

        StreamFactory streamFactory = new StreamFactory("",0,factory);
        streamFactory.getStreamToServer();
    }
}
