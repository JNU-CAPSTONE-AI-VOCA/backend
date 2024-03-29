package kr.ac.jnu.vocai.backend.chat.dto.request;

import kr.ac.jnu.vocai.backend.chat.dto.Message;

import java.util.List;

/**
 * Open AI Chat Completion API 전용 Request 객체.
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">Chat Completion API</a>
 * @author daecheol song
 * @since 1.0
 */
public record ChatRequest(String model, List<Message> messages) {

}
