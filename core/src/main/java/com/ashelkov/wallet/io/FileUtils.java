package com.ashelkov.wallet.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ashelkov.wallet.bip.address.Bip44Address;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private static final Set<PosixFilePermission> WALLET_DIR_PERMISSIONS =
            PosixFilePermissions.fromString("rwxr-x---");
    private static final Set<PosixFilePermission> WALLET_FILE_PERMISSIONS =
            PosixFilePermissions.fromString("rwx------");

    private static final String OSX_WALLET_DIR = String.format("Library%sWallets", File.separator);
    private static final String WINDOWS_WALLET_DIR = "Wallets";
    private static final String LINUX_WALLET_DIR = ".wallets";

    private static final String DEFAULT_FILE_EXT = "wal";
    private static final String DEFAULT_FILE_NAME = String.format(
            "%s.%s",
            new java.sql.Date(System.currentTimeMillis()).toString(),
            DEFAULT_FILE_EXT);

    public static String getDefaultWalletDir() {

        String root;
        String dir;

        if (SystemUtils.IS_OS_MAC_OSX) {
            root = System.getProperty("user.home");
            dir = OSX_WALLET_DIR;
        } else if (SystemUtils.IS_OS_WINDOWS) {
            root = System.getenv("APPDATA");
            dir = WINDOWS_WALLET_DIR;
        } else {
            root = System.getProperty("user.home");
            dir = LINUX_WALLET_DIR;
        }

        return String.join(File.separator, root, dir, DEFAULT_FILE_NAME);
    }

    public static void saveAddressesToFile(Path filePath, String mnemonic, List<Bip44Address> addresses) {

        // Check if directory exists - if not, create it
        Path rootDir = filePath.getParent();
        if (!Files.exists(rootDir)) {
            createDirectory(rootDir);
        }

        // Output mnemonic and addresses to file
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, US_ASCII)) {

            String delim = ": ";

            writer.write(new Date(System.currentTimeMillis()).toString());
            writer.write('\n');
            writer.write('\n');
            writer.write(mnemonic);

            for (Bip44Address address : addresses) {

                // Spacing
                writer.write('\n');
                writer.write('\n');

                // Coin name
                writer.write('(');
                writer.write(address.getCoin());
                writer.write(')');

                // Wallet path
                writer.write(' ');
                writer.write(address.getPath());

                // Address
                writer.write(delim);
                writer.write(address.getAddress());
            }
            writer.write('\n');

            Files.setPosixFilePermissions(filePath, WALLET_FILE_PERMISSIONS);

        } catch (IOException e) {
            logger.error(e.getMessage());
            System.exit(1);
        }
    }

    public static void createDirectory(Path dir) {
        try {
            Files.createDirectory(dir, PosixFilePermissions.asFileAttribute(WALLET_DIR_PERMISSIONS));
        } catch (IOException e) {
            logger.error(e.getMessage());
            System.exit(1);
        }
    }
}
