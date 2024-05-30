package kr.ac.jnu.vocai.backend.generate.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @author daecheol song
 * @since 1.0
 */
public record GenerateSentenceRequest(@NotNull Word incomplete, @NotEmpty List<Word> complete) {
    public record Word(String word, String meaning) {}
}
