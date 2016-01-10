package com.wurmonline.wuhoo;

import com.beust.jcommander.ParameterException;
import com.wurmonline.server.webinterface.WebInterface;
import com.wurmonline.wuhoo.command.Command;
import com.wurmonline.wuhoo.command.CommandBroadcastMessage;
import com.wurmonline.wuhoo.command.CommandGetInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests command parsing.
 */
public class CommandParseTest {
    private CommandParser parser;
    private static WebInterface webInterface;

    @BeforeClass
    public static void setupClass() {
        webInterface = mock(WebInterface.class);
    }

    @Before
    public void setup() {
        reset(webInterface);
        parser = new CommandParser();
    }

    @Test(expected = ParameterException.class)
    public void testMainParametersInvalid() {
        parser.parse("invalid");
    }

    /**
     * Just test a single parameter
     */
    @Test
    public void testHostnameParameters() {
        String testHostname = "192.168.1.100";
        parser.parse("-h", testHostname);
        assertEquals("Hostname should equal expected", testHostname, parser.getHostname());
    }

    /**
     * Setting all parameters should be accessible from the main arguments class.
     */
    @Test
    public void testMainParameters() {
        String hostname = "127.0.0.1";
        int port = 1234;
        String name = "myName";
        parser.parse("--hostname", hostname, "-p", String.valueOf(port), "-n", name);
        assertEquals("Hostname should be as passed in", hostname, parser.getHostname());
        assertEquals("Port should be as passed in", port, parser.getPort());
        assertEquals("Name should be as passed in", name, parser.getName());
    }

    @Test
    public void testBroadcastSingleValue() throws RemoteException {
        String message = "foo";
        parser.parse("broadcast", message);
        executeValid();
        verify(webInterface).broadcastMessage(message);
    }

    @Test
    public void testBroadcastManyValues() throws RemoteException {
        String message = "This is a test";
        parser.parse("broadcast", "This", "is", "a", "test");
        executeValid();
        verify(webInterface).broadcastMessage(message);
    }

    public void testGetGameInfo() throws RemoteException {
        String info = "Game info";
        when(webInterface.getGameInfo()).thenReturn(info);
        parser.parse("getinfo");
        executeValid();
        verify(webInterface).getGameInfo();
    }

    private void executeValid() {
        Command command = parser.getCommand();
        assertNotNull("Returned command should not be null", command);
        try {
            command.execute(webInterface);
        } catch (RemoteException e) {
            assertTrue("Remote exception should not be thrown", false);
        }
    }
}
