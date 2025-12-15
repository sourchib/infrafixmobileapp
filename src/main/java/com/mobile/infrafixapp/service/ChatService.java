package com.mobile.infrafixapp.service;

import com.mobile.infrafixapp.dto.request.ChatRequest;
import com.mobile.infrafixapp.dto.ChatResponse;
import com.mobile.infrafixapp.dto.gemini.GeminiRequest;
import com.mobile.infrafixapp.dto.gemini.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public ChatResponse getChatResponse(ChatRequest request) {
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return new ChatResponse("Please provide a message.");
        }

        String url = apiUrl + "?key=" + apiKey;

        GeminiRequest.GeminiPart part = new GeminiRequest.GeminiPart(request.getMessage());
        GeminiRequest.GeminiContent content = new GeminiRequest.GeminiContent(Collections.singletonList(part));
        GeminiRequest geminiRequest = new GeminiRequest(Collections.singletonList(content));

        try {
            log.info("Sending request to Gemini API");
            GeminiResponse geminiResponse = restTemplate.postForObject(url, geminiRequest, GeminiResponse.class);

            if (geminiResponse != null && geminiResponse.getCandidates() != null
                    && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.GeminiCandidate candidate = geminiResponse.getCandidates().get(0);
                if (candidate.getContent() != null && candidate.getContent().getParts() != null
                        && !candidate.getContent().getParts().isEmpty()) {
                    String text = candidate.getContent().getParts().get(0).getText();
                    return new ChatResponse(text);
                }
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("Gemini API Error: Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new ChatResponse("Error from AI Service: " + e.getStatusText());
        } catch (Exception e) {
            log.error("Unexpected error in ChatService", e);
            return new ChatResponse("Sorry, I am having trouble connecting to the AI service right now.");
        }

        return new ChatResponse("Sorry, I couldn't process that response.");
    }
}
