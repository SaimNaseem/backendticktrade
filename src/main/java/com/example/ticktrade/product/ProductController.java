package com.example.ticktrade.product;

import com.example.ticktrade.s3.ProductImage;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;


    public ProductController(ProductService productService) {

        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts(){

        return productService.getAllProducts();
    }

    @GetMapping("{id}")
    public ProductResponse getProductById(@PathVariable("id") UUID id){
        return productService.getProductById(id);
    }

    @DeleteMapping("{id}")
    public void deleteProductbyId(@PathVariable("id") UUID id){
         productService.deleteProductByIdSer(id);

    }

    /*Insert Product*/
    @PostMapping
    public UUID saveProduct(@RequestBody @Valid NewProductRequest product){
        return productService.saveNewProduct(product);
    }

    @PutMapping("/{id}")
    public void updateProduct(@PathVariable UUID id,
            @RequestBody NewProductRequest request){
         productService.updateProduct(id,request);
    }
    @PatchMapping("/{id}/isPublished")
    public void updatePublishedStatus(
            @PathVariable UUID id,
            @RequestBody PublishedRequest request
    ) {
        productService.updatePublishedStatus(id, request.isPublished());
    }

    @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadProductImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) {
        productService.uploadProductImage(id, file);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<InputStreamResource> getProductImage(@PathVariable UUID id) {
        return imageResponse(productService.getProductImage(id), false);
    }

    @GetMapping("/{id}/image/download")
    public ResponseEntity<InputStreamResource> downloadProductImage(@PathVariable UUID id) {
        return imageResponse(productService.getProductImage(id), true);
    }

    @DeleteMapping("/{id}/image")
    public void deleteProductImage(@PathVariable UUID id) {
        productService.deleteProductImage(id);
    }

    private ResponseEntity<InputStreamResource> imageResponse(ProductImage image, boolean attachment) {
        ContentDisposition disposition = ContentDisposition.builder(attachment ? "attachment" : "inline")
                .filename(image.filename(), StandardCharsets.UTF_8)
                .build();

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString());

        if (image.contentLength() >= 0) {
            builder.contentLength(image.contentLength());
        }

        return builder.body(new InputStreamResource(image.content()));
    }
}
