package com.mobile.infrafixapp.service;

import com.mobile.infrafixapp.dto.request.ChatRequest;
import com.mobile.infrafixapp.dto.ChatResponse;
import com.mobile.infrafixapp.dto.gemini.GeminiRequest;
import com.mobile.infrafixapp.dto.gemini.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public ChatResponse getChatResponse(ChatRequest request) {
        String url = apiUrl + "?key=" + apiKey;

        GeminiRequest.GeminiPart part = new GeminiRequest.GeminiPart(request.getMessage());
        GeminiRequest.GeminiContent content = new GeminiRequest.GeminiContent(Collections.singletonList(part));
        GeminiRequest geminiRequest = new GeminiRequest(Collections.singletonList(content));

        try {
            GeminiResponse geminiResponse = restTemplate.postForObject(url, geminiRequest, GeminiResponse.class);

            if (geminiResponse != null && geminiResponse.getCandidates() != null
                    && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.GeminiCandidate candidate = geminiResponse.getCandidates().get(0);
                if (candidate.getContent() != null && candidate.getContent().getParts() != null
                        && !candidate.getContent().getParts().isEmpty()) {
                    return new ChatResponse(candidate.getContent().getParts().get(0).getText());
                }
            }
        } catch (Exception e) {
            return new ChatResponse("Sorry, I am having trouble connecting to the AI service right now.");
        }

        return new ChatResponse("Sorry, I couldn't process that.");
    }
}
