package com.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportRagService {

    private static final String USER_PROMPT_TEMPLATE = """
            CONTEXT:
            %s

            QUESTION:
            %s
            """;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("${support.rag.top-k:4}")
    private int topK;

    public SupportRagService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public SupportAnswer answer(String question) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(topK)
                .similarityThreshold(0.5)
                .build();

        List<Document> matches = vectorStore.similaritySearch(searchRequest);

        String context = matches.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        String userPrompt = USER_PROMPT_TEMPLATE.formatted(
                context.isBlank() ? "(no matching knowledge base articles found)" : context,
                question
        );

        String reply = chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();

        List<String> sources = matches.stream()
                .map(doc -> String.valueOf(doc.getMetadata().getOrDefault("source", "knowledge-base")))
                .distinct()
                .toList();

        return new SupportAnswer(reply, sources);
    }

    public record SupportAnswer(String answer, List<String> sources) {}
}
