package kr.ac.jnu.vocai.backend.parser.impl;

import kr.ac.jnu.vocai.backend.file.parser.exception.PageIndexOutOfBoundsException;
import kr.ac.jnu.vocai.backend.file.parser.impl.PdfTextParser;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

class PdfTextParserTest {
    private final PdfTextParser parser = new PdfTextParser();


    @ParameterizedTest
    @MethodSource("provideFileNameAndResult")
    public void givenFileName_whenValidatingTextParserSupportThenReturnsResult(String fileName, boolean expected) {
        //given
        //when
        boolean actual = parser.supports(fileName);
        //then
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideFileNameAndResult() {
        return Stream.of(
                Arguments.of("xxxx", false),
                Arguments.of("xxx.xxxx", false),
                Arguments.of(" ", false),
                Arguments.of(".pdf", true),
                Arguments.of("aa.pdf", true)
        );
    }


    @Test
    public void givenFile_whenParsingText_thenReturnParsedText() throws IOException {
        //given
        ClassPathResource resource = new ClassPathResource("testPaper.pdf");
        File pdfFile = resource.getFile();
        //when
        String parsedText = parser.parse(pdfFile);
        //then
        assertThat(parsedText).isNotEmpty();
        System.out.println(parsedText);
    }

    @Test
    public void givenFileAndPage_whenParsingTextByPage_thenReturnParsedText() throws IOException{
        //given
        ClassPathResource resource = new ClassPathResource("testPaper.pdf");
        File pdfFile = resource.getFile();
        int page = 1;
        //when
        String parsedText = parser.parseByPage(pdfFile, page);
        //then
        assertThat(parsedText).isNotNull();
    }


    @Test
    public void givenFileAndIndexOutPage_whenParsingTextByPage_thenThrowsPageIndexOutOfBoundsException() throws Exception {
        //given
        ClassPathResource resource = new ClassPathResource("testPaper.pdf");
        File pdfFile = resource.getFile();
        int fileTotalPages = 33; // testPaper.pdf 의 총 페이지 수
        int indexOutPage = 100_000;
        //when
        ThrowableAssert.ThrowingCallable execute = () -> parser.parseByPage(pdfFile, indexOutPage);
        //then
        assertThatThrownBy(execute)
                .isInstanceOf(PageIndexOutOfBoundsException.class)
                .hasMessage("주어진 page 가 파일의 page 인덱스 범위를 벗어났습니다. page= " + indexOutPage + ", numberOfPages = " + fileTotalPages);
    }

    @Test
    public void givenNotExistFile_whenParsingText_thenReturnEmptyString() throws IOException{
        //given
        File notExistFile = mock(File.class);
        String notExistsFilePath = "xxxxxxxxxxxxxx";
        given(notExistFile.toPath()).willReturn(Path.of(notExistsFilePath));
        //when
        String actual = parser.parse(notExistFile);
        //then
        assertThat(actual).isEmpty();
    }

    @Test
    public void givenNotExistFile_whenParsingTextByPage_thenReturnEmptyString() throws IOException{
        //given
        File notExistFile = mock(File.class);
        String notExistsFilePath = "xxxxxxxxxxxxxx";
        given(notExistFile.toPath()).willReturn(Path.of(notExistsFilePath));
        //when
        String actual = parser.parseByPage(notExistFile, 0);
        //then
        assertThat(actual).isEmpty();
    }
}