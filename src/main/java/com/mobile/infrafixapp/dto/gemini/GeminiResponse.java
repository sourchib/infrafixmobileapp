package com.mobile.infrafixapp.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponse {
    private List<GeminiCandidate> candidates;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiCandidate {
        private GeminiRequest.GeminiContent content;
    }
}
