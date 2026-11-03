package com.sms.controller;

import com.ai.service.RagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    // Example URI: http://localhost:8080/api/ask?question=What is the secret passphrase?
    @GetMapping("/ask/query")
    public String ask(@RequestParam(value = "question") String question) {
        return ragService.askKnowledgeBase(question);
    }
}
