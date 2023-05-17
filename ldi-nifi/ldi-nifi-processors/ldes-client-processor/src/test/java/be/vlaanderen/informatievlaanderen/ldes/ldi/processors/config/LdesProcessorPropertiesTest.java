package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.mock.MockProcessContext;
import org.apache.nifi.util.MockPropertyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LdesProcessorPropertiesTest {

	@Test
	void test_retriesEnabled() {
		// default is true
		assertTrue(LdesProcessorProperties.retriesEnabled(getMockContext(null)));
		assertTrue(LdesProcessorProperties.retriesEnabled(getMockContext("true")));
		assertTrue(LdesProcessorProperties.retriesEnabled(getMockContext("trUe")));
		assertFalse(LdesProcessorProperties.retriesEnabled(getMockContext("false")));
		assertFalse(LdesProcessorProperties.retriesEnabled(getMockContext("FALSE")));
	}

	@Test
	void test_getStatusesToRetry() {
		assertTrue(LdesProcessorProperties.getStatusesToRetry(getMockContext(null)).isEmpty());
		List<Integer> statusesToRetry = LdesProcessorProperties.getStatusesToRetry(getMockContext("200, 204"));
		assertTrue(statusesToRetry.contains(200));
		assertTrue(statusesToRetry.contains(204));
		assertFalse(statusesToRetry.contains(500));
	}

	private static MockProcessContext getMockContext(String value) {
		return new MockProcessContext() {
			@Override
			public PropertyValue getProperty(PropertyDescriptor descriptor) {
				return new MockPropertyValue(value);
			}
		};
	}

}