package com.wurmonline.wuhoo;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.wurmonline.server.webinterface.WebInterface;
import com.wurmonline.wuhoo.command.Command;
import com.wurmonline.wuhoo.command.CommandGetInfo;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Main class
 */
public class WuHoo {
    private static final String SHUTDOWN_INSTIGATOR = "Remote command";
    private static final int ERR_BAD_ARGUMENTS = 1;
    private static final int ERR_REMOTE_INTERFACE_LOOKUP = 2;

    public static void main(String[] args) {
        MainArguments mainArgs = new MainArguments();
        JCommander jCommander = new JCommander(mainArgs);
        CommandGetInfo getInfo = addCommand(jCommander, new CommandGetInfo());
        CommandStartShutdown shutdown = addCommand(jCommander, new CommandStartShutdown());
        try {
            jCommander.parse(args);
        } catch (ParameterException e) {
            System.err.println("Can't process arguments " + e.getMessage());
            System.exit(ERR_BAD_ARGUMENTS);
        }
        String command = jCommander.getParsedCommand();
        if (command == null) {
            printUsage();
            System.exit(0);
        }
        final WebInterface webInterface;
        try {
            webInterface = (WebInterface) java.rmi.Naming.lookup(
                    "//" + mainArgs.hostname + ":" + mainArgs.port + "/" + mainArgs.name);
        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            System.err.println("Error looking up remote interface");
            e.printStackTrace();
            System.exit(ERR_REMOTE_INTERFACE_LOOKUP);
            return;
        }
        try {
            System.out.println("Game Info:");
            System.out.println(webInterface.getGameInfo());
            if (command.equals(shutdown.getCommandName())) {
                webInterface.startShutdown(SHUTDOWN_INSTIGATOR, shutdown.secondsToShutdown, shutdown.reason);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean startShutdown(WebInterface serverInterface, int secondsToShutdown, String reason) {
        try {
            serverInterface.startShutdown(SHUTDOWN_INSTIGATOR, secondsToShutdown, reason);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static <T extends Command> T addCommand(JCommander jCommander, T command) {
        jCommander.addCommand(command.getCommandName(), command);
        return command;
    }

    private static void printUsage() {
        System.out.println("Executes a command against the server.");
    }

    @Parameters(commandDescription = "Commences a shutdown of the server")
    private static class CommandStartShutdown implements Command {
        private static final String START_SHUTDOWN = "startshutdown";
        private static final int DEFAULT_SECONDS_TO_SHUTDOWN = 60 * 10;

        @Parameter(names = "--secondstoshutdown")
        Integer secondsToShutdown = DEFAULT_SECONDS_TO_SHUTDOWN;

        @Parameter(names = "--reason")
        String reason = "Server is shutting down";

        public String getCommandName() {
            return START_SHUTDOWN;
        }
    }

    private static class MainArguments {
        @Parameter(names = { "-h", "--hostname" })
        private String hostname = "localhost";

        @Parameter(names = { "-p", "--port" })
        private int port = WebInterface.DEFAULT_RMI_PORT;

        @Parameter(names = { "-n", "--name" })
        private String name = "WebInterface";
    }
}
