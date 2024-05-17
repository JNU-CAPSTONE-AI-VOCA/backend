package kr.ac.jnu.vocai.backend.chat.adapter.impl;

import kr.ac.jnu.vocai.backend.chat.adapter.ChatAdapter;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.prompt.Prompt;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;


/**
 * @author daecheol song
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class ChatAdapterImpl implements ChatAdapter {

    private final ChatClient chatClient;
    private final StreamingChatClient streamingChatClient;

    @Override
    public ChatResponse createChatCompletion(Prompt prompt) {
        return chatClient.call(prompt);
    }

    @Override
    public Flux<ChatResponse> createChatCompletionStream(Prompt prompt) {
        return streamingChatClient.stream(prompt);
    }
}
