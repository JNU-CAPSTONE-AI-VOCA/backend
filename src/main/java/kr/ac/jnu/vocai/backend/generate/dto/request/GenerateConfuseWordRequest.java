package kr.ac.jnu.vocai.backend.generate.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * @author daecheol song
 * @since 1.0
 */
public record GenerateConfuseWordRequest(@NotNull Word word) {
    public record Word(String word, String meaning){}
}
