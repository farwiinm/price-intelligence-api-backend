package com.farwin.price_intelligence_api.service;

import com.farwin.price_intelligence_api.model.PriceHistory;
import com.farwin.price_intelligence_api.repository.PriceHistoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnomalyDetectionService {

    // If price changes by more than this % in one update — flag it
    private static final double SPIKE_THRESHOLD = 20.0;

    // If price is more than this many standard deviations from average — flag it
    private static final double ZSCORE_THRESHOLD = 2.0;

    // If price updated more than this many times in 24 hours — flag it
    private static final int FREQUENCY_THRESHOLD = 3;

    private final PriceHistoryRepository priceHistoryRepository;

    public AnomalyDetectionService(PriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }

    // Master check — runs all three rules
    // Returns true if ANY rule fires
    public boolean isAnomaly(Long productId, Double oldPrice, Double newPrice) {
        return isSpikeAnomaly(oldPrice, newPrice)
            || isZScoreAnomaly(productId, newPrice)
            || isFrequencyAnomaly(productId);
    }

    // Rule 1 — did the price change by more than 20% in one update?
    private boolean isSpikeAnomaly(Double oldPrice, Double newPrice) {
        if (oldPrice == null || oldPrice == 0) return false;
        double changePercent = Math.abs((newPrice - oldPrice) / oldPrice * 100);
        return changePercent > SPIKE_THRESHOLD;
    }

    // Rule 2 — is the new price more than 2 standard deviations from the average?
    private boolean isZScoreAnomaly(Long productId, Double newPrice) {
        List<PriceHistory> history =
            priceHistoryRepository.findByProductIdOrderByRecordedAtDesc(productId);

        if (history.size() < 5) return false; // not enough history to judge

        double average = history.stream()
            .mapToDouble(h -> h.getNewPrice())
            .average()
            .orElse(0.0);

        double variance = history.stream()
            .mapToDouble(h -> Math.pow(h.getNewPrice() - average, 2))
            .average()
            .orElse(0.0);

        double stdDev = Math.sqrt(variance);

        if (stdDev == 0) return false;

        double zScore = Math.abs((newPrice - average) / stdDev);
        return zScore > ZSCORE_THRESHOLD;
    }

    // Rule 3 — has this product been updated more than 3 times in 24 hours?
    private boolean isFrequencyAnomaly(Long productId) {
    LocalDateTime oneDayAgo = LocalDateTime.now().minusHours(24);
    List<PriceHistory> history =
        priceHistoryRepository.findByProductIdOrderByRecordedAtDesc(productId);

    long recentCount = history.stream()
        .filter(h -> h.getRecordedAt() != null
            && h.getRecordedAt().isAfter(oneDayAgo)
            && Math.abs(h.getChangePercentage()) > 5.0)
        .count();

    return recentCount >= FREQUENCY_THRESHOLD;
}

    // Returns all price history records flagged as anomalies
    public List<PriceHistory> getAllAnomalies() {
    return priceHistoryRepository.findAnomaliesWithProduct();
}
}