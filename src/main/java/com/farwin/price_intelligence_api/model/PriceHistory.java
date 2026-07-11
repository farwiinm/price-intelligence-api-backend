package com.farwin.price_intelligence_api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
//import org.springframework.cglib.core.Local;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "price_history")
public class PriceHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;

    @Column(name = "old_price",nullable = false)
    private Double oldPrice;

    @Column(name = "new_price",nullable = false)
    private Double newPrice;

    @Column(name = "change_percetage")
    private Double changePercentage;

    @Column(name = "is_anomaly")
    private Boolean isAnomaly = false;

    @CreationTimestamp
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Transient
public String getProductName() {
    return product != null ? product.getName() : null;
}

@Transient  
public Long getProductId() {
    return product != null ? product.getId() : null;
}

    public PriceHistory(){}

    public PriceHistory(Product product, Double oldPrice, Double newPrice){
        this.product=product;
        this.oldPrice=oldPrice;
        this.newPrice=newPrice;
        this.changePercentage = ((newPrice - oldPrice)/oldPrice)*100;
        this.isAnomaly=false;
    }

    public Long getId(){
        return id;
    }

    public Product getProduct(){
        return product;
    }

    public Double getOldPrice(){
        return oldPrice;
    }

    public Double getNewPrice(){
        return newPrice;
    }

    public Double getChangePercentage(){
        return changePercentage;
    }

    public Boolean getIsAnomaly(){
        return isAnomaly;
    }

    public LocalDateTime getRecordedAt(){
        return recordedAt;
    }

    public void setOldPrice(Double oldPrice) { this.oldPrice = oldPrice; }
public void setNewPrice(Double newPrice) { this.newPrice = newPrice; }
public void setChangePercentage(Double changePercentage) { this.changePercentage = changePercentage; }

    public void setIsAnomaly(Boolean isAnomaly){
        this.isAnomaly = isAnomaly;
    }

    public void setProduct(Product product) { this.product = product; }
public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
}
