package kr.ac.jnu.vocai.backend.file.service;

import kr.ac.jnu.vocai.backend.file.dto.request.FileExtractRequest;
import kr.ac.jnu.vocai.backend.file.dto.response.ExtractWordResponse;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author daecheol song
 * @since 1.0
 */
public interface ExtractService {

    List<ExtractWordResponse> extractWord(FileExtractRequest extractRequest);

    Flux<ExtractWordResponse> extractWordStream(FileExtractRequest extractRequest);
}
