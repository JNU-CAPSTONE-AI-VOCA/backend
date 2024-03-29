package kr.ac.jnu.vocai.backend;

import kr.ac.jnu.vocai.backend.config.OpenAIProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ConfigurationPropertiesScan
class BackendApplicationTests {

	private final OpenAIProperties properties;

	public BackendApplicationTests(@Autowired OpenAIProperties properties) {
		this.properties = properties;
	}

	@Test
	void contextLoads() {
		Assertions.assertThat(properties.key()).isNotNull().isNotEqualTo("${OPEN_API_KEY}");
		Assertions.assertThat(properties.model()).isNotNull();
	}

}
