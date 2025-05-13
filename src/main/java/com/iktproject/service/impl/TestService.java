package com.iktproject.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iktproject.model.Material;
import com.iktproject.model.Question;
import com.iktproject.model.Subject;
import com.iktproject.model.User;
import com.iktproject.repository.MaterialRepository;
import com.iktproject.repository.SubjectRepository;
import com.iktproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TestService {

    @Value("${openai.api.key}")
    private String openAiKey;

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final MaterialRepository materialRepository;


    public List<Question> generateTestForMaterial(Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        String prompt = """
        Based on the following material, generate 5 multiple-choice questions.
        Each question should include:
        - A question text
        - 4 options
        - The correct answer
        
        Format as JSON list:
        [
          {
            "text": "What is ...?",
            "options": ["A", "B", "C", "D"],
            "correctAnswer": "A"
          },
          ...
        ]
        
        Material:
        """ + material.getContent();

        String aiResponse = callOpenAi(prompt);

        return parseQuestionsFromJson(aiResponse);
    }


    public double calculateGrade(Map<String, String> answers) {
        int correct = 0;
        int total = answers.size();
        for (int i = 0; i < total; i++) {
            String userAnswer = answers.get("answer" + i);
            // Compare with correct answer from the stored/generated test
            if (userAnswer.equalsIgnoreCase("CORRECT_ANSWER")) correct++;
        }
        double rawScore = (double) correct / total;
        return Math.round((rawScore * 4 + 1) * 10.0) / 10.0; // Maps 0-100% to 1-5 scale
    }

    public User getLoggedInUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    private String callOpenAi(String prompt) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        // Create the request body as a Map and let WebClient serialize it
        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are an educational assistant.");
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);  // Use the prompt directly here
        messages.add(userMessage);

        body.put("messages", messages);
        body.put("temperature", 0.7);

        // Use WebClient to send the request
        return webClient.post()
                .bodyValue(body)  // WebClient will automatically serialize the map to JSON
                .retrieve()
                .onStatus(status -> {
                    // Check for client errors (4xx) and server errors (5xx)
                    if (status.is4xxClientError()) {
                        // Handle client error
                        return true;
                    } else if (status.is5xxServerError()) {
                        // Handle server error
                        return true;
                    }
                    return false;  // No error if not 4xx or 5xx
                }, response -> response.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new RuntimeException("Error occurred: " + response.statusCode() + " - " + errorBody))))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(10))  // Adjust timeout as necessary
                .onErrorResume(e -> {
                    // Handle all errors that occur during the request
                    System.out.println("Error: " + e.getMessage()); // Log the error message
                    return Mono.just("An error occurred: " + e.getMessage());
                })
                .block();  // If you need the result synchronously
    }

    private List<Question> parseQuestionsFromJson(String responseJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseJson);
            String content = root.get("choices").get(0).get("message").get("content").asText();

            return Arrays.asList(mapper.readValue(content, Question[].class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    public void generateMaterial(Long subjectId, String prompt) throws IOException {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        String request = """
    Generate an educational material for the following topic: %s.
    The result should contain a title and a body of educational text suitable for learning.
    """.formatted(prompt);

        String responseJson = callOpenAi(request);

        String content = extractContentFromAiResponse(responseJson);

        Material material = new Material();
        material.setTitle(prompt);
        material.setContent(content);
        material.setSubject(subject);

        materialRepository.save(material);
    }

    private String extractContentFromAiResponse(String response) throws IOException {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(response);
            // Process the JSON response
            return jsonNode.toString();
        } catch (JsonParseException e) {
            // Log the error and handle it gracefully, such as returning a default error message
            System.out.println("Failed to parse JSON response: " + response);
            throw new RuntimeException("Invalid response format");
        }
    }



}

