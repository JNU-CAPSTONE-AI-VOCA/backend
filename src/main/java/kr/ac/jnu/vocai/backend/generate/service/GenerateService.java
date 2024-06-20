package kr.ac.jnu.vocai.backend.generate.service;

import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateMeaningRequest;
import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateSentenceRequest;
import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateConfuseWordRequest;
import kr.ac.jnu.vocai.backend.generate.dto.request.GenerateSentenceWordRequest;
import reactor.core.publisher.Flux;

/**
 * @author daecheol song
 * @since 1.0
 */
public interface GenerateService {

    String generateExampleSentence(GenerateSentenceWordRequest generateSentenceWordRequest);

    Flux<String> generateExampleSentenceStream(GenerateSentenceRequest generateSentenceRequest);

    String generateConfuseWords(GenerateConfuseWordRequest generateConfuseWordRequest);

    String generateSentenceMeaning(GenerateMeaningRequest generateMeaningRequest);
}
