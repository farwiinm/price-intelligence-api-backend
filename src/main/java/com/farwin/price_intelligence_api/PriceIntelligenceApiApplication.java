package com.farwin.price_intelligence_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PriceIntelligenceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PriceIntelligenceApiApplication.class, args);
	}

}
