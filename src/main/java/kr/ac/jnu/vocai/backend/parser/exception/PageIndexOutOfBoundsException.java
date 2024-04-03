package kr.ac.jnu.vocai.backend.parser.exception;

/**
 * Page 가 파일의 Page 범위를 벗어날때 발생하는 exception 클래스.
 * @author daecheol song
 * @since 1.0
 */
public class PageIndexOutOfBoundsException extends RuntimeException{

    public PageIndexOutOfBoundsException(String msg) {
        super(msg);
    }
}
