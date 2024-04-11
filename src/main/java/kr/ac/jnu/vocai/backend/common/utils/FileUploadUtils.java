package kr.ac.jnu.vocai.backend.common.utils;

import kr.ac.jnu.vocai.backend.file.exception.InvalidFileExtensionException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 업로드 파일을 uuid로 저장하는 클래스.
 * @author wavewwave20
 * @since 1.0
 */

@Component
public class FileUploadUtils {

    public static boolean isImage(String fileName) {
        String extension = getExtension(fileName);

        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif");
    }

    public static boolean isPdf(String fileName) {
        String extension = getExtension(fileName);

        return extension.equals("pdf");
    }

    public static String fileNameConvert(String fileName) {
        if (!(isImage(fileName) || isPdf(fileName))) {
            String extension = getExtension(fileName);
            throw new InvalidFileExtensionException("Invalid file extension: " + extension);
        }

        StringBuilder builder = new StringBuilder();
        UUID uuid = UUID.randomUUID();
        String extension = getExtension(fileName);

        builder.append(uuid).append(".").append(extension);

        return builder.toString();
    }

    // 확장자 추출
    private static String getExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");

        return fileName.substring(pos + 1);
    }

}
