package kr.ac.jnu.vocai.backend.file.controller;

import kr.ac.jnu.vocai.backend.common.utils.FileUploadUtils;
import kr.ac.jnu.vocai.backend.file.dto.request.FileExtractRequest;
import kr.ac.jnu.vocai.backend.file.dto.response.ExtractWordResponse;
import kr.ac.jnu.vocai.backend.file.service.ExtractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 파일 컨트롤러 클래스.
 * @author wavewwave20
 * @author daecheol song
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final ExtractService extractService;

    @PostMapping(value = "/extract/words", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExtractWordResponse>> extractWords(@Validated FileExtractRequest extractRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("bad request, cause = {}", bindingResult.getAllErrors());
            throw new RuntimeException(bindingResult.getAllErrors().toString());
        }
        return ResponseEntity.ok(extractService.extractWord(extractRequest));
    }

    @PostMapping(value = "/extract/words/stream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ExtractWordResponse> extractWordsStream(@Validated FileExtractRequest extractRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("bad request, cause = {}", bindingResult.getAllErrors());
            throw new RuntimeException(bindingResult.getAllErrors().toString());
        }
        return extractService.extractWordStream(extractRequest);
    }

}
