package com.company.filesharingmanager.controller;

import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.company.filesharingmanager.model.FileInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/files")
@CrossOrigin("http://localhost:3000/")
public class FileController {

    private final Path fileStorageLocation = Paths.get("uploads");
    private final Map<String, FileInfo> filesMap = new HashMap<>();

    public FileController() {
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Path targetLocation = fileStorageLocation.resolve(file.getOriginalFilename());
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            FileInfo fileInfo = new FileInfo(file.getOriginalFilename(), currentTime);
            filesMap.put(fileInfo.getDownloadLink(), fileInfo);

            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles() {
        return ResponseEntity.ok(new ArrayList<>(filesMap.values()));
    }

    @GetMapping("/download/{uuid}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String uuid) {
        FileInfo fileInfo = filesMap.get(uuid);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path filePath = fileStorageLocation.resolve(fileInfo.getFileName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                                     .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<String> deleteFile(@PathVariable String uuid) {
        FileInfo fileInfo = filesMap.remove(uuid);
        if (fileInfo == null) {
            return ((BodyBuilder) ResponseEntity.notFound()).body("File not found");
        }
        try {
            Path filePath = fileStorageLocation.resolve(fileInfo.getFileName()).normalize();
            Files.deleteIfExists(filePath);
            return ResponseEntity.ok("File deleted successfully");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file");
        }
    }
}
