package kr.ac.jnu.vocai.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * @author daecheol song
 * @since 1.0
 */
@ConfigurationPropertiesScan
@SpringJUnitConfig(classes = {RestTemplateConfig.class})
public class RestTemplateConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void given_whenSearchingRestTemplateBuilderBean_thenReturnsNotNull() {
        //given
        //when
        RestTemplateBuilder restTemplateBuilder = context.getBean(RestTemplateBuilder.class);
        //then
        assertThat(restTemplateBuilder).isNotNull();
    }
}
