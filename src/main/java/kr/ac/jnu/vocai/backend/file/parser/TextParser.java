package kr.ac.jnu.vocai.backend.file.parser;

import kr.ac.jnu.vocai.backend.file.parser.exception.PageIndexOutOfBoundsException;

import java.io.File;

/**
 * File 에 있는 내용을 text 로 parsing 하는 기능을 위한 인터페이스.
 * @author daecheol song
 * @since 1.0
 */
public interface TextParser {

    /**
     * 해당 파일이름을 TextParser 가 지원하는지 검증하는 메서드.
     * @param fileName 파일 이름.
     * @return 검증 여부.
     */
    boolean supports(String fileName);

    /**
     * 주어진 파일 전체 내용을 text 로 parsing 하여 반환하는 메서드.
     * @param file text 로 parsing 할 파일.
     * @return parsed text.
     */
    String parse(File file);

    /**
     * 주어진 파일의 특정 Page의 내용을 text 로 parsing 하여 반환하는 메서드.
     * @param file text 로 parsing 할 파일.
     * @param page parsing 할 페이지.
     * @throws PageIndexOutOfBoundsException 주어진 page 가 파일의 page 인덱스 범위를 벗어났을때.
     * @return parsed text.
     */
    String parseByPage(File file, int page) throws PageIndexOutOfBoundsException;
}
