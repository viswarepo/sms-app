package com.sms.ai.controller;

import com.sms.ai.service.SupportRagService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/support")
public class SupportChatController {

    private final SupportRagService supportRagService;

    public SupportChatController(SupportRagService supportRagService) {
        this.supportRagService = supportRagService;
    }

    @PostMapping("/ask")
    public SupportRagService.SupportAnswer ask(@RequestBody AskRequest request) {
        return supportRagService.answer(request.question());
    }

    public record AskRequest(String question) {}
}
