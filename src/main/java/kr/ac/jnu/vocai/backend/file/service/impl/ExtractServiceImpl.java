package kr.ac.jnu.vocai.backend.file.service.impl;

import kr.ac.jnu.vocai.backend.chat.service.ChatService;
import kr.ac.jnu.vocai.backend.common.config.FileProperties;
import kr.ac.jnu.vocai.backend.file.dto.request.FileExtractRequest;
import kr.ac.jnu.vocai.backend.file.dto.response.ExtractWordResponse;
import kr.ac.jnu.vocai.backend.file.service.ExtractService;
import kr.ac.jnu.vocai.backend.file.parser.TextParsers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    private final ChatService chatService;

    @Override
    public ExtractWordResponse extractWord(FileExtractRequest extractRequest) {
        return ExtractWordResponse.of(
                extractRequest.files()
                        .stream()
                        .map(MultipartFile::getOriginalFilename)
                        .toList()
                ,
                extractRequest.files()
                        .stream()
                        .map(this::transferDataAndSaveFile)
                        .filter(Objects::nonNull)
                        .map(this::parseTextFromFile)
                        .map(chatService::getChatCompletionMessageContent)
                        .toList()
        );
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



}
