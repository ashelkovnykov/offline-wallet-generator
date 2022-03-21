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
     * @return [[Writer]] object for saving produced wallets
     */
    public static Writer buildWriter(OutputFormat format, Path outputPath, boolean overwrite) {

        return switch (format) {
            case CONSOLE -> new ConsoleWriter();
            case WALLET -> new WalletWriter(outputPath, overwrite);
        };
    }
}
