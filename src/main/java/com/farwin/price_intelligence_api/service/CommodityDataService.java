package com.farwin.price_intelligence_api.service;

import com.farwin.price_intelligence_api.model.Product;
import com.farwin.price_intelligence_api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CommodityDataService {

    @Value("${api.ninjas.key}")
    private String apiKey;

    @Value("${api.ninjas.url}")
    private String apiUrl;

    private static final double CONVERSION_FACTOR = 300.0;

    private final ProductRepository productRepository;
    private final ProductService productService;

    private static final Map<String, String> COMMODITY_MAP = new HashMap<>();
    static {
        COMMODITY_MAP.put("rice", "Rice");
        COMMODITY_MAP.put("sugar", "Sugar");
        COMMODITY_MAP.put("wheat", "Wheat Flour");
        COMMODITY_MAP.put("corn", "Corn");
        COMMODITY_MAP.put("palm_oil", "Palm Oil");
        COMMODITY_MAP.put("coffee", "Coffee");
    }

    public CommodityDataService(ProductRepository productRepository,
                                ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Scheduled(fixedRate = 3600000, initialDelay = 60000)
    public void fetchAndUpdatePrices() {
        System.out.println(">>> Starting commodity price update...");
        RestTemplate restTemplate = new RestTemplate();

        for (Map.Entry<String, String> entry : COMMODITY_MAP.entrySet()) {
            String commodityName = entry.getKey();
            String productName = entry.getValue();

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-Api-Key", apiKey);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                String url = apiUrl + "?name=" + commodityName;
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
    url, HttpMethod.GET, entity, 
    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});

                if (response.getBody() != null && response.getBody().containsKey("price")) {
                    double usdPrice = ((Number) response.getBody().get("price")).doubleValue();
                    double localPrice = Math.round(usdPrice * CONVERSION_FACTOR * 100.0) / 100.0;

                    Optional<Product> productOpt = productRepository
                        .findByNameContainingIgnoreCase(productName)
                        .stream().findFirst();

                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        double currentPrice = product.getCurrentPrice();

                        if (Math.abs(localPrice - currentPrice) > 0.01) {
                            productService.updatePrice(product.getId(), localPrice);
                            System.out.println(">>> Updated " + productName
                                + ": " + currentPrice + " -> " + localPrice);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(">>> Failed to fetch price for "
                    + commodityName + ": " + e.getMessage());
            }
        }
        System.out.println(">>> Commodity price update complete");
    }

    public void fetchNow() {
        fetchAndUpdatePrices();
    }
}