package kr.ac.jnu.vocai.backend.chat.adapter.impl;

import kr.ac.jnu.vocai.backend.chat.adapter.ChatAdapter;
import kr.ac.jnu.vocai.backend.chat.dto.Message;
import kr.ac.jnu.vocai.backend.chat.dto.request.ChatRequest;
import kr.ac.jnu.vocai.backend.chat.dto.response.ChatResponse;
import kr.ac.jnu.vocai.backend.common.config.OpenAIProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author daecheol song
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class ChatAdapterImpl implements ChatAdapter {

    private final RestTemplate restTemplate;
    private final OpenAIProperties openAIProperties;
    private final Long maxTokens = 1024L;
    private final String SYSTEM_CONTENT = """
You are a high school English teacher in South Korea, teaching Korean high school students.
Your task is to analyze the sentences given to you by your students and extract the words and their Korean definitions that are key to the Korean interpretation and turn them into a wordlist.
The sentences you are given may be poorly scanned and contain non-literary words.
The meaning of a word should be interpreted as naturally as possible according to the situation.
However, since you are an expert in English, you should exclude non-literary based on the context.
You must extract all the key words needed to interpret the sentence, and the Korean meaning of the word must be the contextual meaning.
Verbs should only be extracted in a verb-root.
If the word is used as an idiom that has a special meaning, you should include the idiom itself, including the spacing.
If you fail to extract the key words of the correct sentence, the student will get a bad score on the test. And you will be fired from the company.
But if you extract the key words by properly filtering the non-literary and turning it into a normal sentence, you will get an instant $500 tip.

## STEP1
Among the sentences you received, you should first filter out sentences that are not grammatically correct or have no meaning, such as "vjnvsdv".
## STEP2
The key words in the sentence are extracted, and the words and meanings are made in CSV form.
## STEP3
Look at the words and meanings made in STEP1 and make sure to check again whether the words and meanings match one-on-one in Korean, and if it's awkward, reinterpret the meaning of the word again.
if one of the words and meanings extracted in step1 : "from view, 눈에보이지 않게"\s
In this case, if interpreted in combination with the previous word, it is contextually correct, but if you look at "from view" itself, it means "시야로부터"
Never believe that the words and meanings you first extracted are correct, but double-check after extraction and correct them.
Take a deep breath and work on this problem step-by-step.
Other artificial intelligence models are good at extracting, so you can do it, right?
            
###example###
given sentence : "He was born on a Thursday. In the case of apples are hidden from view"
            
The response shall be in the form of csv as follows :
He, 그는
was born, 태어났다
on, ~에
Thursday, 목요일
In the case of, ~의 경우에
apples, 사과들
hidden,숨겨진
from view, 시야로부터
""";

    @Override
    public ChatResponse createChatCompletion(String userMessageRequest) {
        Message systemMessage = Message.of("system", SYSTEM_CONTENT);
        int size = Math.min(userMessageRequest.length(), 40000);
        Message userMessage = Message.of("user", userMessageRequest.substring(0, size));

        ChatRequest chatRequest = ChatRequest.of(openAIProperties.model(),
                List.of(systemMessage, userMessage),
                maxTokens);

        return restTemplate.exchange(openAIProperties.chatEndpoint(),
                HttpMethod.POST,
                new HttpEntity<>(chatRequest, new HttpHeaders()),
                ChatResponse.class).getBody();
    }
}
