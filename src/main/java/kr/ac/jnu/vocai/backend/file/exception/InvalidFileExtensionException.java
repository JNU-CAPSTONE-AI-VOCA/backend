package kr.ac.jnu.vocai.backend.file.exception;

/**
 * 파일 확장자가 유효하지 않을 때 발생하는 exception 클래스.
 * @author wavewwave20
 * @since 1.0
 */
public class InvalidFileExtensionException extends RuntimeException{
    public InvalidFileExtensionException(String msg) {
        super(msg);
    }
}
