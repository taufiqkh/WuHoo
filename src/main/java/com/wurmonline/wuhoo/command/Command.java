package com.wurmonline.wuhoo.command;

import com.wurmonline.server.webinterface.WebInterface;

import java.rmi.RemoteException;

/**
 * Command-line command, for JCommander parsing
 */
public interface Command {
    /**
     * Gets the command name, as entered on the command line.
     * @return Command name
     */
    public String getCommandName();

    /**
     * Executes the command on the given web interface
     * @return True if no errors occurred, otherwise false.
     * @throws RemoteException from executing on the web interface
     */
    public boolean execute(WebInterface serverInterface) throws RemoteException;
}
