package com.wurmonline.wuhoo.command;

import com.beust.jcommander.Parameters;
import com.wurmonline.server.webinterface.WebInterface;

import java.rmi.RemoteException;

/**
 * Gets information from the server
 */
@Parameters(commandDescription = "Gets general information from the server")
public class CommandGetInfo implements Command {
    @Override
    public String getCommandName() {
        return "getinfo";
    }

    @Override
    public boolean execute(WebInterface serverInterface) throws RemoteException {
        System.out.println(serverInterface.getGameInfo());
        return true;
    }
}
