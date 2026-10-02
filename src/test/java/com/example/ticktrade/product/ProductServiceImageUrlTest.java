package com.example.ticktrade.product;

import com.example.ticktrade.s3.S3StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImageUrlTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private S3StorageService s3StorageService;

    @Test
    void productResponseExposesDownloadableImagePath() {
        ProductService productService = new ProductService(
                productRepository,
                s3StorageService,
                "http://localhost:8083"
        );
        UUID id = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        Product product = new Product(
                id,
                "Headphones",
                "Wireless headphones",
                new BigDecimal("99.99"),
                "products/" + id + "/photo.png",
                10,
                true
        );
        product.setId(id);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(id);

        assertEquals("http://localhost:8083/api/v1/products/" + id + "/image", response.imageUrl());
    }
}
