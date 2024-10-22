package com.project.parkingfinder.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ApiKeyService {

    private final List<Map<String, String>> apiKeys;

    public ApiKeyService() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("api_key.json");
        this.apiKeys = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Map<String, String>>>() {});
    }

    public Optional<String> getUserIdForApiKey(String apiKey) {
        return apiKeys.stream()
                .filter(entry -> apiKey.equals(entry.get("apiKey")))
                .map(entry -> entry.get("userId"))
                .findFirst();
    }
}