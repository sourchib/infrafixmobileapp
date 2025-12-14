package com.mobile.infrafixapp.controller;

import com.mobile.infrafixapp.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/prospects")
    public ResponseEntity<Map<String, String>> getProspects(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam String region) {

        String result;
        if (categoryId != null) {
            result = newsService.getProspectsByCategoryId(categoryId, region);
        } else if (categoryName != null) {
            result = newsService.getProspectsAndNews(categoryName, region);
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Either categoryName or categoryId must be provided"));
        }

        return ResponseEntity.ok(Map.of("result", result));
    }
}
