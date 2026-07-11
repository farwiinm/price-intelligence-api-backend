package com.farwin.price_intelligence_api.model;

public class PriceStatistics {
        private Long productId;    
        private String productName;    
        private Double currentPrice;    
        private Double averagePrice;    
        private Double minPrice;    
        private Double maxPrice;    
        private Double standardDeviation;    
        private String trendDirection;   
        private Integer totalChanges;    
        private Double largestChangePercentage;    
        private Integer anomalyCount;    
        
        public PriceStatistics() {}    
        
        // Getters and setters for all fields    
        public Long getProductId() { return productId; }    
        public void setProductId(Long productId) { this.productId = productId; }    
        public String getProductName() { return productName; }    
        public void setProductName(String name) { this.productName = name; }    
        public Double getCurrentPrice() { return currentPrice; }    
        public void setCurrentPrice(Double p) { this.currentPrice = p; }    
        public Double getAveragePrice() { return averagePrice; }    
        public void setAveragePrice(Double p) { this.averagePrice = p; }    
        public Double getMinPrice() { return minPrice; }    
        public void setMinPrice(Double p) { this.minPrice = p; }    
        public Double getMaxPrice() { return maxPrice; }    
        public void setMaxPrice(Double p) { this.maxPrice = p; }    
        public Double getStandardDeviation() { return standardDeviation; }    
        public void setStandardDeviation(Double sd) { this.standardDeviation = sd; }    
        public String getTrendDirection() { return trendDirection; }    
        public void setTrendDirection(String t) { this.trendDirection = t; }    
        public Integer getTotalChanges() { return totalChanges; }    
        public void setTotalChanges(Integer c) { this.totalChanges = c; }    
        public Double getLargestChangePercentage() { return largestChangePercentage; }    
        public void setLargestChangePercentage(Double p) { this.largestChangePercentage = p; }    
        public Integer getAnomalyCount() { return anomalyCount; }    
        public void setAnomalyCount(Integer c) { this.anomalyCount = c; }
}
