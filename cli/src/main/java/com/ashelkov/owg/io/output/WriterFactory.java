package com.ashelkov.owg.io.output;

import java.nio.file.Path;

/**
 * Factory class for the various wallet output options.
 */
public final class WriterFactory {

    private WriterFactory() {}

    /**
     * Map output settings to an object of the appropriate class for outputting produced wallets.
     *
     * @param format Type of output to produce
     * @param outputPath Location to produce output
     * @param overwrite Overwrite existing output of the same name, if true
     * @param outputFilename Custom filename to use for the output (without extension)
     * @return [[Writer]] object for saving produced wallets
     */
    public static Writer buildWriter(OutputFormat format, Path outputPath, boolean overwrite, String outputFilename) {

        return switch (format) {
            case CONSOLE -> new ConsoleWriter();
            case WALLET -> new WalletWriter(outputPath, overwrite, outputFilename);
        };
    }

    /**
     * Map output settings to an object of the appropriate class for outputting produced wallets.
     * This method maintains backward compatibility with code that doesn't use the custom filename.
     *
     * @param format Type of output to produce
     * @param outputPath Location to produce output
     * @param overwrite Overwrite existing output of the same name, if true
     * @return [[Writer]] object for saving produced wallets
     */
    public static Writer buildWriter(OutputFormat format, Path outputPath, boolean overwrite) {
        return buildWriter(format, outputPath, overwrite, null);
    }
}
