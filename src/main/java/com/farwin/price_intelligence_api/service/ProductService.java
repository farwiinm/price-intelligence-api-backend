package com.farwin.price_intelligence_api.service;

import com.farwin.price_intelligence_api.model.PriceHistory;
import com.farwin.price_intelligence_api.model.Product;
import com.farwin.price_intelligence_api.repository.PriceHistoryRepository;
import com.farwin.price_intelligence_api.repository.ProductRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

//annotator telling spring to manage this as a bean and inject it where needed
@Service
public class ProductService{

    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final AnomalyDetectionService anomalyDetectionService;

public ProductService(ProductRepository productRepository,
                      PriceHistoryRepository priceHistoryRepository,
                      AnomalyDetectionService anomalyDetectionService) {
    this.productRepository = productRepository;
    this.priceHistoryRepository = priceHistoryRepository;
    this.anomalyDetectionService = anomalyDetectionService;
}

    //methods
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(String category) {
    return productRepository.findByCategory(category);
}

public List<Product> searchByName(String name) {
    return productRepository.findByNameContainingIgnoreCase(name);
}

public List<Product> getProductsByPriceRange(Double min, Double max){
    return productRepository.findByPriceRange(min, max);
}
public Product createProduct(Product product) {
    return productRepository.save(product);
}

@Transactional
public Optional<Product> updateProduct(Long id, Product updated) {
    return productRepository.findById(id).map(product -> {
        product.setName(updated.getName());
        product.setCategory(updated.getCategory());
        product.setCurrentPrice(updated.getCurrentPrice());
        return productRepository.save(product);
    });
}

@Transactional
public Optional<Product> updatePrice(Long id, Double newPrice) {
    return productRepository.findById(id).map(product -> {
        Double oldPrice = product.getCurrentPrice();

        // Run anomaly check before saving
        boolean anomaly = anomalyDetectionService.isAnomaly(id, oldPrice, newPrice);

        // Save history with anomaly flag set
        PriceHistory history = new PriceHistory(product, oldPrice, newPrice);
        history.setIsAnomaly(anomaly);
        priceHistoryRepository.save(history);

        if (anomaly) {
            System.out.println(">>> ANOMALY DETECTED for " + product.getName()
                + ": " + oldPrice + " -> " + newPrice);
        }

        product.setCurrentPrice(newPrice);
        return productRepository.save(product);
    });
}

public boolean deleteProduct(Long id) {
    if (productRepository.existsById(id)){
        productRepository.deleteById(id);
        return true;
    }
    return false;
}

public List<PriceHistory> getPriceHistory(Long productId){
    return priceHistoryRepository.findByProductIdOrderByRecordedAtDesc(productId);
}

}