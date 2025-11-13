package com.williammedina.content_analysis_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ContentAnalysisServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentAnalysisServiceApplication.class, args);
	}

}
