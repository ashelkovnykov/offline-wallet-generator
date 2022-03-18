package com.ashelkov.owg.io.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class FileUtils {

    private static final Set<PosixFilePermission> DEFAULT_DIR_PERMISSIONS =
            PosixFilePermissions.fromString("rwxr-x---");
    private static final Set<PosixFilePermission> DEFAULT_FILE_PERMISSIONS =
            PosixFilePermissions.fromString("rw-------");

    private static final String FILE_DELIMITER = ".";

    public static void createDirectory(Path dir)
        throws IOException
    {
        createDirectory(dir, DEFAULT_DIR_PERMISSIONS);
    }

    public static void createDirectory(Path dir, Set<PosixFilePermission> permissions)
        throws IOException
    {
        Files.createDirectories(dir, PosixFilePermissions.asFileAttribute(permissions));
    }

    public static BufferedWriter getBufferedWriter(Path outputPath, boolean overwrite)
            throws IOException {

        Path parentDir = outputPath.getParent();

        if (!Files.exists(parentDir)) {
            FileUtils.createDirectory(parentDir);
        } else if (Files.exists(outputPath) && !overwrite) {
            throw new FileAlreadyExistsException(outputPath.toString());
        }

        return Files.newBufferedWriter(outputPath, US_ASCII);
    }

    public static void setPermissions(Path path, Set<PosixFilePermission> permissions)
            throws IOException
    {
        if (!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }

        Files.setPosixFilePermissions(path, permissions);
    }

    public static void setFilePermissions(Path filePath)
            throws IOException
    {
        setFilePermissions(filePath, DEFAULT_FILE_PERMISSIONS);
    }

    public static void setFilePermissions(Path filePath, Set<PosixFilePermission> permissions)
            throws IOException
    {
        setPermissions(filePath, permissions);
    }

    public static Path resolvePath(Path path, String fileName, String fileExtension) {

        if (Files.isDirectory(path)) {
            return path.resolve(String.join(FILE_DELIMITER, fileName, fileExtension));
        }

        return path;
    }

    private FileUtils() {}
}
