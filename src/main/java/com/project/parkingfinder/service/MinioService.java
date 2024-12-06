package com.project.parkingfinder.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;


    private String bucketName = "doclandpublic";
    @Value("${minio.endpoint}")
    private String endPoint;
    /**
     * Upload file to MinIO
     * @param file MultipartFile to upload
     * @param directory Directory in bucket
     * @return Uploaded file path
     */
    public String uploadFile(MultipartFile file, String directory) {
        try {
            // Validate input
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String uniqueFileName =  UUID.randomUUID().toString() + fileExtension;

            log.info("Uploaded file: " + uniqueFileName,bucketName);
            // Upload to MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return getFileUrl(endPoint,bucketName,uniqueFileName);

        } catch (Exception e) {
            log.error("Error uploading file to MinIO", e);
            throw new RuntimeException("File upload failed", e);
        }
    }
    public String getFileUrl(String endpoint, String bucketName, String uniqueFileName) {

        return endpoint +"/"+ bucketName + "/" + uniqueFileName;
    }

    /**
     * Upload byte array to MinIO
     * @param bytes Byte array to upload
     * @param directory Directory in bucket
     * @param fileExtension File extension
     * @return Uploaded file path
     */
    public String uploadFile(byte[] bytes, String directory, String fileExtension) {
        try {
            // Generate unique filename
            String uniqueFileName = directory + "/" +
                    UUID.randomUUID().toString() +
                    (fileExtension.startsWith(".") ? fileExtension : "." + fileExtension);

            // Upload to MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                            .build()
            );

            return uniqueFileName;
        } catch (Exception e) {
            log.error("Error uploading byte array to MinIO", e);
            throw new RuntimeException("File upload failed", e);
        }
    }

    /**
     * Generate a temporary URL for file download
     * @param objectName Object path in MinIO
     * @param expiryMinutes URL expiry time in minutes
     * @return Temporary download URL
     */
    public String getPreSignedUrl(String objectName, int expiryMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiryMinutes, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error generating pre-signed URL", e);
            throw new RuntimeException("Failed to generate download URL", e);
        }
    }

    /**
     * Delete file from MinIO
     * @param objectName Object path to delete
     */
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error deleting file from MinIO", e);
            throw new RuntimeException("File deletion failed", e);
        }
    }

    /**
     * Check if file exists in MinIO
     * @param objectName Object path to check
     * @return true if file exists, false otherwise
     */
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.response().code() == 404) {
                return false;
            }
            throw new RuntimeException("Error checking file existence", e);
        } catch (Exception e) {
            log.error("Error checking file existence", e);
            throw new RuntimeException("File existence check failed", e);
        }
    }
}