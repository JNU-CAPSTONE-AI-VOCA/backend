package kr.ac.jnu.vocai.backend.generate.service.impl;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * @author daecheol song
 * @since 1.0
 */
public class GenerateServiceTest {
    @ParameterizedTest
    @MethodSource("provideString")
    public void filterInCompleteWordTest(String response, String inCompleteWord, String expected) {
        assertThat(filterInCompleteWord(response, inCompleteWord)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideString() {
        return Stream
                .of(
                        Arguments.of("counts", "count", "____ "),
                        Arguments.of("sadly", "sad", "____ "),
                        Arguments.of("sadly,", "sad", "____, "),
                        Arguments.of("repeats", "repeat", "____ "),
                        Arguments.of("ass", "as", "ass "),
                        Arguments.of("as", "a", "as "),
                        Arguments.of("count", "count", "____ "),
                        Arguments.of("irons", "iron", "____ "),
                        Arguments.of("Kiss", "kiss", "____ "),
                        Arguments.of("Kiss", "kiss", "____ "),
                        Arguments.of("as", "as", "____ "),
                        Arguments.of("easily", "easy", "____ "),
                        Arguments.of("copies", "copy", "____ "),
                        Arguments.of("test", "texts", "test "),
                        Arguments.of("holy.", "texts", "holy. "),
                        Arguments.of("butterflies", "butterfly", "____ ")
                );
    }

    private String filterInCompleteWord(String word, String inCompleteWord) {
        String append = "";
        if (word.endsWith(".") || word.endsWith(",")) {
            append = word.substring(word.length() - 1);
            word = word.substring(0, word.length() - 1);
        }

        if (word.equalsIgnoreCase(inCompleteWord)) {
            return "____" + append + " ";
        }

        if (word.length() < 5) {
            if (inCompleteWord.equalsIgnoreCase(word)) {
                return "____" + append + " ";
            }
            return word + append + " ";
        }
        if (word.endsWith("s")) {
            if (inCompleteWord.toLowerCase().equals(word.toLowerCase().substring(0, word.length() - 1))) {
                return "____" + append + " ";
            }
        }
        if (word.endsWith("ly") || word.endsWith("ed") || word.endsWith("es")) {
            if (inCompleteWord.toLowerCase().contains(word.toLowerCase().substring(0, word.length() - 2))) {
                return "____" + append + " ";
            }
        }
        if (word.endsWith("ies") || word.endsWith("ily")) {
            if (inCompleteWord.toLowerCase().contains(word.toLowerCase().substring(0, word.length() - 3))) {
                return "____" + append + " ";
            }
        }
        return word + append + " ";
    }
}
