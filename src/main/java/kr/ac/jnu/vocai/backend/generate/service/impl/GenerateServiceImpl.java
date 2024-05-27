package kr.ac.jnu.vocai.backend.generate.service.impl;

import kr.ac.jnu.vocai.backend.chat.adapter.ChatAdapter;
import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateSentenceRequest;
import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateConfuseWordRequest;
import kr.ac.jnu.vocai.backend.generate.service.GenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author daecheol song
 * @since 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateServiceImpl implements GenerateService {

    private final ChatAdapter chatAdapter;

    private static final String GENERATE_EXAMPLE_SENTENCE_USER_MESSAGE_TEMPLATE = """
            "{wordList}" have meanings respectively "{meaningList}". "{completeWordList}" 단어들을 사용하여 "{inCompleteWord}" 를 포함한 영어 예문 한 문장을 만들어라. 중요한 점은 너가 생성한 예문이 문맥상 어색하면 안되며, "{inCompleteWord}" 단어는 "{inCompleteMeaning}" 로 해석되어야 한다. 문맥이 어색하다면 임의로 새로운 단어를 추가하거나 "{completeWordList}" 단어들 중 일부분을 삭제해도 좋으니 이를 명심하면서 신중히 {inCompleteWord} 를 포함한 예문을 생성해라. 생성한 예문이 문맥에 어울린다면 너에게 큰 보상을 줄 것이다. 응답은 부가설명없이 예문만 응답해라.
            """;

    private static final String GENERATE_CONFUSE_WORDS_USER_MESSAGE_TEMPLATE = """
            "{word}" 와 헷갈릴만한 단어를 생성해야한다. 단, "{word}" 와 품사만 다른 형태는 제외해야 하며 단어의 의미가 "{meaning}" 와 유사하면 안된다. 적절한 응답을 하면 큰 보상을 얻을 것이다. "{word}" 단어를 포함한 4가지를 ","  를 구분자로 하여 한줄형태로 응답해라.
            """;

    @Override
    public String generateExampleSentence(GenerateSentenceRequest generateSentenceRequest) {
        List<GenerateSentenceRequest.Word> complete = generateSentenceRequest.complete();
        GenerateSentenceRequest.Word incomplete = generateSentenceRequest.incomplete();

        String inCompleteWord = incomplete.word();
        String inCompleteMeaning = incomplete.meaning();
        String completeWordList = complete.stream()
                .map(GenerateSentenceRequest.Word::word)
                .collect(Collectors.joining(", "));
        String wordList = completeWordList.concat(", ").concat(inCompleteWord);
        String meaningList = complete.stream()
                .map(GenerateSentenceRequest.Word::meaning)
                .collect(Collectors.joining(", "))
                .concat(", ").concat(inCompleteMeaning);

        PromptTemplate promptTemplate = new PromptTemplate(GENERATE_EXAMPLE_SENTENCE_USER_MESSAGE_TEMPLATE);

        return chatAdapter.createChatCompletion(promptTemplate.create(Map.of("wordList", wordList, "inCompleteMeaning", inCompleteMeaning, "completeWordList", completeWordList, "meaningList", meaningList, "inCompleteWord", inCompleteWord)))
                .getResult().getOutput().getContent().replaceAll(inCompleteWord, "____");
    }

    @Override
    public Flux<String> generateExampleSentenceStream(GenerateSentenceRequest generateSentenceRequest) {
        List<GenerateSentenceRequest.Word> complete = generateSentenceRequest.complete();
        GenerateSentenceRequest.Word incomplete = generateSentenceRequest.incomplete();

        String inCompleteWord = incomplete.word();
        String inCompleteMeaning = incomplete.meaning();
        String completeWordList = complete.stream()
                .map(GenerateSentenceRequest.Word::word)
                .collect(Collectors.joining(", "));
        String wordList = completeWordList.concat(", ").concat(inCompleteWord);
        String meaningList = complete.stream()
                .map(GenerateSentenceRequest.Word::meaning)
                .collect(Collectors.joining(", "))
                .concat(", ").concat(inCompleteMeaning);

        PromptTemplate promptTemplate = new PromptTemplate(GENERATE_EXAMPLE_SENTENCE_USER_MESSAGE_TEMPLATE);

        return chatAdapter.createChatCompletionStream(promptTemplate.create(Map.of("wordList", wordList, "inCompleteMeaning", inCompleteMeaning, "completeWordList", completeWordList, "meaningList", meaningList, "inCompleteWord", inCompleteWord)))
                .mapNotNull(resp -> resp.getResult().getOutput().getContent())
                .map(content -> content.trim().equals(inCompleteWord) ? " ____" : content)
                .doOnError(Exception.class, ex -> log.error("exception occur, cause = {}", ex.getMessage()))
                .onErrorResume(Exception.class, ex -> Flux.empty())
                .doOnNext(content -> log.debug("content : {}", content));
    }

    @Override
    public String generateConfuseWords(GenerateConfuseWordRequest generateConfuseWordRequest) {
        String word = generateConfuseWordRequest.word().word();
        String meaning = generateConfuseWordRequest.word().meaning();

        PromptTemplate promptTemplate = new PromptTemplate(GENERATE_CONFUSE_WORDS_USER_MESSAGE_TEMPLATE);
        List<String> collectionWords = new ArrayList<>(
                Arrays.stream(chatAdapter.createChatCompletion(promptTemplate.create(Map.of("word", word, "meaning", meaning)))
                                .getResult().getOutput().getContent().concat(", ").concat(word)
                                .split(",")
                        )
                        .map(cWord -> cWord.replaceAll("\\.", ""))
                        .map(String::trim)
                        .toList()
        );

        Collections.shuffle(collectionWords);

        return String.join(", ", collectionWords);
    }
}
