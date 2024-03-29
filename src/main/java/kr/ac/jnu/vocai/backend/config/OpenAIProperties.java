package kr.ac.jnu.vocai.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OPEN AI API 프로퍼티.
 * @author daecheol song
 * @since 1.0
 */
@ConfigurationProperties("openai")
public record OpenAIProperties(String key, String model, String chatEndpoint) {
}
