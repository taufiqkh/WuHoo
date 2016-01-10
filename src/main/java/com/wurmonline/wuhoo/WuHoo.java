package com.wurmonline.wuhoo;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.wurmonline.server.webinterface.WebInterface;
import com.wurmonline.wuhoo.command.Command;
import com.wurmonline.wuhoo.command.CommandBroadcastMessage;
import com.wurmonline.wuhoo.command.CommandGetInfo;
import com.wurmonline.wuhoo.command.CommandStartShutdown;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Command-line interface to the Wurm Server RMI.
 */
public class WuHoo {
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

    /**
     * Process command-line arguments and call the web interface.
     *
     * @param args Command line arguments.
     */
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
                    "//" + mainArgs.getHostname() + ":" + mainArgs.getPort() + "/" + mainArgs.getName());
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

}
