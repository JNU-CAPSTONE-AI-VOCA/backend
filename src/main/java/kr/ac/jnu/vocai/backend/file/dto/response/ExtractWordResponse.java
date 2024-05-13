package kr.ac.jnu.vocai.backend.file.dto.response;

import java.util.List;

/**
 * @author daecheol song
 * @since 1.0
 */
public record ExtractWordResponse(String sentence, List<Word> words) {
    private record Word(String word, String meaning) {}
}