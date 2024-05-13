package kr.ac.jnu.vocai.backend.chat.adapter;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

/**
 * @author daecheol song
 * @since 1.0
 */
public interface ChatAdapter {

    ChatResponse createChatCompletion(Prompt prompt);

    Flux<ChatResponse> createChatCompletionStream(Prompt prompt);
}
