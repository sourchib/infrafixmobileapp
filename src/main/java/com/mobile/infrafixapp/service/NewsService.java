package com.mobile.infrafixapp.service;

import com.mobile.infrafixapp.dto.gemini.GeminiRequest;
import com.mobile.infrafixapp.dto.gemini.GeminiResponse;
import com.mobile.infrafixapp.model.ReportCategory;
import com.mobile.infrafixapp.repository.ReportCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final RestTemplate restTemplate;
    private final ReportCategoryRepository reportCategoryRepository;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public String getProspectsAndNews(String categoryName, String region) {
        String prompt = String.format(
                "Temukan prospek pekerjaan, proyek, dan berita terkini yang relevan dengan infrastruktur kategori '%s' di daerah '%s'. "
                        +
                        "Fokus pada perbaikan, pembangunan, atau inisiatif pemerintah setempat.",
                categoryName, region);

        return callGeminiApi(prompt);
    }

    public String getProspectsByCategoryId(Integer categoryId, String region) {
        ReportCategory category = reportCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return getProspectsAndNews(category.getName(), region);
    }

    private String callGeminiApi(String prompt) {
        String url = apiUrl + "?key=" + apiKey;

        GeminiRequest.GeminiPart part = new GeminiRequest.GeminiPart(prompt);
        GeminiRequest.GeminiContent content = new GeminiRequest.GeminiContent(Collections.singletonList(part));
        GeminiRequest geminiRequest = new GeminiRequest(Collections.singletonList(content));

        try {
            GeminiResponse geminiResponse = restTemplate.postForObject(url, geminiRequest, GeminiResponse.class);

            if (geminiResponse != null && geminiResponse.getCandidates() != null
                    && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.GeminiCandidate candidate = geminiResponse.getCandidates().get(0);
                if (candidate.getContent() != null && candidate.getContent().getParts() != null
                        && !candidate.getContent().getParts().isEmpty()) {
                    return candidate.getContent().getParts().get(0).getText();
                }
            }
        } catch (Exception e) {
            return "Failed to fetch data from AI: " + e.getMessage();
        }

        return "No information found.";
    }
}
