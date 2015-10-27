package com.wurmonline.wuhoo.command;

/**
 * Command-line command, for JCommander parsing
 */
public interface Command {
    /**
     * Gets the command name, as entered on the command line.
     * @return Command name
     */
    public String getCommandName();
}
