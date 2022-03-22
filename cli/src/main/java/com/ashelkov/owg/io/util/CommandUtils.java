package com.ashelkov.owg.io.util;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JCommander utilities.
 */
public class CommandUtils {

    private static final Logger logger = LoggerFactory.getLogger(CommandUtils.class);

    /**
     * Add a sub-command to a JCommander command.
     *
     * @param root Root JCommander object which contains all commands
     * @param parent Name of command to which to add sub-command
     * @param name Name of sub-command
     * @param command Sub-command object
     * @param aliases Sub-command aliases
     */
    public static void addSubCommand(
            JCommander root,
            String parent,
            String name,
            Object command,
            String... aliases) {
        try {
            JCommander parentCommand = root.findCommandByAlias(parent);
            parentCommand.addCommand(name, command, aliases);

        } catch (IllegalStateException e) {
            logger.error("Cannot find command '%s'", parent);
        }
    }

    private CommandUtils() {}
}
