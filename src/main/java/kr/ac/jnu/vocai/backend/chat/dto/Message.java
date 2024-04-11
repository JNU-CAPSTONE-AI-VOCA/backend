package kr.ac.jnu.vocai.backend.chat.dto;

/**
 * Open AI Chat Completion API 전용 Request Message 객체
 *
 * @author daecheol song
 * @see <a href="https://platform.openai.com/docs/api-reference/chat/create">Chat Completion API</a>
 * @since 1.0
 */
public record Message(String role, String content) {

    public static Message of(String role, String content) {
        return new Message(role, content);
    }
}
