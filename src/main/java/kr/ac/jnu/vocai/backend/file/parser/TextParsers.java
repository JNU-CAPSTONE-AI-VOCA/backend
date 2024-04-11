package kr.ac.jnu.vocai.backend.file.parser;

import kr.ac.jnu.vocai.backend.file.parser.exception.ParserNotFoundException;
import kr.ac.jnu.vocai.backend.file.parser.impl.PdfTextParser;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TextParser 클래스들을 관리하는 클래스.
 * @author daecheol song
 * @since 1.0
 */
@Component
public class TextParsers {
    private final List<TextParser> parsers = List.of(new PdfTextParser());

    public TextParsers() {}

    /**
     * 해당 파일 이름을 지원하는 TextParser 를 반환하는 메서드.
     * @param fileName 파일 이름.
     * @throws ParserNotFoundException 해당 파일 이름을 지원하는 TextParser 가 없을때.
     * @return 지원하는 TextParser.
     */
    public TextParser getTextParser(String fileName) {

        return parsers
                .stream()
                .filter(parser -> parser.supports(fileName))
                .findFirst()
                .orElseThrow(() -> new ParserNotFoundException("해당 파일이름을 지원하는 text parser 가 존재하지 않습니다. fileName = " + fileName));
    }
}
