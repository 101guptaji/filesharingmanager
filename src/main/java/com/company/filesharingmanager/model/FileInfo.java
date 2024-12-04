package com.company.filesharingmanager.model;

import java.util.UUID;

public class FileInfo {
    private String fileName;
    private String uploadTime;
    private String downloadLink;

    public FileInfo(String fileName, String uploadTime) {
        this.fileName = fileName;
        this.uploadTime = uploadTime;
        this.downloadLink = UUID.randomUUID().toString(); // Generate unique link
    }

    public String getFileName() {
        return fileName;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public String getDownloadLink() {
        return downloadLink;
    }
}


