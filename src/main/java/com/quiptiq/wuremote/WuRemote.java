package com.quiptiq.wuremote;

import com.beust.jcommander.ParameterException;
import com.wurmonline.server.webinterface.WebInterface;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Command-line interface to the Wurm Server RMI.
 */
public class WuRemote {
    private static final int ERR_BAD_ARGUMENTS = 1;
    private static final int ERR_REMOTE_INTERFACE_LOOKUP = 2;

    /**
     * Process command-line arguments and call the web interface.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        CommandParser parser = new CommandParser();
        try {
            parser.parse(args);
        } catch (ParameterException e) {
            System.err.println("Can't process arguments " + e.getMessage());
            System.exit(ERR_BAD_ARGUMENTS);
        }
        if (!parser.hasParsedCommand()) {
            printUsage();
            System.exit(0);
        }
        final WebInterface webInterface;
        try {
            webInterface = (WebInterface) java.rmi.Naming.lookup(
                    "//" + parser.getHostname() + ":" + parser.getPort() + "/" + parser.getName());
        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            System.err.println("Error looking up remote interface");
            e.printStackTrace();
            System.exit(ERR_REMOTE_INTERFACE_LOOKUP);
            return;
        }
        try {
            if (parser.isRecognisedCommand()) {
                parser.getCommand().execute(webInterface);
            } else {
                System.out.println("Unhandled command: " + parser.getParsedCommand());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println("Executes a command against the server.");
    }

}
