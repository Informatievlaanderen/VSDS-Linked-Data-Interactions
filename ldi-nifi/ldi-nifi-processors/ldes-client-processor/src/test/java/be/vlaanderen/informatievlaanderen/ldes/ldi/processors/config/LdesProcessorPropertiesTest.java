package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.mock.MockProcessContext;
import org.apache.nifi.util.MockPropertyValue;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.getDataSourceUrl;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

	@Test
	void test_getDatasourceUrl() {
		assertDoesNotThrow(() -> getMockContext("http://localhost/endpoint"));
		assertDoesNotThrow(() -> getMockContext("http://localhost/endpoint,http://localhost/other"));
		var singleInvalidUri = getMockContext("inv alid");
		assertThrows(IllegalArgumentException.class, () -> getDataSourceUrl(singleInvalidUri));
		var multiInvalidUri = getMockContext("inv alid,http://localhost/other");
		assertThrows(IllegalArgumentException.class, () -> getDataSourceUrl(multiInvalidUri));
	}

	@Test
	void test_getSqliteConfig() {
		assertThat(PersistenceProperties.getStatePersistenceStrategy(getMockContext("SQLITE")))
				.isEqualTo(StatePersistenceStrategy.SQLITE);
		assertThat(PersistenceProperties.getSqliteDirectory(getMockContext("/ldio/sqlite")))
				.contains("/ldio/sqlite");
		assertThat(PersistenceProperties.getSqliteDirectory(getMockContext(null)))
				.isEmpty();
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