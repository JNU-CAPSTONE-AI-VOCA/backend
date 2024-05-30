package kr.ac.jnu.vocai.backend.generate.service;

import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateSentenceRequest;
import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateConfuseWordRequest;
import reactor.core.publisher.Flux;

/**
 * @author daecheol song
 * @since 1.0
 */
public interface GenerateService {

    String generateExampleSentence(GenerateSentenceRequest generateSentenceRequest);

    Flux<String> generateExampleSentenceStream(GenerateSentenceRequest generateSentenceRequest);

    String generateConfuseWords(GenerateConfuseWordRequest generateConfuseWordRequest);

}
