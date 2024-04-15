package kr.ac.jnu.vocai.backend.file.service;

import kr.ac.jnu.vocai.backend.file.dto.request.FileExtractRequest;
import kr.ac.jnu.vocai.backend.file.dto.response.ExtractWordResponse;

/**
 * @author daecheol song
 * @since 1.0
 */
public interface ExtractService {
    ExtractWordResponse extractWord(FileExtractRequest extractRequest);
}
