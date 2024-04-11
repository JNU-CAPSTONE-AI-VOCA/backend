package kr.ac.jnu.vocai.backend.file.dto.request;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author daecheol song
 * @since 1.0
 */
public record FileExtractRequest(@NotEmpty List<MultipartFile> files) {
}
