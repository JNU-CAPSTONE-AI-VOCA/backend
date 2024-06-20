package kr.ac.jnu.vocai.backend.generate.dto.request;

import jakarta.validation.constraints.NotEmpty;

/**
 * @author daecheol song
 * @since 1.0
 */
public record GenerateMeaningRequest(@NotEmpty String sentence) {
}
