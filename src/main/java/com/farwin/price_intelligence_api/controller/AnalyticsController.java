package com.farwin.price_intelligence_api.controller;

import com.farwin.price_intelligence_api.model.PriceHistory;
import com.farwin.price_intelligence_api.model.PriceStatistics;
import com.farwin.price_intelligence_api.service.AnalyticsService;
import com.farwin.price_intelligence_api.service.AnomalyDetectionService;
import com.farwin.price_intelligence_api.service.CommodityDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AnomalyDetectionService anomalyDetectionService;
    private final CommodityDataService commodityDataService;

    public AnalyticsController(AnalyticsService analyticsService,
                               AnomalyDetectionService anomalyDetectionService,
                               CommodityDataService commodityDataService) {
        this.analyticsService = analyticsService;
        this.anomalyDetectionService = anomalyDetectionService;
        this.commodityDataService = commodityDataService;
    }

    // GET /api/analytics/products/1/statistics
    // Full price statistics for one product
    @GetMapping("/products/{id}/statistics")
    public ResponseEntity<PriceStatistics> getStatistics(@PathVariable Long id) {
        return analyticsService.getStatistics(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/analytics/statistics
    // Statistics for all products
    @GetMapping("/statistics")
    public ResponseEntity<List<PriceStatistics>> getAllStatistics() {
        return ResponseEntity.ok(analyticsService.getAllStatistics());
    }

    // GET /api/analytics/anomalies
    // All price history records flagged as anomalies
    @GetMapping("/anomalies")
    public ResponseEntity<List<PriceHistory>> getAnomalies() {
        return ResponseEntity.ok(anomalyDetectionService.getAllAnomalies());
    }

    // POST /api/analytics/fetch-now
    // Triggers an immediate commodity price fetch — use this during your demo
    @PostMapping("/fetch-now")
    public ResponseEntity<String> fetchNow() {
        commodityDataService.fetchNow();
        return ResponseEntity.ok("Price fetch triggered successfully");
    }
    
}