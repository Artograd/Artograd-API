package com.artograd.api.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;

@RestController
public class S3FileUploadController {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @GetMapping("/generate-presigned-url")
    public ResponseEntity<URL> generatePreSignedURL(@RequestParam String tenderId, @RequestParam String proposalId, @RequestParam String fileName) {
        String key = String.format("%s/%s/%s", tenderId, proposalId, fileName);
        S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(r -> r.signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(por -> por.bucket(bucketName).key(key)));

        return ResponseEntity.ok(presignedRequest.url());
    }

}
