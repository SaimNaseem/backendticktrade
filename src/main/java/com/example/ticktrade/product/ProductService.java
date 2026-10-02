package com.example.ticktrade.product;

import com.example.ticktrade.exception.ResourceNotFound;
import com.example.ticktrade.s3.ProductImage;
import com.example.ticktrade.s3.S3StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final S3StorageService s3StorageService;
    private final String publicBaseUrl;

    public ProductService(
            ProductRepository productRepository,
            S3StorageService s3StorageService,
            @Value("${app.public-base-url:}") String publicBaseUrl
    ) {
        this.productRepository = productRepository;
        this.s3StorageService = s3StorageService;
        this.publicBaseUrl = publicBaseUrl == null ? "" : publicBaseUrl.replaceAll("/+$", "");
    }

    public ProductResponse getProductById(UUID id) {
        return productRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFound(
                        "product [" + id + "] not found"
                ));
    }

    public void deleteProductByIdSer(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("product [" + id + "] not found"));
        s3StorageService.delete(product.getImageUrl());
        productRepository.deleteById(id);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UUID saveNewProduct(NewProductRequest product) {
        Product newProduct = new Product(
                product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.imageUrl(),
                product.stockLevel(),
                product.isPublished()
        );
        Product savedProduct = productRepository.save(newProduct);
        return savedProduct.getId();
    }

    @Transactional
    public void updateProduct(UUID id, NewProductRequest newProductRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(
                        "Product with [" + id + " ] not found"
                ));
        if (newProductRequest.name() != null && !newProductRequest.name().equals(product.getName())) {
            product.setName(newProductRequest.name());
        }

        if (newProductRequest.description() != null && !newProductRequest.description().equals(product.getDescription())) {
            product.setDescription(newProductRequest.description());
        }

        if (newProductRequest.price() != null && !newProductRequest.price().equals(product.getPrice())) {
            product.setPrice(newProductRequest.price());
        }

        if (newProductRequest.imageUrl() != null && !newProductRequest.imageUrl().equals(product.getImageUrl())) {
            product.setImageUrl(newProductRequest.imageUrl());
        }

        if (newProductRequest.stockLevel() != null && !newProductRequest.stockLevel().equals(product.getStockLevel())) {
            product.setStockLevel(newProductRequest.stockLevel());
        }

        productRepository.save(product);
    }

    @Transactional
    public void updatePublishedStatus(UUID id, boolean isPublished) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(
                        "Product with [" + id + "] not found"
                ));

        product.setPublished(isPublished);
    }

    @Transactional
    public void uploadProductImage(UUID id, MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("product [" + id + "] not found"));

        String previousKey = product.getImageUrl();
        String key = s3StorageService.upload(id, file);
        product.setImageUrl(key);
        productRepository.save(product);

        if (previousKey != null && !previousKey.equals(key)) {
            s3StorageService.delete(previousKey);
        }
    }

    public ProductImage getProductImage(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("product [" + id + "] not found"));
        if (product.getImageUrl() == null || product.getImageUrl().isBlank()) {
            throw new ResourceNotFound("product [" + id + "] has no image");
        }
        if (S3StorageService.isExternalUrl(product.getImageUrl())) {
            throw new ResourceNotFound("product [" + id + "] image is not stored in S3");
        }
        return s3StorageService.download(product.getImageUrl());
    }

    @Transactional
    public void deleteProductImage(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("product [" + id + "] not found"));
        s3StorageService.delete(product.getImageUrl());
        product.setImageUrl(null);
        productRepository.save(product);
    }

    private ProductResponse mapToResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                toPublicImageUrl(p),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getDeletedAt(),
                p.getPublished()
        );
    }

    private String toPublicImageUrl(Product product) {
        String stored = product.getImageUrl();
        if (stored == null || stored.isBlank()) {
            return null;
        }
        if (S3StorageService.isExternalUrl(stored)) {
            return stored;
        }
        return publicBaseUrl + "/api/v1/products/" + product.getId() + "/image";
    }
}
