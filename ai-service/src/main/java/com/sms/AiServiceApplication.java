package com.ai;

import com.ai.service.RagDataLoaderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiragServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiragServiceApplication.class, args);
	}

    @Bean
    CommandLineRunner initDatabase(RagDataLoaderService loader) {
        return args -> {
            // Seed your vector database on startup
            loader.populateVectorStore();
            System.out.println(">>> Vector store populated with context documentation!");
        };
    }
}
