package kr.ac.jnu.vocai.backend.chat.dto.response;

import java.util.List;

/**
 * Open AI Chat Completion API 전용 Response 객체.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">Chat Completion API</a>
 * @author daecheol song
 * @since 1.0
 */
public record ChatResponse(String id, List<Choice> choices, Integer created, String model) {
    private record Choice(String finishReason, String index, String message) {}
}
