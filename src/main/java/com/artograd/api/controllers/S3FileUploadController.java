package com.artograd.api.controllers;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@RestController
public class S3FileUploadController {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.cloudfront.distribution-domain}")
    private String cloudFrontDomainName;

    private final S3Client s3Client = S3Client.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();

    @PostMapping("/uploadFile/{tenderFolder}/{subFolder}")
    public ResponseEntity<FileInfo> uploadFile(
            @PathVariable String tenderFolder,
            @PathVariable String subFolder,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
        String uniqueFileName = tenderFolder + "/" + subFolder + "/" + UUID.randomUUID().toString() + "." + extension;
        String fileType = determineFileType(extension);

        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(uniqueFileName)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Construct the CloudFront URL for the file
        String fileUrl = cloudFrontDomainName + "/" + uniqueFileName;

        FileInfo fileInfo = new FileInfo(fileUrl, originalFilename, file.getSize(), 0, fileType, extension);
        return new ResponseEntity<>(fileInfo, HttpStatus.CREATED);
    }

    private String determineFileType(String extension) {
        if ("pdf".equals(extension)) {
            return "iframe";
        } else if (extension.matches("svg|png|jpg|heic|avif")) {
            return "image";
        }
        return "attachment";
    }

    // Inner class to encapsulate file info response
    static class FileInfo {
        public String path;
        public String name;
        public long size;
        public int id;
        public String type;
        public String extension;

        public FileInfo(String path, String name, long size, int id, String type, String extension) {
            this.path = path;
            this.name = name;
            this.size = size;
            this.id = id;
            this.type = type;
            this.extension = extension;
        }
    }
}
