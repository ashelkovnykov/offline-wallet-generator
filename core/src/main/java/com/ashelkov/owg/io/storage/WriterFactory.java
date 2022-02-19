package com.ashelkov.owg.io.storage;

import java.nio.file.Path;

public final class WriterFactory {

    private WriterFactory() {}

    public static Writer buildWriter(OutputFormat format, Path outputPath, boolean overwrite) {

        return switch (format) {
            case WALLET -> new WalletWriter(outputPath, overwrite);
        };
    }
}
