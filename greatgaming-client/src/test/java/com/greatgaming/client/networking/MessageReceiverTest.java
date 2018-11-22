package com.greatgaming.client.networking;

import com.greatgaming.comms.serialization.Serializer;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageReceiverTest {
    @Test
    public void receiveMessages_noMessages_nothingReturned() throws Exception{
        BufferedReader myReader = mock(BufferedReader.class);
        when(myReader.ready()).thenReturn(false);
        StreamFactory factory = mock(StreamFactory.class);
        when(factory.getStreamFromServer()).thenReturn(myReader);
        Serializer serializer = mock(Serializer.class);
        when(serializer.deserialize(any())).thenReturn(new Object());

        MessageReceiver receiver = new MessageReceiver(factory, serializer);
        List<Object> output = receiver.receiveMessages();
        assertEquals(0, output.size());
    }

    @Test
    public void receiveMessages_message_returned() throws Exception{
        BufferedReader myReader = mock(BufferedReader.class);
        when(myReader.ready()).thenReturn(true).thenReturn(false);
        when(myReader.readLine()).thenReturn("Hi");
        StreamFactory factory = mock(StreamFactory.class);
        when(factory.getStreamFromServer()).thenReturn(myReader);
        Serializer serializer = mock(Serializer.class);
        when(serializer.deserialize(any())).thenReturn(new Object());

        MessageReceiver receiver = new MessageReceiver(factory, serializer);
        List<Object> output = receiver.receiveMessages();
        assertEquals(1, output.size());
    }

    @Test(expected = ClientMisconfiiguredException.class)
    public void receiveMessages_exception_clientConfigurationThrown() throws Exception{
        BufferedReader myReader = mock(BufferedReader.class);
        when(myReader.ready()).thenReturn(true).thenReturn(false);
        when(myReader.readLine()).thenThrow(new IOException());
        StreamFactory factory = mock(StreamFactory.class);
        when(factory.getStreamFromServer()).thenReturn(myReader);
        Serializer serializer = mock(Serializer.class);
        when(serializer.deserialize(any())).thenReturn(new Object());

        MessageReceiver receiver = new MessageReceiver(factory, serializer);
        List<Object> output = receiver.receiveMessages();
    }
}
