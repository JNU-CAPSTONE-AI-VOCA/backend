package kr.ac.jnu.vocai.backend.parser;

import kr.ac.jnu.vocai.backend.parser.exception.ParserNotFoundException;
import kr.ac.jnu.vocai.backend.parser.impl.PdfTextParser;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * @author daecheol song
 * @since 1.0
 */
class TextParsersTest {

    private final TextParsers textParsers = new TextParsers();

    @Test
    public void givenSupportedFileName_whenGetTextParser_thenReturnSupportedTextParser() {
        //given
        String fileName = "foo.pdf";
        //when
        TextParser textParser = textParsers.getTextParser(fileName);
        //then
        assertThat(textParser).isInstanceOf(PdfTextParser.class);
    }

    @Test
    public void givenNotSupportedFileName_whenGetTextParser_thenThrowParserNotFoundException() {
        //given
        String fileName = "xxxx.xxxxx";
        //when
        ThrowableAssert.ThrowingCallable execute = () -> textParsers.getTextParser(fileName);
        //then
        assertThatExceptionOfType(ParserNotFoundException.class)
                .isThrownBy(execute)
                .withMessage("해당 파일이름을 지원하는 text parser 가 존재하지 않습니다. fileName = " + fileName);
    }
}
