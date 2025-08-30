package com.david.localnews.backend.adapter.llm;

import java.time.Duration;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class LlmClassifierAdapter implements NewsClassifier {
    private final OpenAiClient openAiClient;

    public LlmClassifierAdapter(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @Override
    public Classification classify(String title, String summary, String source) {
        String user = """
        TITLE: %s
        SOURCE: %s
        SUMMARY: %s
        Decide local/global for a U.S. city. If local, provide city and state code.
        """.formatted(safe(title), safe(source), safe(summary));
        try {
            String json = openAiClient.classify(SYS, user).block(Duration.ofSeconds(35));
            LlmResult res = LlmParser.parse(json);
            boolean isLocal = res.isLocal();
            int conf = Math.max(0, Math.min(res.confidence(), 100));
            return new Classification(
                    isLocal,
                    safe(res.city()),
                    safe(res.state()).toUpperCase(Locale.US),
                    conf
            );
        } catch (Exception e) {
            return new Classification(false, "", "", 0);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static final String SYS = """
      You classify news for U.S. localities.
      Return STRICT JSON: {"kind":"local|global","city":"<city or empty>","state":"<US state code 2 letters or empty>","confidence":0-100}
      """;
}