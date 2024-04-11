package kr.ac.jnu.vocai.backend.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.ac.jnu.vocai.backend.chat.dto.Message;

import java.util.List;

/**
 * Open AI Chat Completion API 전용 Request 객체.
 *
 * @author daecheol song
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">Chat Completion API</a>
 * @since 1.0
 */
public record ChatRequest(String model, List<Message> messages, @JsonProperty("max_tokens") Long maxTokens) {

    public static ChatRequest of(String model, List<Message> messages, Long maxTokens) {
        return new ChatRequest(model, messages, maxTokens);
    }
    
}
