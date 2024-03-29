package kr.ac.jnu.vocai.backend.chat.dto;

/**
 * Open AI Chat Completion API 전용 Request Message 객체
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">Chat Completion API</a>
 * @author daecheol song
 * @since 1.0
 */
public record Message(String role, String content) {
}
