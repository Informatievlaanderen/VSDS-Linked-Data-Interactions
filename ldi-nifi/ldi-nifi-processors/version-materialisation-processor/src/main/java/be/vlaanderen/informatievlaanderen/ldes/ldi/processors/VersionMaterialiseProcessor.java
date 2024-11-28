package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;

import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.VersionMaterialisationProcessorProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.*;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes, vsds" })
@CapabilityDescription("Version materialisation of an LDES stream")
public class VersionMaterialiseProcessor extends AbstractProcessor {
	private VersionMaterialiser versionMaterialiser;

	@Override
	public Set<Relationship> getRelationships() {
		return Set.of(SUCCESS, FAILURE);
	}

	@Override
	public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		return List.of(VERSION_OF_PROPERTY, RESTRICT_TO_MEMBERS);
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {
		Property isVersionOfProperty = ResourceFactory.createProperty(getVersionOfProperty(context));

		versionMaterialiser = new VersionMaterialiser(isVersionOfProperty, restrictToMembers(context));
	}

	@Override
	public void onTrigger(final ProcessContext context, final ProcessSession session) {
		final FlowFile flowFile = session.get();
		if (flowFile != null) {
			try {
				final Model inputModel = receiveDataAsModel(session, flowFile,
						RDFLanguages.contentTypeToLang(flowFile.getAttribute("mime.type")));

				Model result = versionMaterialiser.transform(inputModel);
				sendRDFToRelation(session, flowFile, result, SUCCESS,
						RDFLanguages.contentTypeToLang(flowFile.getAttribute("mime.type")));

			} catch (Exception e) {
				getLogger().error("Error executing version materialisation: {}", e.getMessage());
				session.transfer(flowFile, FAILURE);
			}
		}
	}

}
