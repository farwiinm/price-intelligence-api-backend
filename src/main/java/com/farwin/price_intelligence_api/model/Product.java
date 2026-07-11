package com.farwin.price_intelligence_api.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table (name="products")
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(name = "current_price", nullable = false)
    private Double currentPrice;

    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceHistory> priceHistory = new ArrayList<>();

    //empty constructor for Jackson
    public Product(){}

    //filled constructor 
    public Product(String name, String category, Double currentPrice){
        this.name = name;
        this.category = category;
        this.currentPrice = currentPrice;
    }

    //getters
    public Long getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getCategory(){
        return category;
    }
    public Double getCurrentPrice(){
        return currentPrice;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public List<PriceHistory> getPriceHistory(){
        return priceHistory;
    }
    //setters
    public void setName(String name){
        this.name =name;
    }
    public void setCategory(String category){
        this.category =category;
    }
    public void setCurrentPrice(Double currentPrice){
        this.currentPrice =currentPrice;
    }
}