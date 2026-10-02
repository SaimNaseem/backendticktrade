package com.example.ticktrade.s3;

import com.example.ticktrade.config.S3Properties;
import com.example.ticktrade.exception.InvalidImageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @Mock
    private S3Client s3Client;

    private S3StorageService s3StorageService;

    @BeforeEach
    void setUp() {
        s3StorageService = new S3StorageService(
                s3Client,
                new S3Properties("ticktrade-product-images", "eu-central-1", "")
        );
    }

    @Test
    void uploadStoresImageUnderProductKey() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "headphones.png",
                "image/png",
                new byte[]{1, 2, 3, 4}
        );
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        UUID productId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        String key = s3StorageService.upload(productId, file);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));
        PutObjectRequest request = captor.getValue();

        assertTrue(key.startsWith("products/" + productId + "/"));
        assertTrue(key.endsWith(".png"));
        assertTrue("ticktrade-product-images".equals(request.bucket()));
        assertTrue("image/png".equals(request.contentType()));
        assertTrue("headphones.png".equals(request.metadata().get("original-filename")));
    }

    @Test
    void uploadRejectsNonImageFiles() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "notes.txt",
                "text/plain",
                "hello".getBytes()
        );

        assertThrows(InvalidImageException.class, () -> s3StorageService.upload(UUID.randomUUID(), file));
    }
}
