package com.farwin.price_intelligence_api.config;

import com.farwin.price_intelligence_api.model.Product;
import com.farwin.price_intelligence_api.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class DataSeeder {
    @Bean
    @Order(1)
    public CommandLineRunner seedData(ProductRepository productRepository) {        
        return args -> {            
        if (productRepository.count() == 0) {                
            productRepository.save(new Product("Rice", "Food", 150.00));                
            productRepository.save(new Product("Sugar", "Food", 200.00));                
            productRepository.save(new Product("Coconut Oil", "Food", 650.00));                
            productRepository.save(new Product("Dhal", "Food", 320.00));                
            productRepository.save(new Product("Wheat Flour", "Food", 180.00));                
            productRepository.save(new Product("Palm Oil", "Food", 420.00));                
            productRepository.save(new Product("Corn", "Grain", 95.00));                
            productRepository.save(new Product("Coffee", "Beverage", 1800.00));                
            System.out.println(">>> Database seeded with initial products");            
        } else {                
            System.out.println(">>> Database already has data — skipping seed");            
        }        };    }
}
