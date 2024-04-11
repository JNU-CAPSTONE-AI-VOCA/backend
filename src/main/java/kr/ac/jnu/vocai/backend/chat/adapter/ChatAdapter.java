package kr.ac.jnu.vocai.backend.chat.adapter;

import kr.ac.jnu.vocai.backend.chat.dto.response.ChatResponse;

/**
 * @author daecheol song
 * @since 1.0
 */
public interface ChatAdapter {
    ChatResponse createChatCompletion(String userMessage);
}
