package com.wurmonline.wuhoo;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates command line options
 */
public class CommandLineOptions {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(description = "Command to be executed")
    private String command;

    @Parameter(names = "-h,--host")
    private String hostName;
}
