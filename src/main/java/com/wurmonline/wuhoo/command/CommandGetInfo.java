package com.wurmonline.wuhoo.command;

import com.beust.jcommander.Parameters;

/**
 * Gets information from the server
 */
@Parameters(commandDescription = "Gets general information from the server")
public class CommandGetInfo implements Command {
    @Override
    public String getCommandName() {
        return "getinfo";
    }
}
