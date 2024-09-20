package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.h2.H2Properties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import org.apache.nifi.processor.ProcessContext;

public class StatePersistenceFactory {

	public StatePersistence getStatePersistence(ProcessContext context) {
		HibernateProperties properties = new H2Properties(context.getName());

		return StatePersistence.from(properties, context.getName());
	}
}
