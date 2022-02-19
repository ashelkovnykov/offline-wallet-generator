package com.ashelkov.owg.io.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashelkov.owg.io.util.FileUtils;
import com.ashelkov.owg.wallet.Wallet;

public final class WalletWriter extends Writer {

    private static final Logger logger = LoggerFactory.getLogger(WalletWriter.class);

    private static final String DEFAULT_FILE_EXT = "wal";

    private final Path basePath;
    private final boolean overwrite;

    public WalletWriter(Path basePath, boolean overwrite) {
        this.basePath = basePath;
        this.overwrite = overwrite;
    }

    public void saveWallet(String mnemonic, Wallet wallet) {

        logger.debug(String.format("WalletWriter.save() called with base path '%s'", basePath));

        Path outputPath = FileUtils.resolvePath(basePath, wallet.identifier(), DEFAULT_FILE_EXT);

        logger.debug(String.format("Attempting to save wallet to file '%s'", outputPath));

        try (BufferedWriter writer = FileUtils.getBufferedWriter(outputPath, overwrite)) {

            // Output timestamp
            writer.write(new Date(System.currentTimeMillis()).toString());
            writer.write('\n');
            writer.write('\n');

            // Output mnemonic
            writer.write(mnemonic);
            writer.write('\n');
            writer.write('\n');

            // Output wallet
            writer.write(wallet.toString());
            writer.write('\n');

            FileUtils.setFilePermissions(outputPath);

        } catch (IOException e) {
            logger.error(e.toString());
            System.exit(1);
        }

        System.out.println("Saved wallet to " + outputPath);
    }
}
