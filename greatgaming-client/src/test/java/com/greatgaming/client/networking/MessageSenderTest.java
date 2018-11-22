package com.greatgaming.client.networking;

import com.greatgaming.comms.messages.LoginRequest;
import com.greatgaming.comms.serialization.Serializer;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MessageSenderTest {
    @Test
    public void testHasPendingMessage_noPendingMessages_false() throws Exception {
        StreamFactory factory = mock(StreamFactory.class);
        Serializer serializer = mock(Serializer.class);

        MessageSender sender = new MessageSender(factory, serializer);
        assertFalse(sender.hasPendingMessages());
    }

    @Test
    public void testHasPendingMessage_pendingMessages_true() throws Exception {
        StreamFactory factory = mock(StreamFactory.class);
        Serializer serializer = mock(Serializer.class);
        when(serializer.serialize(any(), any())).thenReturn("");

        MessageSender sender = new MessageSender(factory, serializer);
        sender.addMessage(Object.class, new Object());
        assertTrue(sender.hasPendingMessages());
    }

    @Test
    public void sendMessages_nothingPending_nothingSent() throws Exception {
        StreamFactory factory = mock(StreamFactory.class);
        Serializer serializer = mock(Serializer.class);

        MessageSender sender = new MessageSender(factory, serializer);

        sender.sendMessages();
    }

    @Test
    public void sendMessages_messagePending_sent() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream mySteam = new DataOutputStream(stream);
        StreamFactory factory = mock(StreamFactory.class);
        when(factory.getStreamToServer()).thenReturn(mySteam);
        Serializer serializer = mock(Serializer.class);
        when(serializer.serialize(any(), any())).thenReturn("");

        LoginRequest request = new LoginRequest();
        MessageSender sender = new MessageSender(factory, serializer);
        sender.addMessage(LoginRequest.class, request);

        sender.sendMessages();
        verify(factory, times(1)).getStreamToServer();
        verify(serializer, times(1)).serialize(LoginRequest.class, request);
        String sent = new String(stream.toByteArray());
        assertEquals(System.lineSeparator(), sent);
    }
}
