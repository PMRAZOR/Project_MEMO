package com.memo.gpt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.memo.gpt.dto.ChatGPTRequest;
import com.memo.gpt.dto.ChatGPTResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatGPTService {
    private final RestTemplate template;

    @Value("${openai.api.url}")
    private String apiUrl;

    public ChatGPTResponse chat(ChatGPTRequest request) {
        return template.postForObject(
                apiUrl,
                request,
                ChatGPTResponse.class
        );
    }
}
