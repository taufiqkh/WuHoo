package com.quiptiq.wuremote;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.wurmonline.server.webinterface.WebInterface;
import com.quiptiq.wuremote.command.Command;
import com.quiptiq.wuremote.command.CommandBroadcastMessage;
import com.quiptiq.wuremote.command.CommandGetInfo;
import com.quiptiq.wuremote.command.CommandStartShutdown;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses commands. This class is not thread-safe.
 */
public class CommandParser {
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

    private final MainArguments mainArguments;
    private final JCommander jCommander;
    private boolean hasParsedCommand = false;
    private Command command;

    public CommandParser() {
        mainArguments = new MainArguments();
        jCommander = new JCommander(mainArguments);
        for (Command command : HANDLED_COMMANDS.values()) {
            addCommand(jCommander, command);
        }
    }

    /**
     * Parses the given array of command line arguments
     * @param args Arguments to parse
     */
    public CommandParser parse(String... args) {
        jCommander.parse(args);
        String parsedCommand = jCommander.getParsedCommand();
        hasParsedCommand = parsedCommand != null;
        command = null;
        if (hasParsedCommand && isRecognisedCommand()) {
            command = HANDLED_COMMANDS.get(parsedCommand);
        }
        return this;
    }

    /**
     * @return Name of the host to which to connect for RMI calls
     */
    public String getHostname() {
        return mainArguments.hostname;
    }

    /**
     * @return Port on which the RMI interface resides
     */
    public int getPort() {
        return mainArguments.port;
    }

    /**
     * @return Name of the web interface
     */
    public String getName() {
        return mainArguments.name;
    }

    public String getPassword() {
        return mainArguments.password;
    }

    /**
     * @return The primary command given, as parsed from the command line
     */
    public String getParsedCommand() {
        return jCommander.getParsedCommand();
    }

    /**
     * @return Whether or not the parse resulted in a command, regardless of whether or not that command was valid.
     */
    public boolean hasParsedCommand() {
        return hasParsedCommand;
    }

    /**
     * @return Whether or not the parsed command has been recognised.
     */
    public boolean isRecognisedCommand() {
        return HANDLED_COMMANDS.containsKey(jCommander.getParsedCommand());
    }

    public Command getCommand() {
        return command;
    }

    private static <T extends Command> T addCommand(JCommander jCommander, T command) {
        jCommander.addCommand(command.getCommandName(), command);
        return command;
    }

    /**
     * Main arguments to the program are configured and accessed through this class.
     */
    private static class MainArguments {
        @Parameter(names = {"-h", "--hostname"})
        private String hostname = "localhost";

        @Parameter(names = {"-p", "--port"})
        private int port = WebInterface.DEFAULT_RMI_PORT;

        @Parameter(names = {"-n", "--name"})
        private String name = "WebInterface";

        @Parameter(names = {"-p", "--password"}, description = "Connection password", password = true)
        private String password = null;
    }
}
