package com.example.ticktrade.s3;

import com.example.ticktrade.config.S3Properties;
import com.example.ticktrade.exception.InvalidImageException;
import com.example.ticktrade.exception.ResourceNotFound;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class S3StorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private final S3Client s3Client;
    private final S3Properties properties;

    public S3StorageService(S3Client s3Client, S3Properties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    public String upload(UUID productId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("Image file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidImageException("Only JPEG, PNG, GIF and WebP images are allowed");
        }

        String key = "products/" + productId + "/" + UUID.randomUUID() + extensionFor(contentType);
        String originalFilename = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(properties.bucket())
                            .key(key)
                            .contentType(contentType)
                            .metadata(Map.of("original-filename", originalFilename))
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException ex) {
            throw new InvalidImageException("Could not read the uploaded image");
        }

        return key;
    }

    public ProductImage download(String key) {
        if (key == null || key.isBlank()) {
            throw new ResourceNotFound("product image not found");
        }

        try {
            ResponseInputStream<GetObjectResponse> object = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(properties.bucket())
                            .key(key)
                            .build()
            );
            GetObjectResponse response = object.response();
            String filename = response.metadata().getOrDefault("original-filename", key.substring(key.lastIndexOf('/') + 1));
            String contentType = response.contentType() == null ? "application/octet-stream" : response.contentType();
            long contentLength = response.contentLength() == null ? -1 : response.contentLength();
            return new ProductImage(object, contentType, filename, contentLength);
        } catch (NoSuchKeyException ex) {
            throw new ResourceNotFound("product image not found");
        }
    }

    public void delete(String key) {
        if (key == null || key.isBlank() || isExternalUrl(key)) {
            return;
        }
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(properties.bucket())
                        .key(key)
                        .build()
        );
    }

    public static boolean isExternalUrl(String value) {
        return value != null && (value.startsWith("http://") || value.startsWith("https://"));
    }

    private static String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}
