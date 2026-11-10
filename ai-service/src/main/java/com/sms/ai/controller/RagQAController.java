package com.sms.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rag/qa")
public class RagQAController {

    private final ChatClient chatClient;

    @Autowired
    private final VectorStore vectorStore;

    // Use ChatClient.Builder to wire up components seamlessly
    public RagQAController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/ask/query")
    public String ask(@RequestParam(value = "query") String query) {

        // 1. Fetch relevant records using similarity search
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(3) // Fetch top 3 matches
                        .build()
        );

        // 2. Combine document snippets into a context block
        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));

        // 3. Prompt Template injection
        String systemPrompt = """
                You are a helpful assistant. Answer the user prompt using only the context provided below.
                If you do not know the answer based on the context, say 'I cannot find that in my database'.
                
                CONTEXT:
                {context}
                """;

        // 4. Send request to Ollama and return results
        return chatClient.prompt()
                .system(sp -> sp.text(systemPrompt).param("context", context))
                .user(query)
                .call()
                .content();
    }
}
