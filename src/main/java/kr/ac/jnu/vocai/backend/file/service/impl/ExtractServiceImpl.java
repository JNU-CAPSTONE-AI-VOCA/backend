package kr.ac.jnu.vocai.backend.file.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.jnu.vocai.backend.common.config.FileProperties;
import kr.ac.jnu.vocai.backend.common.utils.FileUploadUtils;
import kr.ac.jnu.vocai.backend.common.utils.TokenUtils;
import kr.ac.jnu.vocai.backend.file.dto.request.FileExtractRequest;
import kr.ac.jnu.vocai.backend.file.dto.response.ExtractWordResponse;
import kr.ac.jnu.vocai.backend.file.service.ExtractService;
import kr.ac.jnu.vocai.backend.file.parser.TextParsers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
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
    private final ChatClient.Builder chatClientBuilder;
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
    ## STEP 5 - Your response should be in following format. 영어 문장을 가능한 많이 추출해라. "JSON" must be included as a delimiter for the response json object. Do not include any explanations, only provide a response following this format without deviation. Do not include markdown code blocks in your response.Here is the format instance your output must adhere to:
    JSON
    {"sentence":"Studying English is very hard","words":[{"word":"Studying","meaning":"공부하는 것"}, {"word":"English","meaning":"영어"}, {"word":"hard","meaning":"어려운"}]}
    JSON
    {"sentence":"I can understand you.","words":[{"word":"can","meaning":"할 수 있는"}, {"word":"understand","meaning":"이해하다"}]}
    """;

    private static final String IMG__USER_MESSAGE = """
    이 사진에서 영어 문장을 추출해라. 그리고 추출된 문장과 그 문장에 있는 모든 단어가 아닌 핵심 단어들만 반드시 문맥에 맞게 해석한후 응답하면 된다. 만약 사진에서 영어 문장을 추출할 수 없다면 반드시 "X" 문자 하나만 보내라. Do not include any explanations, only provide a response following this format without deviation. Do not include markdown code blocks in your response.Here is the format instance your output must adhere to:
    [
    {"sentence":"Studying English is very hard","words":[{"word":"Studying","meaning":"공부하는 것"}, {"word":"English","meaning":"영어"}, {"word":"hard","meaning":"어려운"}]}, {"sentence":"I can understand you.","words":[{"word":"can","meaning":"할 수 있는"}, {"word":"understand","meaning":"이해하다"}]}
    ]""";

    private static final String IMG_STREAM_USER_MESSAGE = """
    이 사진에서 영어 문장을 추출해라. 그리고 추출된 문장과 그 문장에 있는 단어들을 반드시 문맥에 맞게 해석한후 응답하면 된다. 만약 사진에서 영어 문장을 추출할 수 없다면 반드시 "X" 문자 하나만 보내라. Your response should be in following format. "JSON" must be included as a delimiter for the response json object. Do not include any explanations, only provide a response following this format without deviation. Do not include markdown code blocks in your response.Here is the format instance your output must adhere to:
    JSON
    {"sentence":"Studying English is very hard","words":[{"word":"Studying","meaning":"공부하는 것"}, {"word":"English","meaning":"영어"}, {"word":"hard","meaning":"어려운"}]}
    JSON
    {"sentence":"I can understand you.","words":[{"word":"can","meaning":"할 수 있는"}, {"word":"understand","meaning":"이해하다"}]}
    """;

    @Override
    public List<ExtractWordResponse> extractWord(FileExtractRequest extractRequest) {

        return extractRequest.files()
                .stream()
                .map(this::fileSave)
                .filter(Objects::nonNull)
                .map(file -> createPromptByFile(file, false))
                .map(prompt -> chatClientBuilder
                        .build()
                        .prompt(prompt)
                        .call().chatResponse())
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
                        .map(this::fileSave)
                        .filter(Objects::nonNull)
                        .map(file -> createPromptByFile(file, true))
                        .map(prompt -> chatClientBuilder.build()
                                .prompt(prompt)
                                .stream()
                                .chatResponse())
                        .toList())
                .map(chatResponse -> chatResponse.getResult().getOutput().getContent())
                .map(content -> {
                    if (content.equals("X")) {
                        throw new RuntimeException("cannot parse english sentence from image.");
                    }
                    return content;
                })
                .bufferWhile(responseString -> !responseString.trim().equals("JSON"))
                .map(jsonStrings -> String.join("", jsonStrings))
                .doOnNext(jsonString -> log.info("jsonString : {}", jsonString))
                .doOnError(Exception.class, ex -> log.error("exception occur, cause = {}", ex.getMessage()))
                .onErrorResume(NullPointerException.class, ex -> Flux.empty())
                .filter(jsonString -> !jsonString.trim().isBlank())
                .mapNotNull(bufferedResponse -> mapResponseToObject(bufferedResponse, new TypeReference<ExtractWordResponse>() {
                }))
                .doOnComplete(() -> log.info("data transferred successfully"));

    }

    private File fileSave(MultipartFile multipartFile) {

        String originalFileName = multipartFile.getOriginalFilename();
        if (!FileUploadUtils.isValidFileName(originalFileName)) {
            throw new RuntimeException("fileName is invalid, fileName = " + originalFileName);
        }

        try {
            File file = new File(fileProperties.uploadDir() + FileUploadUtils.toUploadFileName(originalFileName));
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            log.error("cannot transfer data to file, cause = {}", e.getMessage());
        }
        return null;
    }


    private <T> T mapResponseToObject(String response, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(response, typeReference);
        } catch (IOException e) {
            log.error("cannot map response to object, cause = {}", e.getMessage());
        }
        return null;
    }

    private Prompt createPromptByFile(File file, boolean isStreaming) {

        if (FileUploadUtils.isImage(file.getName())) {
            FileSystemResource imgResource = new FileSystemResource(file);
            UserMessage userMessage = new UserMessage(isStreaming ? IMG_STREAM_USER_MESSAGE : IMG__USER_MESSAGE,
                    new Media(FileUploadUtils.parseImageMimeType(FileUploadUtils.getExtension(file.getName())), imgResource));
            OpenAiChatOptions options = new OpenAiChatOptions.Builder(openAiChatProperties.getOptions())
                    .withModel(OpenAiApi.ChatModel.GPT_4_O).build();
            return new Prompt(List.of(userMessage), options);
        }

        if (FileUploadUtils.isPdf(file.getName())) {
            String parsedText = textParsers.getTextParser(file.getName())
                    .parse(file);
            SystemMessage systemMessage = new SystemMessage(isStreaming ? SYSTEM_CONTENT_STREAMING : SYSTEM_CONTENT);
            UserMessage userMessage = new UserMessage(TokenUtils.truncateStringWithDefaultModelType(parsedText, openAiChatProperties.getOptions().getMaxTokens()));
            OpenAiChatOptions options = new OpenAiChatOptions.Builder(openAiChatProperties.getOptions())
                    .withModel(OpenAiApi.ChatModel.GPT_3_5_TURBO).build();
            return new Prompt(List.of(systemMessage, userMessage), options);
        }

        throw new UnsupportedOperationException("unsupported fileName, fileName = " + file.getName());
    }

}
