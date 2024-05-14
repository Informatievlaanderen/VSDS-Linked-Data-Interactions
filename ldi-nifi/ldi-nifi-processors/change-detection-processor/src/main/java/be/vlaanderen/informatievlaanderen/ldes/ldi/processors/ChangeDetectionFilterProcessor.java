package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;


import be.vlaanderen.informatievlaanderen.ldes.ldi.ChangeDetectionFilter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.nifi.annotation.lifecycle.OnRemoved;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ChangeDetectionFilterProperties.DATA_SOURCE_FORMAT;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ChangeDetectionFilterProperties.getDataSourceFormat;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ChangeDetectionFilterRelationships.IGNORED;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.ChangeDetectionFilterRelationships.NEW_STATE_RECEIVED;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.sendRDFToRelation;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
public class ChangeDetectionFilterProcessor extends AbstractProcessor {
	private ChangeDetectionFilter changeDetectionFilter;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(NEW_STATE_RECEIVED, IGNORED);
	}

	@Override
	protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(
				DATA_SOURCE_FORMAT,
				STATE_PERSISTENCE_STRATEGY,
				POSTGRES_URL,
				POSTGRES_USERNAME,
				POSTGRES_PASSWORD,
				SQLITE_DIRECTORY,
				KEEP_STATE
		);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		final HashedStateMemberRepository repository = HashedMemberRepositoryFactory.getRepository(context);
		final boolean keepState = PersistenceProperties.stateKept(context);
		changeDetectionFilter = new ChangeDetectionFilter(repository, keepState);
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) {
		final FlowFile flowFile = session.get();
		if (flowFile == null) {
			return;
		}

		final Lang lang = getDataSourceFormat(context);
		final Model model = FlowManager.receiveDataAsModel(session, flowFile, lang);

		final Model filteredModel = changeDetectionFilter.transform(model);

		if (filteredModel.isEmpty()) {
			sendRDFToRelation(session, flowFile, IGNORED);
		}
		sendRDFToRelation(session, flowFile, NEW_STATE_RECEIVED);
	}

	@OnRemoved
	public void onRemoved() {
		changeDetectionFilter.destroyState();
	}
}
