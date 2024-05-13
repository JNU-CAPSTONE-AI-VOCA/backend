package kr.ac.jnu.vocai.backend.common.config;

import com.fasterxml.jackson.core.json.JsonReadFeature;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @author daecheol song
 * @since 1.0
 */
@Configuration
public class WebConfig {

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .featuresToEnable(
                        JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature()
                        ,JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature()
                );
    }
}
