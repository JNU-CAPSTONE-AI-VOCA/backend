package kr.ac.jnu.vocai.backend.file.dto.response;

/**
 * @author daecheol song
 * @since 1.0
 */

import java.util.*;

/**
 * @author daecheol song
 * @since 1.0
 */
public record ExtractWordResponse(Set<FileWord> vocabularyList) {
    public static ExtractWordResponse of(List<String> fileNames, List<String> contents) {

        Set<FileWord> vocabularyList = new HashSet<>();

        for (int i = 0; i < fileNames.size(); i++) {
            String fileName = fileNames.get(i);
            String content = contents.get(i);
            vocabularyList.add(FileWord.of(fileName, content));
        }

        return new ExtractWordResponse(Collections.unmodifiableSet(vocabularyList));
//        return new ExtractWordResponse(contents
//                .stream()
//                .map(s -> s.split("\n"))
//                .map(Arrays::stream)
//                .map(wordAndMeaning -> wordAndMeaning
//                            .map(wordAndMeaningString -> wordAndMeaningString.split(","))
//                            .filter(splitWordAndMeaning -> splitWordAndMeaning.length == 2)
//                            .map(splitWordAndMeaning -> Word.of(splitWordAndMeaning[0].strip(), splitWordAndMeaning[1].strip()))
//                            .toList()
//                ).collect(Collectors.toUnmodifiableSet())
//        );
    }

    private record FileWord(String fileName, List<Word> words) {
        private static FileWord of(String fileName, String content) {
            return new FileWord(fileName, Arrays.stream(content.split("\n"))
                    .map(wordAndMeaningString -> wordAndMeaningString.split(","))
                    .filter(splitWordAndMeaning -> splitWordAndMeaning.length == 2)
                    .map(splitWordAndMeaning -> Word.of(splitWordAndMeaning[0].strip(), splitWordAndMeaning[1].strip()))
                    .toList()
            );
        }
    }


    private record Word(String word, String meaning) {
        private static Word of(String word, String meaning) {
            return new Word(word, meaning);
        }
    }
}