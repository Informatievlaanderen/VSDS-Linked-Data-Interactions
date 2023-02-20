package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LdtoHttpInputTest {
	@Test
	// As a workaround to not spin up the RestController by default, the LdtoHttpIn Bean gets a @RestController
	// Annotation on Autoconfiguration
	void assert_NoRestControllerAnnotationisSet() {
		LdioHttpIn ldtoHttpIn = new LdioHttpIn(null);

		assertTrue(Arrays.stream(ldtoHttpIn.getClass().getAnnotations())
				.anyMatch(annotation -> annotation.annotationType().equals(RequestMapping.class)));
		assertTrue(Arrays.stream(ldtoHttpIn.getClass().getAnnotations())
				.noneMatch(annotation -> annotation.annotationType().equals(RestController.class)));
	}
}
