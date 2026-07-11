package com.farwin.price_intelligence_api.repository;

import com.farwin.price_intelligence_api.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.farwin.price_intelligence_api.model.Product;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByProductOrderByRecordedAtDesc(Product product);

    List<PriceHistory> findTop10ByProductOrderByRecordedAtDesc(Product product);

    @Query("SELECT h FROM PriceHistory h JOIN FETCH h.product WHERE h.isAnomaly = true ORDER BY h.recordedAt DESC")
    List<PriceHistory> findAnomaliesWithProduct();

    @Query("SELECT h FROM PriceHistory h WHERE h.product.id = :productId ORDER BY h.recordedAt DESC")
List<PriceHistory> findByProductIdOrderByRecordedAtDesc(@Param("productId") Long productId);

@Query("SELECT h FROM PriceHistory h WHERE h.product.id = :productId ORDER BY h.recordedAt DESC")
List<PriceHistory> findTop10ByProductIdOrderByRecordedAtDesc(@Param("productId") Long productId);
}
