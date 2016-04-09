package com.quiptiq.wuremote.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.wurmonline.server.webinterface.WebInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to broadcast a server-wide message. Accepts a message as its parameter, in one of the following forms:
 * <pre>
 * {@value #BROADCAST} Your message here
 * {@value #BROADCAST} "Your message here"
 * </pre>
 * The former will be treated as a space delimited sequence of words, and will be joined by spaces to form a single
 * message.
 */
@Parameters(commandDescription = "Broadcasts a server-wide message")
public class CommandBroadcastMessage implements Command {
    public static final String BROADCAST = "broadcast";

    @Parameter(description = "Message to be broadcast to the server")
    List<String> message = new ArrayList<>();

    public String getCommandName() {
        return BROADCAST;
    }

    public boolean execute(WebInterface serverInterface) throws RemoteException {
        if (message == null) {
            System.err.println("Cannot broadcast null message");
        } else if (message.isEmpty()) {
            System.err.println("Cannot broadcast empty message");
        } else {
            if (message.size() > 1) {
                serverInterface.broadcastMessage(String.join(" ", message));
            } else {
                serverInterface.broadcastMessage(message.get(0));
            }
            return true;
        }
        return false;
    }
}
