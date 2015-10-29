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
import java.util.*;

/**
 * Main class
 */
public class WuHoo {
    private static final String SHUTDOWN_INSTIGATOR = "Remote command";
    private static final int ERR_BAD_ARGUMENTS = 1;
    private static final int ERR_REMOTE_INTERFACE_LOOKUP = 2;
    private static final Map<String, Command> HANDLED_COMMANDS;
    static {
        HashMap<String, Command> handledCommands = new HashMap<>();
        for (Command command : new Command[] {
                new CommandGetInfo(),
                new CommandBroadcastMessage(),
                new CommandStartShutdown()
        }) {
            handledCommands.put(command.getCommandName(), command);
        }
        HANDLED_COMMANDS = Collections.unmodifiableMap(handledCommands);
    }

    public static void main(String[] args) {
        MainArguments mainArgs = new MainArguments();
        JCommander jCommander = new JCommander(mainArgs);
        for (Command command : HANDLED_COMMANDS.values()) {
            addCommand(jCommander, command);
        }
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
            if (HANDLED_COMMANDS.containsKey(command)) {
                HANDLED_COMMANDS.get(command).execute(webInterface);
            } else {
                System.out.println("Unhandled command: " + command);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
        public static final String START_SHUTDOWN = "startshutdown";
        private static final int DEFAULT_SECONDS_TO_SHUTDOWN = 60 * 10;

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

    @Parameters(commandDescription = "Broadcasts a server-wide message")
    private static class CommandBroadcastMessage implements Command {
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
    private static class MainArguments {
        @Parameter(names = { "-h", "--hostname" })
        private String hostname = "localhost";

        @Parameter(names = { "-p", "--port" })
        private int port = WebInterface.DEFAULT_RMI_PORT;

        @Parameter(names = { "-n", "--name" })
        private String name = "WebInterface";
    }
}
