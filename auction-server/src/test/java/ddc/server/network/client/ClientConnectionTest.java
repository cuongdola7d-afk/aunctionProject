package ddc.server.network.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;

import ddc.server.network.message.MessageType;
import ddc.server.network.message.SocketMessage;

class ClientConnectionTest {

    @Test
    void connection_shouldManageUserAndSubscriptions() {
        Socket socket = Mockito.mock(Socket.class);
        BufferedReader reader = new BufferedReader(new StringReader(""));
        PrintWriter writer = new PrintWriter(new StringWriter(), true);
        ClientConnection connection = new ClientConnection(socket, reader, writer);

        assertNotNull(connection.getConnectionId());
        assertSame(socket, connection.getSocket());
        assertSame(reader, connection.getReader());

        connection.setUserId("U001");
        assertEquals("U001", connection.getUserId());

        connection.subscribe(null);
        connection.subscribe("");
        connection.subscribe("   ");
        assertFalse(connection.isSubscribedTo("A001"));

        connection.subscribe("A001");
        assertTrue(connection.isSubscribedTo("A001"));

        connection.unsubscribeAll();
        assertFalse(connection.isSubscribedTo("A001"));
    }

    @Test
    void send_shouldWriteSocketMessageJsonLine() throws Exception {
        Gson gson = new Gson();
        StringWriter output = new StringWriter();
        ClientConnection connection = new ClientConnection(
                Mockito.mock(Socket.class),
                new BufferedReader(new StringReader("")),
                new PrintWriter(output, true)
        );

        connection.send(MessageType.PING, java.util.Map.of("ok", true), gson);

        SocketMessage message = gson.fromJson(output.toString().trim(), SocketMessage.class);
        assertEquals(MessageType.PING, message.getType());
        assertEquals("{\"ok\":true}", message.getPayloadJson());
    }

    @Test
    void close_shouldCloseResourcesAndIgnoreSocketCloseException() throws Exception {
        Socket socket = Mockito.mock(Socket.class);
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        PrintWriter writer = Mockito.mock(PrintWriter.class);
        Mockito.doThrow(new java.io.IOException("close failed")).when(socket).close();
        ClientConnection connection = new ClientConnection(socket, reader, writer);

        assertDoesNotThrow(connection::close);

        verify(reader).close();
        verify(writer).close();
        verify(socket).close();
    }
}
