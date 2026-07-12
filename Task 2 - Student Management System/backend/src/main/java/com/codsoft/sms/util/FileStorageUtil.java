package com.codsoft.sms.util;

import com.codsoft.sms.exception.FileStorageException;
import com.codsoft.sms.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Utility for storing and deleting files on the local filesystem.
 *
 * <p>Validates file types by inspecting actual "magic bytes" (file signatures),
 * completely ignoring the client-provided MIME type or extension. Prevents
 * directory traversal attacks and generates safe UUID-based filenames.
 */
@Component
public class FileStorageUtil {

    private final Path fileStorageLocation;
    private final String uploadDir;
    private final long maxSize;

    /**
     * Initializes the storage directory on startup.
     */
    public FileStorageUtil(
            @Value("${app.upload.dir:uploads/images/}") String uploadDir,
            @Value("${app.upload.max-size:2097152}") long maxSize) { // Default 2MB
        this.uploadDir = uploadDir;
        this.maxSize = maxSize;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * Stores a multipart file securely.
     *
     * @param file the file to store
     * @return the servable URL path of the stored file (e.g., "/uploads/images/xyz.jpg")
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty or missing");
        }

        if (file.getSize() > maxSize) {
            throw new InvalidFileException("File exceeds maximum allowed size of " + (maxSize / 1024 / 1024) + "MB");
        }

        String extension = determineExtensionByMagicBytes(file);

        // Generate non-guessable filename
        String fileName = UUID.randomUUID().toString() + extension;

        try {
            // Check if the file's name contains invalid characters
            // (Even though we generate it, standard safety practice)
            if (fileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + fileName, null);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/" + uploadDir + (uploadDir.endsWith("/") ? "" : "/") + fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file. Please try again!", ex);
        }
    }

    /**
     * Deletes a file from the local storage.
     *
     * @param fileUrl the URL path returned by storeFile (e.g. "/uploads/images/xyz.jpg")
     */
    public void deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) return;
        
        // Extract the filename from the URL
        String fileName = Paths.get(fileUrl).getFileName().toString();
        
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName).normalize();
            
            // Prevent traversal by ensuring the resolved path is still inside the base dir
            if (!targetLocation.startsWith(this.fileStorageLocation)) {
                throw new FileStorageException("Cannot delete file outside storage directory", null);
            }

            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file: " + fileName, ex);
        }
    }

    /**
     * Inspects the file signature (magic bytes) to strictly verify the file format.
     * 
     * @param file the uploaded file
     * @return the appropriate file extension (".jpg" or ".png")
     * @throws InvalidFileException if the file is not a valid JPG or PNG
     */
    private String determineExtensionByMagicBytes(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            int read = is.read(header);
            
            if (read < 4) {
                throw new InvalidFileException("File is too small or corrupted");
            }

            // Check JPEG (FF D8 FF)
            if ((header[0] & 0xFF) == 0xFF && 
                (header[1] & 0xFF) == 0xD8 && 
                (header[2] & 0xFF) == 0xFF) {
                return ".jpg";
            }

            // Check PNG (89 50 4E 47 0D 0A 1A 0A)
            if (read >= 8 &&
                (header[0] & 0xFF) == 0x89 &&
                (header[1] & 0xFF) == 0x50 &&
                (header[2] & 0xFF) == 0x4E &&
                (header[3] & 0xFF) == 0x47 &&
                (header[4] & 0xFF) == 0x0D &&
                (header[5] & 0xFF) == 0x0A &&
                (header[6] & 0xFF) == 0x1A &&
                (header[7] & 0xFF) == 0x0A) {
                return ".png";
            }

            throw new InvalidFileException("Only JPG and PNG images are allowed (verified by file signature)");
            
        } catch (IOException e) {
            throw new FileStorageException("Failed to read file signature", e);
        }
    }
}
