package com.farwin.price_intelligence_api.service;

import com.farwin.price_intelligence_api.model.PriceHistory;
import com.farwin.price_intelligence_api.model.PriceStatistics;
import com.farwin.price_intelligence_api.model.Product;
import com.farwin.price_intelligence_api.repository.PriceHistoryRepository;
import com.farwin.price_intelligence_api.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AnalyticsService {    
    private final ProductRepository productRepository;    
    private final PriceHistoryRepository priceHistoryRepository;    
    public AnalyticsService(ProductRepository productRepository, PriceHistoryRepository priceHistoryRepository) 
    {        
        this.productRepository = productRepository;        
        this.priceHistoryRepository = priceHistoryRepository;    
    }    
    
    public Optional<PriceStatistics> getStatistics(Long productId) {        
        Optional<Product> productOpt = productRepository.findById(productId);        
        if (productOpt.isEmpty()) return Optional.empty();        
        
        Product product = productOpt.get();        
        List<PriceHistory> history =
    priceHistoryRepository.findByProductOrderByRecordedAtDesc(product);        
            
        PriceStatistics stats = new PriceStatistics();        
        stats.setProductId(productId);        
        stats.setProductName(product.getName());        
        stats.setCurrentPrice(product.getCurrentPrice());        
        stats.setTotalChanges(history.size());        
        
        if (history.isEmpty()) {            
            stats.setAveragePrice(product.getCurrentPrice());            
            stats.setMinPrice(product.getCurrentPrice());            
            stats.setMaxPrice(product.getCurrentPrice());            
            stats.setStandardDeviation(0.0);            
            stats.setTrendDirection("STABLE");            
            stats.setLargestChangePercentage(0.0);            
            stats.setAnomalyCount(0);            
            return Optional.of(stats);        
        }        
        
        // Collect all new prices from history        
        List<Double> prices = history.stream().map(PriceHistory::getNewPrice).collect(java.util.stream.Collectors.toList());        
        
        // Average        
        double average = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);        
        stats.setAveragePrice(Math.round(average * 100.0) / 100.0);        
        
        // Min and Max        
        stats.setMinPrice(prices.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));        
        stats.setMaxPrice(prices.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));        
        
        // Standard Deviation        
        // // Step 1: find the difference of each price from the average, squared        
        // // Step 2: find the average of those squared differences        
        // // Step 3: square root of that average        
        double variance = prices.stream().mapToDouble(p -> Math.pow(p - average, 2)).average().orElse(0.0);        
        double stdDev = Math.sqrt(variance);        
        stats.setStandardDeviation(Math.round(stdDev * 100.0) / 100.0);        
        
        // Trend direction — compare first 3 recent prices to last 3        
        if (history.size() >= 6) {            
            double recentAvg = history.subList(0, 3).stream().mapToDouble(h -> h.getNewPrice()).average().orElse(0.0);            
            double olderAvg = history.subList(history.size() - 3, history.size()).stream().mapToDouble(h -> h.getNewPrice()).average().orElse(0.0);            
            if (recentAvg > olderAvg * 1.02) stats.setTrendDirection("UP");            
            else if (recentAvg < olderAvg * 0.98) stats.setTrendDirection("DOWN");            
            else stats.setTrendDirection("STABLE");        
        } else {            
            stats.setTrendDirection("INSUFFICIENT_DATA");        
        }        
        
        // Largest single change percentage        
        double largestChange = history.stream().mapToDouble(h -> Math.abs(h.getChangePercentage())).max().orElse(0.0);        
        stats.setLargestChangePercentage(Math.round(largestChange * 100.0) / 100.0);        
        
        // Count anomalies        
        long anomalyCount = history.stream().filter(h -> Boolean.TRUE.equals(h.getIsAnomaly())).count();        
        stats.setAnomalyCount((int) anomalyCount);        
        return Optional.of(stats);    
    }    
    
    // Get statistics for all products    
    public List<PriceStatistics> getAllStatistics() {        
        return productRepository.findAll().stream().map(p -> getStatistics(p.getId())).filter(Optional::isPresent).map(Optional::get).collect(java.util.stream.Collectors.toList());    
    }
}