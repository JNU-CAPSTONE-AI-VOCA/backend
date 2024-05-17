package kr.ac.jnu.vocai.backend.file.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.jnu.vocai.backend.chat.adapter.ChatAdapter;
import kr.ac.jnu.vocai.backend.common.config.FileProperties;
import kr.ac.jnu.vocai.backend.common.utils.TokenUtils;
import kr.ac.jnu.vocai.backend.file.dto.request.FileExtractRequest;
import kr.ac.jnu.vocai.backend.file.dto.response.ExtractWordResponse;
import kr.ac.jnu.vocai.backend.file.service.ExtractService;
import kr.ac.jnu.vocai.backend.file.parser.TextParsers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * @author daecheol song
 * @since 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExtractServiceImpl implements ExtractService {

    private final TextParsers textParsers;
    private final FileProperties fileProperties;
    private final OpenAiChatProperties openAiChatProperties;
    private final ChatAdapter chatAdapter;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_CONTENT = """
    You are Korean English Teacher. Your task is to extract the words in the sentences given them. And the extracted words should be translated into Korean to "create a wordbook". I'll tell you a few things to keep in mind when you extract words. Make sure you keep them. 1.  In the given sentences, there may be words like "lxkjncvois" that you don't know what it means. If you think a token doesn't have a meaning, you "should never extract" it. 2. The words you have extracted "shouldn't contain numbers or special symbols." They should be in English only. For example, "12abc," "[sdfs]," "some234," and "https:/sdf/" should not be included in the tokens you have extracted. Now, I'll tell you the steps to extract sentences, so you have to extract tokens according to steps and respond according to the type of response I'll tell you.
    ## STEP 1 - First, "you have to extract up to 15 sentences from the given sentences." "Among the sentences, only sentences that a "person can understand" should be brought. If the sentence is too short or there is no word to extract, then the sentence should be extracted.
    ## STEP 2 - Tokens are extracted for each sentence extracted from STEP1. "You should only extract keywords from the sentence or words that are only in English that people can understand." And the token to be extracted can be a word or a compound word. you should not extract words as follows: "1", "ar", "x", "iv", "2", ";]" ,"02", "4"
    ## STEP 3 - The extracted tokens should fit the context and be interpreted naturally in Korean. "When a word is translated into Korean, it should never be included that is translated into a foreign language."
    ## STEP 4 - Since the response will be mapped to an object, the response should not be interrupted. If you think the response will be long, take the plunge and filter the response.
    ## STEP 5 - "You must make up to 15 sentences." Your response should be in JSONL format. Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation. Do not include markdown code blocks in your response.Here is the JSON Schema instance your output must adhere to:
    [
    {"sentence":"Studying English is very hard","words":[{"word":"Studying","meaning":"공부하는 것"}, {"word":"English","meaning":"영어"}, {"word":"hard","meaning":"어려운"}]}, {"sentence":"I can understand you.","words":[{"word":"can","meaning":"할 수 있는"}, {"word":"understand","meaning":"이해하다"}]}
    ]
    """;

    private static final String SYSTEM_CONTENT_STREAMING = """
    Your task is to extract the words in the sentences given them. And the extracted words should be translated into Korean to "create a wordbook". I'll tell you a few things to keep in mind when you extract words. Make sure you keep them. 1.  In the given sentences, there may be words like "lxkjncvois" that you don't know what it means. If you think a token doesn't have a meaning, you "should never extract" it. 2. The words you have extracted "shouldn't contain numbers or special symbols." They should be in English only. For example, "12abc," "[sdfs]," "some234," and "https:/sdf/" should not be included in the tokens you have extracted. Now, I'll tell you the steps to extract sentences, so you have to extract tokens according to steps and respond according to the type of response I'll tell you.
    ## STEP 1 - First, "Among the sentences, only sentences that a "person can understand" should be brought. If the sentence is too short or there is no word to extract, then the sentence should be extracted.
    ## STEP 2 - Tokens are extracted for each sentence extracted from STEP1. "You should only extract keywords from the sentence or words that are only in English that people can understand." And the token to be extracted can be a word or a compound word. you should not extract words as follows: "1", "ar", "x", "iv", "2", ";]" ,"02", "4"
    ## STEP 3 - The extracted tokens should fit the context and be interpreted naturally in Korean. "When a word is translated into Korean, it should never be included that is translated into a foreign language."
    ## STEP 4 - Since the response will be mapped to an object, the response should not be interrupted. If you think the response will be long, take the plunge and filter the response.
    ## STEP 5 - Your response should be in following format. "!!The number of sentences extracted and included in the response should be made as much as possible!!". # must be included as a separator for the response json object. Do not include any explanations, only provide a response following this format without deviation. Do not include markdown code blocks in your response.Here is the format instance your output must adhere to:
    
    #
    {"sentence":"Studying English is very hard","words":[{"word":"Studying","meaning":"공부하는 것"}, {"word":"English","meaning":"영어"}, {"word":"hard","meaning":"어려운"}]}
    #
    {"sentence":"I can understand you.","words":[{"word":"can","meaning":"할 수 있는"}, {"word":"understand","meaning":"이해하다"}]}
    #
    """;

    @Override
    public List<ExtractWordResponse> extractWord(FileExtractRequest extractRequest) {
        return extractRequest.files()
                .stream()
                .map(this::transferDataAndSaveFile)
                .filter(Objects::nonNull)
                .map(this::parseTextFromFile)
                .map(parsedText -> createPrompt(parsedText, false))
                .map(chatAdapter::createChatCompletion)
                .map(chatResponse -> chatResponse.getResult().getOutput().getContent())
                .map(response -> mapResponseToObject(response, new TypeReference<List<ExtractWordResponse>>() {
                }))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public Flux<ExtractWordResponse> extractWordStream(FileExtractRequest extractRequest) {

        return Flux.concat(extractRequest.files()
                        .stream()
                        .map(this::transferDataAndSaveFile)
                        .filter(Objects::nonNull)
                        .map(this::parseTextFromFile)
                        .map(parsedText -> createPrompt(parsedText, true))
                        .map(chatAdapter::createChatCompletionStream)
                        .toList())
                .map(chatResponse -> chatResponse.getResult().getOutput().getContent())
                .filter(Objects::nonNull)
                .bufferWhile(responseString -> !responseString.contains("#"))
                .map(jsonStrings -> String.join("", jsonStrings))
                .doOnError(Exception.class, ex -> log.error("exception occur, cause = {}", ex.getMessage()))
                .onErrorResume(Exception.class, ex -> Flux.empty())
                .filter(jsonString -> !jsonString.trim().isBlank())
                .doOnNext(jsonString -> log.info("jsonString : {}", jsonString))
                .mapNotNull(bufferedResponse -> mapResponseToObject(bufferedResponse, new TypeReference<ExtractWordResponse>() {}))
                .filter(Objects::nonNull)
                .doOnComplete(() -> log.info("data transferred successfully"));

    }


    private File transferDataAndSaveFile(MultipartFile multipartFile) {
        try {
            File file = new File(fileProperties.uploadDir() + multipartFile.getOriginalFilename());
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            log.error("cannot transfer data to file, cause = {}", e.getMessage());
        }
        return null;
    }

    private String parseTextFromFile(File file) {
        return textParsers.getTextParser(file.getName()).parse(file);
    }

    private <T> T mapResponseToObject(String response, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(response, typeReference);
        } catch (IOException e) {
            log.error("cannot map response to object, cause = {}", e.getMessage());
        }
        return null;
    }

    private Prompt createPrompt(String parsedText, boolean isStreaming) {
        SystemMessage systemMessage = new SystemMessage(isStreaming ? SYSTEM_CONTENT_STREAMING : SYSTEM_CONTENT);
        UserMessage userMessage = new UserMessage(TokenUtils.truncateStringWithDefaultModelType(parsedText, openAiChatProperties.getOptions().getMaxTokens()));
        return new Prompt(List.of(systemMessage, userMessage));
    }



}
