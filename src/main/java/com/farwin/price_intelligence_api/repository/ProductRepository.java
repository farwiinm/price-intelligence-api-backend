package com.farwin.price_intelligence_api.repository;

import com.farwin.price_intelligence_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>{
    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE p.currentPrice BETWEEN :min AND :max")
    List<Product> findByPriceRange(
        @Param("min") Double min,
        @Param("max") Double max
    );
}
