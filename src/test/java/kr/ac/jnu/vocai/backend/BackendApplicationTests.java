package kr.ac.jnu.vocai.backend;

import kr.ac.jnu.vocai.backend.common.config.OpenAIProperties;
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

	private static final String DEFAULT_KEY_NAME = "${OPEN_API_KEY}";
	@Test
	void given_whenContextLoads_thenPropertiesLoaded() {
		//given
		//when
		//then
		Assertions.assertThat(properties.key()).isNotNull().isNotEqualTo(DEFAULT_KEY_NAME);
		Assertions.assertThat(properties.model()).isNotNull();
	}

}
