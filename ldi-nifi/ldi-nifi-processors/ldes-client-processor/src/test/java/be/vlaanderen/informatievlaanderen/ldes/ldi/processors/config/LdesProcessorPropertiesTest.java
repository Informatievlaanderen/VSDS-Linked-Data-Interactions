package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.mock.MockProcessContext;
import org.apache.nifi.util.MockPropertyValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LdesProcessorPropertiesTest {

	@Test
	void test_retriesEnabled() {
		// default is true
		assertTrue(LdesProcessorProperties.retriesEnabled(getBooleanMockContext(null)));
		assertTrue(LdesProcessorProperties.retriesEnabled(getBooleanMockContext("true")));
		assertTrue(LdesProcessorProperties.retriesEnabled(getBooleanMockContext("trUe")));
		assertFalse(LdesProcessorProperties.retriesEnabled(getBooleanMockContext("false")));
		assertFalse(LdesProcessorProperties.retriesEnabled(getBooleanMockContext("FALSE")));
	}

	private static MockProcessContext getBooleanMockContext(String bool) {
		return new MockProcessContext() {
			@Override
			public PropertyValue getProperty(PropertyDescriptor descriptor) {
				return new MockPropertyValue(bool);
			}
		};
	}

}