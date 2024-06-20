package kr.ac.jnu.vocai.backend.common.utils;

import kr.ac.jnu.vocai.backend.file.exception.InvalidFileExtensionException;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.util.Objects;
import java.util.UUID;

/**
 * 업로드 파일을 uuid로 저장하는 클래스.
 * @author wavewwave20
 * @since 1.0
 */

public class FileUploadUtils {

    public static boolean isImage(String fileName) {
        String extension = getExtension(fileName);
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif");
    }

    public static boolean isPdf(String fileName) {
        String extension = getExtension(fileName);
        return extension.equals("pdf");
    }

    public static MimeType parseImageMimeType(String extension) {
        if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
            return MimeTypeUtils.IMAGE_JPEG;
        }

        if (extension.equalsIgnoreCase("png")) {
            return MimeTypeUtils.IMAGE_PNG;
        }

        if (extension.equalsIgnoreCase("gif")) {
            return MimeTypeUtils.IMAGE_GIF;
        }

        throw new TypeNotPresentException(extension, new RuntimeException("해당 extension 에 해당하는 이미지 MimeType 을 찾을수 없습니다."));
    }

    public static boolean isValidFileName(String fileName) {
        return Objects.nonNull(fileName) && fileName.matches(".+\\..+");
    }

    public static String toUploadFileName(String fileName) {
        StringBuilder builder = new StringBuilder();
        UUID uuid = UUID.randomUUID();
        String extension = getExtension(fileName);
        builder.append(uuid).append(".").append(extension);
        return builder.toString();
    }

    // 확장자 추출
    public static String getExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos + 1);
    }

}
