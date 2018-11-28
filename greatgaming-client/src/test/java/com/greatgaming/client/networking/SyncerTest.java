package com.greatgaming.client.networking;

import com.greatgaming.comms.messages.DisconnectRequest;
import com.greatgaming.comms.messages.DisconnectResponse;
import com.greatgaming.comms.messages.LoginResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SyncerTest {
    @Test
    public void run_sendMessage_sent() throws Exception {
        MessageSender sender = mock(MessageSender.class);
        doNothing().when(sender).addMessage(any(), any());
        when(sender.hasPendingMessages()).thenReturn(false);

        MessageReceiver receiver = mock(MessageReceiver.class);
        when(receiver.receiveMessages()).thenReturn(new ArrayList<>());

        Syncer syncer = new Syncer(sender, receiver);
        Thread thread = new Thread(syncer);
        thread.start();
        DisconnectRequest message = new DisconnectRequest();
        syncer.sendMessage(DisconnectRequest.class, message);

        Thread.sleep(23);
        syncer.stop();

        verify(sender).addMessage(DisconnectRequest.class, message);
    }

    @Test
    public void run_sendMessage_queueCanBlockExit_sent() throws Exception {
        MessageSender sender = mock(MessageSender.class);
        doNothing().when(sender).addMessage(any(), any());
        when(sender.hasPendingMessages()).thenReturn(true);

        MessageReceiver receiver = mock(MessageReceiver.class);
        when(receiver.receiveMessages()).thenReturn(new ArrayList<>());

        Syncer syncer = new Syncer(sender, receiver);
        syncer.stop();
        Thread thread = new Thread(syncer);
        thread.start();
        DisconnectRequest message = new DisconnectRequest();
        syncer.sendMessage(DisconnectRequest.class, message);

        Thread.sleep(23);

        when(sender.hasPendingMessages()).thenReturn(false);
        verify(sender).addMessage(DisconnectRequest.class, message);
    }

    @Test
    public void run_sendMessage_clientException_caught()
            throws ClientMisconfiiguredException, InterruptedException {
        MessageSender sender = mock(MessageSender.class);
        doNothing().when(sender).addMessage(any(), any());
        when(sender.hasPendingMessages()).thenReturn(false);

        MessageReceiver receiver = mock(MessageReceiver.class);
        when(receiver.receiveMessages()).thenThrow(new ClientMisconfiiguredException(new Exception()));

        Syncer syncer = new Syncer(sender, receiver);
        Thread thread = new Thread(syncer);
        thread.start();

        Thread.sleep(2);
        syncer.stop();
    }

    @Test
    public void run_sendMessage_serverException_caught()
            throws ServerUnavailableException, InterruptedException {
        MessageSender sender = mock(MessageSender.class);
        doThrow(new ServerUnavailableException(new Exception())).when(sender).sendMessages();
        when(sender.hasPendingMessages()).thenReturn(false);

        MessageReceiver receiver = mock(MessageReceiver.class);

        Syncer syncer = new Syncer(sender, receiver);
        Thread thread = new Thread(syncer);
        thread.start();

        Thread.sleep(2);
        syncer.stop();
    }

    @Test
    public void run_sendMessage_interruptedException_caught() {
        MessageSender sender = mock(MessageSender.class);
        doNothing().when(sender).addMessage(any(), any());
        when(sender.hasPendingMessages()).thenReturn(false);

        MessageReceiver receiver = mock(MessageReceiver.class);

        Syncer syncer = new Syncer(sender, receiver);
        Thread thread = new Thread(syncer);
        thread.start();
        thread.interrupt();
        syncer.stop();
    }

    @Test
    public void run_receiveMessage_noMessages_empty() throws Exception {
        MessageSender sender = mock(MessageSender.class);
        doNothing().when(sender).addMessage(any(), any());
        when(sender.hasPendingMessages()).thenReturn(false);

        MessageReceiver receiver = mock(MessageReceiver.class);
        when(receiver.receiveMessages()).thenReturn(new ArrayList<>());

        Syncer syncer = new Syncer(sender, receiver);
        Thread thread = new Thread(syncer);
        thread.start();

        Thread.sleep(23);
        syncer.stop();

        List<Object> messages = syncer.getIncomingMessages();
        assertEquals(0, messages.size());
    }

    @Test
    public void run_receiveMessage_messages_received() throws Exception {
        MessageSender sender = mock(MessageSender.class);
        doNothing().when(sender).addMessage(any(), any());
        when(sender.hasPendingMessages()).thenReturn(false);

        MessageReceiver receiver = mock(MessageReceiver.class);
        List<Object> list = new ArrayList<>();
        list.add(new LoginResponse());
        list.add(new DisconnectResponse());
        when(receiver.receiveMessages()).thenReturn(list);

        Syncer syncer = new Syncer(sender, receiver);

        syncer.run();

        List<Object> messages = syncer.getIncomingMessages();
        assertEquals(2, messages.size());
        assertEquals(LoginResponse.class, messages.get(0).getClass());
        assertEquals(DisconnectResponse.class, messages.get(1).getClass());
    }

    @Test
    public void run_receiveMessage_disconnectMessage_stopped() throws Exception {
        MessageSender sender = mock(MessageSender.class);
        doNothing().when(sender).addMessage(any(), any());
        when(sender.hasPendingMessages()).thenReturn(false);

        MessageReceiver receiver = mock(MessageReceiver.class);
        List<Object> list = new ArrayList<>();
        list.add(new DisconnectResponse());
        when(receiver.receiveMessages()).thenReturn(list);

        Syncer syncer = new Syncer(sender, receiver);

        syncer.run();

        List<Object> messages = syncer.getIncomingMessages();
        assertEquals(1, messages.size());
        assertEquals(DisconnectResponse.class, messages.get(0).getClass());
    }
}
