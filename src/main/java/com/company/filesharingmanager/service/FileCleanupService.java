package com.company.filesharingmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Service
public class FileCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(FileCleanupService.class);
    private static final String UPLOAD_DIR = "uploads"; // Ensure it matches your upload path

    @Scheduled(fixedRate = 3600000) // Refresh page after every hour (3600000 ms = 1 hour)
    public void cleanUpOldFiles() {
        try (Stream<Path> files = Files.list(Paths.get(UPLOAD_DIR))) {
            Instant cutoffTime = Instant.now().minus(24, ChronoUnit.HOURS); //files auto delete after 24 hours
            files.filter(Files::isRegularFile)
                 .filter(path -> isOlderThan(path, cutoffTime))
                 .forEach(this::deleteFile);
        } catch (IOException e) {
            logger.error("Error cleaning up files", e);
        }
    }

    private boolean isOlderThan(Path file, Instant cutoffTime) {
        try {
            FileTime fileTime = Files.getLastModifiedTime(file);
            return fileTime.toInstant().isBefore(cutoffTime);
        } catch (IOException e) {
            logger.error("Could not get file time for: " + file, e);
        }
        return false;
    }

    private void deleteFile(Path file) {
        try {
            Files.delete(file);
            logger.info("Deleted old file: " + file.getFileName());
        } catch (IOException e) {
            logger.error("Failed to delete file: " + file, e);
        }
    }
}
