package kr.ac.jnu.vocai.backend.file.parser.exception;

/**
 * Parser 를 찾을수 없을 때 발생하는 exception 클래스.
 * @author daecheol song
 * @since 1.0
 */
public class ParserNotFoundException extends RuntimeException {
    public ParserNotFoundException(String msg) {
        super(msg);
    }
}
