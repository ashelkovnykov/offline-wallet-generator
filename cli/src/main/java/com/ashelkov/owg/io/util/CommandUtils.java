package com.ashelkov.owg.io.util;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandUtils {

    private static final Logger logger = LoggerFactory.getLogger(CommandUtils.class);

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
