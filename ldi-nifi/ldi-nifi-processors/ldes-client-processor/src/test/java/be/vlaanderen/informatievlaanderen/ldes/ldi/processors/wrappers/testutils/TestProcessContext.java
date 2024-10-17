package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers.testutils;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.mock.MockProcessContext;
import org.apache.nifi.util.MockPropertyValue;

import java.util.Map;

public class TestProcessContext extends MockProcessContext {
	private final Map<PropertyDescriptor, String> properties;


	public TestProcessContext(boolean useExactlyOnceFilter, boolean useVersionMaterialisation, boolean useLatestStateFilter) {
		properties = Map.of(
				LdesProcessorProperties.USE_EXACTLY_ONCE_FILTER, String.valueOf(useExactlyOnceFilter),
				LdesProcessorProperties.USE_VERSION_MATERIALISATION, String.valueOf(useVersionMaterialisation),
				LdesProcessorProperties.USE_LATEST_STATE_FILTER, String.valueOf(useLatestStateFilter),
				PersistenceProperties.KEEP_STATE, String.valueOf(false),
				PersistenceProperties.STATE_PERSISTENCE_STRATEGY, "MEMORY"
		);
	}

	public TestProcessContext(boolean useExactlyOnce) {
		this(useExactlyOnce, false, false);
	}

	public TestProcessContext(boolean useVersionMaterialisation, boolean useLatestStateFilter) {
		this(false, useVersionMaterialisation, useLatestStateFilter);
	}

	@Override
	public PropertyValue getProperty(PropertyDescriptor descriptor) {
		return new MockPropertyValue(properties.get(descriptor));
	}
}
