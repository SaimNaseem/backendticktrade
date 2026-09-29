package com.example.ticktrade;

import com.example.ticktrade.product.Product;
import com.example.ticktrade.product.ProductRepository;
import jakarta.persistence.PreUpdate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootApplication
public class TickTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TickTradeApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner
            (ProductRepository productRepository){
              return args ->{
                  Product product1 = new Product();
                  product1.setName("Macbook Pro");
                  product1.setDescription("Macbook Pro M5");
                  product1.setPrice(new BigDecimal(3000));
                  product1.setStockLevel(100);
                  productRepository.save(product1);


                  Product product2 = new Product();
                  product2.setId(UUID.fromString(
                          "41f37edb-72af-49dc-9f9c-3d220e489174"
                  ));
                  product2.setName("Mouse");
                  product2.setDescription("LG Mouse");
                  product2.setPrice(new BigDecimal(78));
                  product2.setStockLevel(1000);
                  productRepository.save(product2);
        };

    }

}
