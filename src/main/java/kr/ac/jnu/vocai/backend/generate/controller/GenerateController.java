package kr.ac.jnu.vocai.backend.generate.controller;

import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateSentenceRequest;
import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateConfuseWordRequest;
import kr.ac.jnu.vocai.backend.generate.service.GenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 생성 관련 컨트롤러 클래스.
 * @author daecheol song
 * @since 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/generate")
public class GenerateController {

    private final GenerateService generateService;

    @PostMapping(value = "/sentence/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateExampleSentenceStream(@RequestBody @Validated GenerateSentenceRequest generateSentenceRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("validation failed, cause = {}", bindingResult.getAllErrors());
            throw new RuntimeException(bindingResult.getAllErrors().toString());
        }
        return generateService.generateExampleSentenceStream(generateSentenceRequest);
    }

    @PostMapping(value = "/sentence", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String generateExampleSentence(@RequestBody @Validated GenerateSentenceRequest generateSentenceRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("validation failed, cause = {}", bindingResult.getAllErrors());
            throw new RuntimeException(bindingResult.getAllErrors().toString());
        }
        return generateService.generateExampleSentence(generateSentenceRequest);
    }

    @PostMapping(value = "/words", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String generateExampleWords(@RequestBody @Validated GenerateConfuseWordRequest generateConfuseWordRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("validation failed, cause = {}", bindingResult.getAllErrors());
            throw new RuntimeException(bindingResult.getAllErrors().toString());
        }
        return generateService.generateConfuseWords(generateConfuseWordRequest);
    }
}
