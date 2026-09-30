package com.example.ticktrade.product;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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





}
