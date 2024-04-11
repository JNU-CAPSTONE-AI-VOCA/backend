package kr.ac.jnu.vocai.backend.chat.service;

/**
 * @author daecheol song
 * @since 1.0
 */
public interface ChatService {
    String getChatCompletionMessageContent(String userMessageRequest);
}
