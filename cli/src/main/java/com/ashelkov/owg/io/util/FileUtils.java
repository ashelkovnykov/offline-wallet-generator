package com.ashelkov.owg.io.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Utilities for managing file I/O.
 */
public class FileUtils {

    private static final Set<PosixFilePermission> DEFAULT_DIR_PERMISSIONS =
            PosixFilePermissions.fromString("rwxr-x---");
    private static final Set<PosixFilePermission> DEFAULT_FILE_PERMISSIONS =
            PosixFilePermissions.fromString("rw-------");

    private static final String FILE_DELIMITER = ".";

    /**
     * Create a directory at the given path with default file permissions.
     *
     * @param dir Path to directory
     * @throws IOException
     */
    public static void createDirectory(Path dir)
        throws IOException
    {
        createDirectory(dir, DEFAULT_DIR_PERMISSIONS);
    }

    /**
     * Create a directory at the given path with the given file permissions.
     *
     * @param dir Path to directory
     * @param permissions Directory file permissions
     * @throws IOException
     */
    public static void createDirectory(Path dir, Set<PosixFilePermission> permissions)
        throws IOException
    {
        Files.createDirectories(dir, PosixFilePermissions.asFileAttribute(permissions));
    }

    /**
     * Create a [[BufferedWriter]] for new file at the given path.
     *
     * @param outputPath Path to new file
     * @param overwrite Overwrite file if true, throw exception if false and file exists already
     * @return BufferedWriter object for file at path
     * @throws IOException
     */
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

    /**
     * Set permissions of file/directory.
     *
     * @param path Path to file/directory
     * @param permissions Permissions to set
     * @throws IOException
     */
    public static void setPermissions(Path path, Set<PosixFilePermission> permissions)
            throws IOException
    {
        if (!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }

        Files.setPosixFilePermissions(path, permissions);
    }

    /**
     * Set file permissions to default.
     *
     * @param filePath Path to file
     * @throws IOException
     */
    public static void setFilePermissions(Path filePath)
            throws IOException
    {
        setFilePermissions(filePath, DEFAULT_FILE_PERMISSIONS);
    }

    /**
     * Set file permissions.
     *
     * @param filePath Path to file
     * @param permissions Permissions to set
     * @throws IOException
     */
    public static void setFilePermissions(Path filePath, Set<PosixFilePermission> permissions)
            throws IOException
    {
        setPermissions(filePath, permissions);
    }

    /**
     * Determines if a path string likely contains a filename with an extension.
     * 
     * @param path The path to check
     * @return true if the path appears to end with a filename.extension
     */
    private static boolean hasFileExtension(Path path) {
        String pathStr = path.toString();
        String fileName = path.getFileName().toString();
        
        // Check if filename contains a dot that's not at the start (hidden files)
        return fileName.indexOf(FILE_DELIMITER) > 0;
    }

    /**
     * Resolve a path to a file:
     *  - If given a file path that already has an extension, return it unchanged
     *  - If given a directory path, append the file name and file extension to the path
     *  - If given a path without an extension, treat it as a filename and add the extension
     *
     * @param path Path to file/directory
     * @param fileName File name, if path doesn't resolve to file
     * @param fileExtension File extension, if path doesn't resolve to file
     * @return Path to file
     */
    public static Path resolvePath(Path path, String fileName, String fileExtension) {
        // If it's a directory, append the filename and extension
        if (Files.isDirectory(path)) {
            return path.resolve(String.join(FILE_DELIMITER, fileName, fileExtension));
        }
        
        // If the path already includes a filename with extension, return it unchanged
        if (hasFileExtension(path)) {
            return path;
        }
        
        // Otherwise, assume it's a filename without extension and add the extension
        Path parentDir = path.getParent();
        if (parentDir == null) {
            // No parent directory specified, use the filename directly
            return Paths.get(String.join(FILE_DELIMITER, path.toString(), fileExtension));
        }
        
        // Construct with parent directory and filename with extension
        return parentDir.resolve(String.join(FILE_DELIMITER, path.getFileName().toString(), fileExtension));
    }

    private FileUtils() {}
}
