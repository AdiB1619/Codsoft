package com.codsoft.sms.util;

import com.codsoft.sms.exception.FileStorageException;
import com.codsoft.sms.exception.InvalidFileException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FileStorageUtil Tests")
class FileStorageUtilTest {

    private FileStorageUtil fileStorageUtil;
    private final String testUploadDir = "test_uploads";
    private final long maxSize = 2 * 1024 * 1024; // 2MB

    // Valid JPG magic bytes: FF D8 FF
    private final byte[] validJpgBytes = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00, 0x00};
    
    // Valid PNG magic bytes: 89 50 4E 47 0D 0A 1A 0A
    private final byte[] validPngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00};

    // Invalid magic bytes (e.g., text file)
    private final byte[] invalidBytes = "Hello World".getBytes();

    @BeforeEach
    void setUp() {
        fileStorageUtil = new FileStorageUtil(testUploadDir, maxSize);
    }

    @AfterEach
    void tearDown() throws IOException {
        Path dir = Paths.get(testUploadDir);
        if (Files.exists(dir)) {
            try (Stream<Path> walk = Files.walk(dir)) {
                walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
            }
        }
    }

    @Test
    @DisplayName("Successfully stores a valid JPG file")
    void storeFile_validJpg_success() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", validJpgBytes);
        
        String url = fileStorageUtil.storeFile(file);
        
        assertThat(url).isNotBlank();
        assertThat(url).startsWith("/" + testUploadDir + "/");
        assertThat(url).endsWith(".jpg");
        
        Path storedFile = Paths.get(testUploadDir, url.substring(testUploadDir.length() + 2));
        assertThat(Files.exists(storedFile)).isTrue();
    }

    @Test
    @DisplayName("Successfully stores a valid PNG file")
    void storeFile_validPng_success() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", validPngBytes);
        
        String url = fileStorageUtil.storeFile(file);
        
        assertThat(url).isNotBlank();
        assertThat(url).startsWith("/" + testUploadDir + "/");
        assertThat(url).endsWith(".png");
    }

    @Test
    @DisplayName("Rejects file with invalid magic bytes (wrong type)")
    void storeFile_invalidMagicBytes_throwsInvalidFileException() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", invalidBytes);
        
        assertThatThrownBy(() -> fileStorageUtil.storeFile(file))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("Only JPG and PNG images are allowed");
    }

    @Test
    @DisplayName("Rejects empty file")
    void storeFile_emptyFile_throwsInvalidFileException() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        
        assertThatThrownBy(() -> fileStorageUtil.storeFile(file))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("File is empty or missing");
    }

    @Test
    @DisplayName("Rejects oversized file")
    void storeFile_oversizedFile_throwsInvalidFileException() {
        // Create an oversized util
        FileStorageUtil smallLimitUtil = new FileStorageUtil(testUploadDir, 2); // 2 bytes max
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", validJpgBytes);
        
        assertThatThrownBy(() -> smallLimitUtil.storeFile(file))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("File exceeds maximum allowed size");
    }

    @Test
    @DisplayName("Deletes file successfully")
    void deleteFile_success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", validJpgBytes);
        String url = fileStorageUtil.storeFile(file);
        
        Path storedFile = Paths.get(testUploadDir, url.substring(testUploadDir.length() + 2));
        assertThat(Files.exists(storedFile)).isTrue();
        
        fileStorageUtil.deleteFile(url);
        
        assertThat(Files.exists(storedFile)).isFalse();
    }
}
