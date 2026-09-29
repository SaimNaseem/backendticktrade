package com.example.ticktrade.product;

import com.example.ticktrade.TickTradeApplication;
import com.example.ticktrade.exception.ResourceNotFound;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductService{
    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;

    }

    public ProductResponse getProductById(UUID id){
        return productRepository.findById(id)
                .map(mapToResponse())
                .orElseThrow( () -> new ResourceNotFound(
                        "product [" + id +  "] not found"
                ));
    }



    /*DELETE ENDPOINT*/
    public void deleteProductByIdSer(UUID id){
        boolean exists = productRepository.existsById(id);
        if(!exists) throw new ResourceNotFound("product [" + id +  "] not found");
        {
            productRepository.deleteById(id);
        }

    }
    /*ADD PRODUCT ENDPOINT*/
    /*TODO*/


    public List<ProductResponse> getAllProducts(){
        return productRepository.findAll().stream()
                .map(mapToResponse())
                .collect(Collectors.toList());
    }

    private static @NonNull Function<Product, ProductResponse> mapToResponse() {
        return p -> new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getDeletedAt()
        );
    }

    public UUID saveNewProduct(NewProductRequest product) {

        Product newProduct = new Product(
                product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.imageUrl(),
                product.stockLevel()
                );
//        p.setName(product.name());
//        p.setDescription(product.description());
//        p.setPrice(product.price());
//        p.setStockLevel(product.stockLevel());
//        p.setImageUrl(product.imageUrl());

        Product savedProduct = productRepository.save(newProduct);
        return savedProduct.getId();
    }

    @Transactional
    public void updateProduct(UUID id,NewProductRequest newProductRequest){
        Product product = productRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFound (
                        "Product with [" + id + " ] not found"
                ));
        if(newProductRequest.name()!=null && !newProductRequest.name().equals(product.getName())){
            product.setName(newProductRequest.name());
        }

        if(newProductRequest.description()!=null && !newProductRequest.description().equals(product.getDescription())){
            product.setDescription(newProductRequest.description());
        }

        if(newProductRequest.price()!=null && !newProductRequest.price().equals(product.getPrice())){
            product.setPrice(newProductRequest.price());
        }

        if(newProductRequest.imageUrl()!=null && !newProductRequest.imageUrl().equals(product.getImageUrl())){
            product.setImageUrl(newProductRequest.imageUrl());
        }

        if(newProductRequest.stockLevel()!=null && !newProductRequest.stockLevel().equals(product.getStockLevel())){
            product.setStockLevel(newProductRequest.stockLevel());
        }

        productRepository.save(product);

    }

}
