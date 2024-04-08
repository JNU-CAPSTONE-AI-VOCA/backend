package kr.ac.jnu.vocai.backend.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class VocabularyListDTO {
    private List<ExampleDTO> vocabularyList;

    public VocabularyListDTO(List<ExampleDTO> vocabulary) {
        this.vocabularyList = vocabulary;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ExampleDTO {
        private String example;
        private List<WordDTO> vocabulary;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class WordDTO {
        private String word;
        private String meaning;
    }
}
