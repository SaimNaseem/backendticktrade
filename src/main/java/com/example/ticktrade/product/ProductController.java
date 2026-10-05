package com.example.ticktrade.product;

import com.example.ticktrade.product.ai.AiProductRequest;
import com.example.ticktrade.product.ai.AiProductResponse;
import com.example.ticktrade.product.ai.GeminiService;

import com.example.ticktrade.cloudinary.CloudinaryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;
    private final GeminiService gemeniService;


    public ProductController(ProductService productService,
                             CloudinaryService cloudinaryService,
                                GeminiService  gemeniService) {

        this.productService = productService;
        this.cloudinaryService = cloudinaryService;
        this.gemeniService=gemeniService;
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

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        String imageUrl = cloudinaryService.uploadImage(file);

        return ResponseEntity.ok(
                Map.of("url", imageUrl)
        );
    }
    @PostMapping("/ai/product")
    public AiProductResponse generateProductWithAi(
            @RequestBody AiProductRequest request
    ) {
        return gemeniService.generateProduct(
                request.imageUrl()
        );
    }






}
