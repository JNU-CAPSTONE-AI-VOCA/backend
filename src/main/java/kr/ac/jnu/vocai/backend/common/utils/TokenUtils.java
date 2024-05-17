package kr.ac.jnu.vocai.backend.common.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingResult;
import com.knuddels.jtokkit.api.ModelType;
import lombok.extern.slf4j.Slf4j;

/**
 * Chatgpt Token 유틸 클래스.
 *
 * @author daecheol song
 * @since 1.0
 */
@Slf4j
public final class TokenUtils {

    private static final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

    public static int countFromString(String content, ModelType modelType) {
        Encoding encoding = registry.getEncodingForModel(modelType);
        return encoding.countTokens(content);
    }

    public static String truncateString(String content, ModelType modelType, int maxToken) {
        Encoding encoding = registry.getEncodingForModel(modelType);
        EncodingResult encode = encoding.encode(content, maxToken);
        if (encode.isTruncated()) {
            log.debug("encoding truncated");
        }
        return encoding.decode(encode.getTokens());
    }

    public static String truncateStringWithDefaultModelType(String content, int maxToken) {
        return truncateString(content, ModelType.GPT_3_5_TURBO, maxToken);
    }
}
