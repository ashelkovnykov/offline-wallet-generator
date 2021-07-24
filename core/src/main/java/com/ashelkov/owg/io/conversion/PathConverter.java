package com.ashelkov.owg.io.conversion;

import java.nio.file.Path;

import com.ashelkov.owg.io.util.FileUtils;

public class PathConverter extends com.beust.jcommander.converters.PathConverter {

    public PathConverter(String optionName) {
        super(optionName);
    }

    @Override
    public Path convert(String value) {

        Path path = super.convert(value);

        if (value.endsWith("/")) {
            FileUtils.createDirectory(path);
        }

        return path;
    }
}
