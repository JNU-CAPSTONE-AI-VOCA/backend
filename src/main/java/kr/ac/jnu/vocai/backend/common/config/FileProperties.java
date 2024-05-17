package kr.ac.jnu.vocai.backend.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author daecheol song
 * @since 1.0
 */
@ConfigurationProperties("file")
public record FileProperties(String uploadDir) {
}
