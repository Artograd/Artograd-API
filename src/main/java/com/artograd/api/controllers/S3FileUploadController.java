package com.artograd.api.controllers;

import com.artograd.api.config.AwsProperties;
import com.artograd.api.model.FileInfo;
import com.artograd.api.model.system.UserTokenClaims;
import com.artograd.api.services.IUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
public class S3FileUploadController {

  @Autowired AwsProperties awsProperties;

  @Autowired private IUserService userService;

  private final S3Client s3Client =
      S3Client.builder().credentialsProvider(DefaultCredentialsProvider.create()).build();

  /**
   * Uploads a file to the specified folder in the server.
   *
   * @param tenderFolder The name of the tender folder where the file should be uploaded.
   * @param subFolder The name of the sub-folder within the tender folder where the file should be
   *     uploaded.
   * @param file The file to be uploaded.
   * @param request The HTTP servlet request object.
   * @return A ResponseEntity representing the HTTP response. If the file upload is successful, it
   *     returns the file info in the response body. If the file is empty, it returns a bad request
   *     status with the corresponding error message. If the user is not authorized or there is an
   *     error processing the file, it returns a forbidden or internal server error status with the
   *     corresponding error message.
   */
  @PostMapping("/uploadFile/{tenderFolder}/{subFolder}")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<?> uploadFile(
      @PathVariable String tenderFolder,
      @PathVariable String subFolder,
      @RequestParam("file") MultipartFile file,
      HttpServletRequest request) {

    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("File is empty");
    }

    Optional<UserTokenClaims> claims = userService.getUserTokenClaims(request);
    if (!claims.isPresent() || claims.get().getUsername() == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
    }

    try {
      FileInfo fileInfo = processFileUpload(file, tenderFolder, subFolder);
      return ResponseEntity.ok(fileInfo);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to process the file");
    }
  }

  private FileInfo processFileUpload(MultipartFile file, String tenderFolder, String subFolder)
      throws IOException {
    String originalFilename = file.getOriginalFilename();
    String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
    String fileName = UUID.randomUUID().toString().replace("-", "");
    String uniqueFileName =
        String.format("%s/%s/%s.%s", tenderFolder, subFolder, fileName, extension);
    String fileType = determineFileType(extension);

    uploadToS3(file.getBytes(), uniqueFileName);

    String fileUrl = awsProperties.getCloudfront().getDistributionDomain() + "/" + uniqueFileName;
    String snapPath =
        fileType.equals("image")
            ? createAndUploadImageSnap(file, tenderFolder, subFolder, fileName, extension)
            : fileUrl;

    return new FileInfo(
        fileUrl, snapPath, originalFilename, file.getSize(), 0, fileType, extension);
  }

  private String createAndUploadImageSnap(
      MultipartFile file, String tenderFolder, String subFolder, String fileName, String extension)
      throws IOException {
    // In this try-with-resources the InputStream and ByteArrayOutputStream will be automatically
    // closed
    try (InputStream fileInputStream = file.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

      BufferedImage thumbnail = Thumbnails.of(fileInputStream).size(286, 336).asBufferedImage();
      ImageIO.write(thumbnail, extension, baos);
      byte[] snapBytes = baos.toByteArray();

      String snapFileName =
          String.format("%s/%s/snaps/%s.%s", tenderFolder, subFolder, fileName, extension);
      uploadToS3(snapBytes, snapFileName);

      return awsProperties.getCloudfront().getDistributionDomain() + "/" + snapFileName;
    }
  }

  private void uploadToS3(byte[] content, String key) {
    s3Client.putObject(
        PutObjectRequest.builder().bucket(awsProperties.getS3().getBucketName()).key(key).build(),
        RequestBody.fromBytes(content));
  }

  private String determineFileType(String extension) {
    if ("pdf".equals(extension)) {
      return "iframe";
    } else if (extension.matches("svg|png|jpg|heic|avif")) {
      return "image";
    }
    return "attachment";
  }
}
