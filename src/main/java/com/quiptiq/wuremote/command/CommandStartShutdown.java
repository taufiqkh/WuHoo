package com.quiptiq.wuremote.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.wurmonline.server.webinterface.WebInterface;

import java.rmi.RemoteException;

/**
 * Shutdown command. Directs the server to initiate shutdown, with an optional reason and time to shutdown:
 * <pre>
 * startshutdown [--secondstoshutdown &lt;seconds&gt;] [--reason "&lt;Your reason&gt;"]
 * </pre>
 */
@Parameters(commandDescription = "Commences a shutdown of the server")
public class CommandStartShutdown implements Command {
    public static final String START_SHUTDOWN = "startshutdown";

    public static final int DEFAULT_SECONDS_TO_SHUTDOWN = 60 * 10;

    private static final String SHUTDOWN_INSTIGATOR = "Remote command";

    @Parameter(names = "--secondstoshutdown")
    Integer secondsToShutdown = DEFAULT_SECONDS_TO_SHUTDOWN;

    @Parameter(names = "--reason")
    String reason = "Server is shutting down";

    public boolean execute(WebInterface serverInterface) throws RemoteException {
        try {
            serverInterface.startShutdown(SHUTDOWN_INSTIGATOR, secondsToShutdown, reason);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getCommandName() {
        return START_SHUTDOWN;
    }
}
