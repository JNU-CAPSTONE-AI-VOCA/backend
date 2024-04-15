package kr.ac.jnu.vocai.backend.chat.service.impl;

import kr.ac.jnu.vocai.backend.chat.adapter.ChatAdapter;
import kr.ac.jnu.vocai.backend.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author daecheol song
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatAdapter chatAdapter;
    @Override
    public String getChatCompletionMessageContent(String userMessageRequest) {
        return chatAdapter.createChatCompletion(userMessageRequest).messageContent();
    }
}
