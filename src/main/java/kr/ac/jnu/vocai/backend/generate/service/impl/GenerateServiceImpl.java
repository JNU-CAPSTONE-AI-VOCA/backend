package kr.ac.jnu.vocai.backend.generate.service.impl;

import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateMeaningRequest;
import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateSentenceRequest;
import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateConfuseWordRequest;
import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateSentenceWordRequest;
import kr.ac.jnu.vocai.backend.generate.service.GenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author daecheol song
 * @since 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateServiceImpl implements GenerateService {

    private final ChatClient.Builder chatClientBuilder;

    private static final String GENERATE_EXAMPLE_SENTENCE_USER_MESSAGE_TEMPLATE = """
            "{wordList}" have meanings respectively "{meaningList}". "{completeWordList}" 단어들을 사용하여 "{inCompleteWord}" 를 반드시 포함한 영어 예문 한 문장을 만들어라. 중요한 점은 너가 생성한 예문이 문맥상 어색하면 안되며, "{inCompleteWord}" 단어는 "{inCompleteMeaning}" 로 해석되어야 한다. 문맥이 어색하다면 임의로 새로운 단어를 추가하거나 "{completeWordList}" 단어들 중 일부분을 삭제해도 좋으니 이를 명심하면서 신중히 {inCompleteWord} 단어 그대로 를 포함한 영어 예문을 생성해라. 생성한 영어 예문이 문맥에 어울린다면 너에게 큰 보상을 줄 것이다. 응답은 부가설명없이 "{inCompleteWord}"를 포함한 영어 예문만 응답해라.
            """;

    private static final String GENERATE_CONFUSE_WORD_SYSTEM_MESSAGE = """
            너는 영어 단어 문제 출제자이다. 너의 일은 사용자가 단어 한개를 주면 그 단어에 대한 선지 구성을 할 단어 4가지를 만들어야 한다. 단, 중복정답이 되지 않게 주어진 단어와 유사한 단어를 선지에 구성하면 안된다. 되도록이면 반의어를 만들려고 노력해라.
            응답은 부가설명없이 선지를 구성할 영단어 4가지를 ,를 구분자로 하여 한줄로 응답해라. !!응답형식은 반드시 지켜야한다.!!
            ### example
            # request : happy, 행복한
            # response :
            gloomy, sad, upset, sorrow
            """;

    private static final String GENERATE_CONFUSE_WORDS_USER_MESSAGE_TEMPLATE = """
            {word}, {meaning}
            """;

    private static final String GENERATE_SENTENCE_BY_WORD_TEMPLATE = """
            {word} 를 포함한 영어 예문을 생성해라. 예문을 구성하는 단어의 수준은 유럽연합 공통언어 표준등급(CEFR) 기준 A2 수준으로 구성해라. 단, "{word}" 가 "{meaning}" 으로 반드시 해석되어야 하는 문맥으로 영어 예문을 생성해야 한다. 적절한 응답을 하면 큰 보상을 얻을 것이다. 응답은 부가설명없이 {word} 를 포함한 영어 예문만 응답해라.
            """;

    private static final String GENERATE_SENTENCE_MEANING_TEMPLATE = """
            너는 유능한 한국어 번역가 이다."{sentence}" 를 번역가가 사람에게 번역해주듯이 한국어로 번역해라. 응답은 부가설명없이 한국어로 번역된 문장만 답해라. 응답형식을 지키면 큰 보상을 줄것이다.
            """;

    @Override
    public String generateExampleSentence(GenerateSentenceWordRequest generateSentenceWordRequest) {
        String word = generateSentenceWordRequest.word().word();
        String meaning = generateSentenceWordRequest.word().meaning();

        PromptTemplate promptTemplate = new PromptTemplate(GENERATE_SENTENCE_BY_WORD_TEMPLATE);

        return chatClientBuilder.build()
                .prompt(promptTemplate.create(Map.of("word", word, "meaning", meaning)))
                .call()
                .chatResponse()
                .getResult().getOutput().getContent();
    }

    @Override
    public Flux<String> generateExampleSentenceStream(GenerateSentenceRequest generateSentenceRequest) {
        List<GenerateSentenceRequest.Word> complete = generateSentenceRequest.complete();
        GenerateSentenceRequest.Word incomplete = generateSentenceRequest.incomplete();

        String inCompleteWord = incomplete.word().trim();
        String inCompleteMeaning = "( " + incomplete.meaning().trim() +" )";
        List<GenerateSentenceRequest.Word> completeList = new ArrayList<>(complete);
        Collections.shuffle(completeList);

        List<GenerateSentenceRequest.Word> completeShuffleList = new ArrayList<>();
        for(int i = 0; i < 3; i ++) {
            completeShuffleList.add(completeList.get(i));
        }

        String completeWordList = completeShuffleList.stream()
                .map(GenerateSentenceRequest.Word::word)
                .collect(Collectors.joining(", "));
        String wordList = completeWordList.concat(", ").concat(inCompleteWord);
        String meaningList = completeShuffleList.stream()
                .map(GenerateSentenceRequest.Word::meaning)
                .map(meaning -> "( " + meaning + " )")
                .collect(Collectors.joining(", "))
                .concat(", ").concat(inCompleteMeaning);

        PromptTemplate promptTemplate = new PromptTemplate(GENERATE_EXAMPLE_SENTENCE_USER_MESSAGE_TEMPLATE);

        String content = chatClientBuilder.build()
                .prompt(promptTemplate.create(Map.of("wordList", wordList, "inCompleteMeaning", inCompleteMeaning, "completeWordList", completeWordList, "meaningList", meaningList, "inCompleteWord", inCompleteWord)))
                .call()
                .chatResponse()
                .getResult().getOutput().getContent();

        if (inCompleteWord.contains(" ")) {
            content = content.replaceAll("(?i)" + inCompleteWord, "____");
        }

        return Flux.fromStream(Arrays.stream(content.split(" ")))
                .map(word -> filterInCompleteWord(word, inCompleteWord))
                .doOnNext(word -> log.info("word : {}", word));
    }


    private String filterInCompleteWord(String word, String inCompleteWord) {
        String append = "";
        if (word.endsWith(".") || word.endsWith(",")) {
            append = word.substring(word.length() - 1);
            word = word.substring(0, word.length() - 1);
        }

        if (word.equalsIgnoreCase(inCompleteWord)) {
            return "____" + append + " ";
        }

        if (word.length() < 3) {
            if (inCompleteWord.equalsIgnoreCase(word)) {
                return "____" + append + " ";
            }
            return word + append + " ";
        }
        if (word.endsWith("s")) {
            if (inCompleteWord.toLowerCase().equals(word.toLowerCase().substring(0, word.length() - 1))) {
                return "____" + append + " ";
            }
        }
        if (word.endsWith("ly") || word.endsWith("ed") || word.endsWith("es")) {
            if (inCompleteWord.toLowerCase().contains(word.toLowerCase().substring(0, word.length() - 2))) {
                return "____" + append + " ";
            }
        }
        if (word.endsWith("ies") || word.endsWith("ily")) {
            if (inCompleteWord.toLowerCase().contains(word.toLowerCase().substring(0, word.length() - 3))) {
                return "____" + append + " ";
            }
        }
        return word + append + " ";
    }

    @Override
    public String generateConfuseWords(GenerateConfuseWordRequest generateConfuseWordRequest) {
        String word = generateConfuseWordRequest.word().word();
        String meaning = generateConfuseWordRequest.word().meaning();

        PromptTemplate promptTemplate = new PromptTemplate(GENERATE_CONFUSE_WORDS_USER_MESSAGE_TEMPLATE);
        Set<String> collectionWords = new HashSet<>(
                Arrays.stream(chatClientBuilder.build()
                                .prompt()
                                .system(GENERATE_CONFUSE_WORD_SYSTEM_MESSAGE)
                                .user(promptTemplate.render(Map.of("word", word, "meaning", meaning)))
                                .call()
                                .chatResponse()
                                .getResult().getOutput().getContent().concat(", ").concat(word)
                                .split(",")
                        )
                        .map(cWord -> cWord.replaceAll("\\.", ""))
                        .map(String::trim)
                        .toList()
        );


        return String.join(", ", collectionWords);
    }

    @Override
    public String generateSentenceMeaning(GenerateMeaningRequest generateMeaningRequest) {
        String sentence = generateMeaningRequest.sentence();
        PromptTemplate promptTemplate = new PromptTemplate(GENERATE_SENTENCE_MEANING_TEMPLATE);

        return chatClientBuilder.build()
                .prompt(promptTemplate.create(Map.of("sentence", sentence)))
                .call()
                .chatResponse()
                .getResult().getOutput().getContent();
    }

}
