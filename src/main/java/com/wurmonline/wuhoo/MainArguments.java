package com.wurmonline.wuhoo;

import com.beust.jcommander.Parameter;
import com.wurmonline.server.webinterface.WebInterface;

/**
 * Main arguments to the program are configured and accessed through this class.
 */
public class MainArguments {
    @Parameter(names = { "-h", "--hostname" })
    private String hostname = "localhost";

    @Parameter(names = { "-p", "--port" })
    private int port = WebInterface.DEFAULT_RMI_PORT;

    @Parameter(names = { "-n", "--name" })
    private String name = "WebInterface";

    /**
     * @return Name of the host to which to connect for RMI calls
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @return Port on which the RMI interface resides
     */
    public int getPort() {
        return port;
    }

    /**
     * @return Name of the web interface
     */
    public String getName() {
        return name;
    }
}
