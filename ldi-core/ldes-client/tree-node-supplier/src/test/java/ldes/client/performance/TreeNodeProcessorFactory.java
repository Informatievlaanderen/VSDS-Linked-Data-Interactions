package ldes.client.performance;

import be.vlaanderen.informatievlaanderen.ldes.ldi.h2.H2Properties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromCurrentTimeExtractor;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.apache.jena.riot.Lang;
import org.mockito.Mockito;

import java.util.List;
import java.util.function.Consumer;

class TreeNodeProcessorFactory {

	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory(false);

	TreeNodeProcessor createTreeNodeProcessor(List<String> url, Lang sourceFormat) {
		final StatePersistence statePersistence = createh2Persistence();
		final LdesMetaData ldesMetaData = new LdesMetaData(url, sourceFormat);
		final RequestExecutor requestExecutor = requestExecutorFactory.createNoAuthExecutor();
		final TimestampExtractor timestampExtractor = new TimestampFromCurrentTimeExtractor();
		return new TreeNodeProcessor(ldesMetaData, statePersistence, requestExecutor, timestampExtractor, Mockito.mock(Consumer.class));
	}

	private StatePersistence createh2Persistence() {
		var properties = new H2Properties("test");
		return StatePersistence.from(properties, "test");
	}
}
