package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.LdesClientProcessor;
import org.apache.nifi.util.MockProcessContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.DATA_SOURCE_URLS;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties.getDataSourceUrl;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RequestExecutorProperties.RETRIES_ENABLED;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RequestExecutorProperties.STATUSES_TO_RETRY;
import static org.junit.jupiter.api.Assertions.*;

class LdesProcessorPropertiesTest {

	@Test
	void test_retriesEnabled() {
		// default is true
		assertTrue(RequestExecutorProperties.retriesEnabled(getMockContextRetriesEnabled("true")));
		assertTrue(RequestExecutorProperties.retriesEnabled(getMockContextRetriesEnabled("trUe")));
		assertFalse(RequestExecutorProperties.retriesEnabled(getMockContextRetriesEnabled("false")));
		assertFalse(RequestExecutorProperties.retriesEnabled(getMockContextRetriesEnabled("FALSE")));
	}

	@Test
	void test_getStatusesToRetry() {
		List<Integer> statusesToRetry = RequestExecutorProperties.getStatusesToRetry(getMockContextStatuses("200, 204"));
		assertTrue(statusesToRetry.contains(200));
		assertTrue(statusesToRetry.contains(204));
		assertFalse(statusesToRetry.contains(500));
	}

	@Test
	void test_getDatasourceUrl() {
		assertDoesNotThrow(() -> getMockContextDatasource("http://localhost/endpoint"));
		assertDoesNotThrow(() -> getMockContextDatasource("http://localhost/endpoint,http://localhost/other"));
		var singleInvalidUri = getMockContextDatasource("inv alid");
		assertThrows(IllegalArgumentException.class, () -> getDataSourceUrl(singleInvalidUri));
		var multiInvalidUri = getMockContextDatasource("inv alid,http://localhost/other");
		assertThrows(IllegalArgumentException.class, () -> getDataSourceUrl(multiInvalidUri));
	}

	private MockProcessContext getMockContextRetriesEnabled(String retriesEnabled) {
		// Create a MockProcessContext with the TestRunner
		MockProcessContext context = new MockProcessContext(new LdesClientProcessor());
		context.setProperty(RETRIES_ENABLED, retriesEnabled);
		return context;
	}

	private MockProcessContext getMockContextStatuses(String val) {
		// Create a MockProcessContext with the TestRunner
		MockProcessContext context = new MockProcessContext(new LdesClientProcessor());
		context.setProperty(STATUSES_TO_RETRY, val);
		return context;
	}

	private MockProcessContext getMockContextDatasource(String val) {
		// Create a MockProcessContext with the TestRunner
		MockProcessContext context = new MockProcessContext(new LdesClientProcessor());
		context.setProperty(DATA_SOURCE_URLS, val);
		return context;
	}

}