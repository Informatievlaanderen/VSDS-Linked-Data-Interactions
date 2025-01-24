package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;


import be.vlaanderen.informatievlaanderen.ldes.ldi.ChangeDetectionFilter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
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

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({"change-detection-filter", "vsds"})
@CapabilityDescription("Checks if the state of state members has been changed, and if not the member will be ignored")
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
				DBCP_SERVICE,
				KEEP_STATE
		);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		final HashedStateMemberRepository repository = HashedMemberRepositoryFactory.getRepository(context);
		changeDetectionFilter = new ChangeDetectionFilter(repository);
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) {
		final Lang fallbackLang = getDataSourceFormat(context);

		final FlowFile flowFile = session.get();
		if (flowFile == null) {
			return;
		}

		final String mimeType = flowFile.getAttribute("mime.type");
		final Lang lang = mimeType != null
				? RDFLanguages.contentTypeToLang(mimeType)
				: fallbackLang;
		final Model model = FlowManager.receiveDataAsModel(session, flowFile, lang);

		final Model filteredModel = changeDetectionFilter.transform(model);

		if (filteredModel.isEmpty()) {
			session.transfer(flowFile, IGNORED);
		} else {
			session.transfer(flowFile, NEW_STATE_RECEIVED);
		}
	}

	@OnRemoved
	public void onRemoved() {
		changeDetectionFilter.close();
	}
}
