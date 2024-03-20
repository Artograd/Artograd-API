package com.artograd.api.controllers;

import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.CognitoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
public class S3FileUploadController {
	
	@Value("${aws.s3.bucket-name}")
    private String bucketName;
	
	@Value("${aws.cloudfront.distribution-domain}")
    private String cloudFrontDomainName;
	
	@Autowired
    private CognitoService cognitoService;
	
	private final S3Client s3Client = S3Client.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
	
    @PostMapping("/uploadFile/{tenderFolder}/{subFolder}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<FileInfo> uploadFile(
    		@PathVariable String tenderFolder, 
    		@PathVariable String subFolder, 
    		@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        UserTokenClaims claims = cognitoService.getUserTokenClaims(request);
    	if ( StringUtils.isBlank( claims.getUsername() )) {//operation is allowed only to authorized users
    		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    	}

        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
        String fileName = UUID.randomUUID().toString();
        String uniqueFileName = tenderFolder + "/" + subFolder + "/" + fileName + "." + extension;
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
        
        String snapPath = null;
        if (determineFileType(extension).equals("image")) {
            // Define the path for the resized image (snap)
        	String snapFileName  = tenderFolder + "/" + subFolder  + "/snaps/" + fileName + "." + extension;
			try {
				BufferedImage thumbnail = Thumbnails.of(file.getInputStream())
				         .size(286, 336) 
				         .asBufferedImage();
			
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(thumbnail, extension, baos);
			
	            byte[] snapBytes = baos.toByteArray();
	
	            s3Client.putObject(PutObjectRequest.builder()
	                    .bucket(bucketName)
	                    .key(snapFileName)
	                    .build(),
	                    RequestBody.fromBytes(snapBytes));
	
	            snapPath = cloudFrontDomainName + "/" + snapFileName;
            
			} catch (IOException e) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
        } else {
        	snapPath = fileUrl;
        }

        FileInfo fileInfo = new FileInfo(fileUrl, snapPath, originalFilename, file.getSize(), 0, fileType, extension);
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
        public String snapPath;
        public String name;
        public long size;
        public int id;
        public String type;
        public String extension;

        public FileInfo(String path, String snapPath, String name, long size, int id, String type, String extension) {
            this.path = path;
            this.snapPath = snapPath;
            this.name = name;
            this.size = size;
            this.id = id;
            this.type = type;
            this.extension = extension;
        }
    }
}
