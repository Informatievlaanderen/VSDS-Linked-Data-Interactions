package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.wrappers.testutils;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.LdesClientProcessor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.LdesProcessorProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties;
import org.apache.nifi.util.MockProcessContext;

public class TestProcessContext extends MockProcessContext {

	public TestProcessContext(boolean useExactlyOnceFilter, boolean useVersionMaterialisation, boolean useLatestStateFilter) {
		super(new LdesClientProcessor());
		this.setProperty(LdesProcessorProperties.USE_EXACTLY_ONCE_FILTER, String.valueOf(useExactlyOnceFilter));
		this.setProperty(LdesProcessorProperties.USE_VERSION_MATERIALISATION, String.valueOf(useVersionMaterialisation));
		this.setProperty(LdesProcessorProperties.USE_LATEST_STATE_FILTER, String.valueOf(useLatestStateFilter));
		this.setProperty(PersistenceProperties.KEEP_STATE, String.valueOf(false));
		this.setProperty(PersistenceProperties.STATE_PERSISTENCE_STRATEGY, "MEMORY");
	}

	public TestProcessContext(boolean useExactlyOnce) {
		this(useExactlyOnce, false, false);
	}

	public TestProcessContext(boolean useVersionMaterialisation, boolean useLatestStateFilter) {
		this(false, useVersionMaterialisation, useLatestStateFilter);
	}
}
