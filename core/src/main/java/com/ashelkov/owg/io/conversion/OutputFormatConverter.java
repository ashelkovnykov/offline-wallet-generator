package com.ashelkov.owg.io.conversion;

import com.beust.jcommander.converters.EnumConverter;

import com.ashelkov.owg.io.storage.OutputFormat;

public class OutputFormatConverter extends EnumConverter<OutputFormat> {
    public OutputFormatConverter(String optionName, Class<OutputFormat> clazz) {
        super(optionName, clazz);
    }
}
