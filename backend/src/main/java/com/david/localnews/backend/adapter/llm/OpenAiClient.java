package com.david.localnews.backend.adapter.llm;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OpenAiClient {
    private final WebClient client;
    private final String model;
    private final Duration timeout;

    public OpenAiClient(WebClient.Builder builder,
                        @Value("${openai.apiKey:}") String apiKey,
                        @Value("${openai.model:gpt-4o-mini}") String model,
                        @Value("${openai.timeoutSec:30}") int timeoutSec) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("openai.apiKey is empty");
        }
        this.client = builder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.model = model;
        this.timeout = Duration.ofSeconds(timeoutSec);
    }

    public Mono<String> classify(String systemPrompt, String userPrompt) {
        var body = Map.of(
                "model", model,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.2
        );

        return client.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .onStatus(s -> s.value() == 401, r -> Mono.error(new RuntimeException("OpenAI: unauthorized")))
                .onStatus(s -> s.value() == 429, r -> Mono.error(new RuntimeException("OpenAI: rate limit")))
                .onStatus(HttpStatusCode::is5xxServerError, r -> Mono.error(new RuntimeException("OpenAI: server error")))
                .bodyToMono(ChatResponse.class)
                .timeout(timeout)
                .map(res -> {
                    if (res == null || res.choices() == null || res.choices().isEmpty()) return "";
                    var msg = res.choices().getFirst().message();
                    return msg != null ? String.valueOf(msg.content()) : "";
                });
    }

    public record ChatResponse(List<Choice> choices) {
        public record Choice(Message message) {}
        public record Message(Object content) {}
    }
}