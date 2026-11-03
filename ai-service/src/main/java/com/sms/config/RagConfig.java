package com.sms.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class RagConfig {
    /*@Value("classpath:knowledge-base.txt")
    private Resource rawDocument;

    @Bean(name = "myCustomVectorStore")
    public VectorStore vectorStore(EmbeddingModel embeddingModel) throws IOException {
        // SimpleVectorStore handles in-memory storage (Use PGVector or MongoDB for production)
        VectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        // Load, split, and ingest your document text
        String content = new String(rawDocument.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Document document = new Document(content);

        // Add text data into the Vector Store
        vectorStore.add(List.of(document));
        return vectorStore;
    }*/


   /* @Configuration
    public class VectorStoreConfig {

        @Bean(name = "myCustomVectorStore")
        public VectorStore vectorStore(EmbeddingModel embeddingModel) {
            // SimpleVectorStore is excellent for local testing and lightweight text files
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }*/
}
