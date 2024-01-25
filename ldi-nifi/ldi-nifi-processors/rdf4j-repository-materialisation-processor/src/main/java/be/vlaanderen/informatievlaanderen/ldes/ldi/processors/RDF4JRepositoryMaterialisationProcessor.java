package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.Materialiser;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.parser.JenaContextProvider;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.RDF4JRepositoryMaterialisationProcessorProperties.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.FAILURE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.FlowManager.SUCCESS;

@SuppressWarnings("java:S2160") // nifi handles equals/hashcode of processors
@Tags({ "ldes, rdf4j-repository, vsds" })
@CapabilityDescription("Materialises LDES events into an RDF4J repository")
@InputRequirement(InputRequirement.Requirement.INPUT_REQUIRED)
public class RDF4JRepositoryMaterialisationProcessor extends AbstractProcessor {

	private Materialiser materialiser;

	@Override
	public Set<Relationship> getRelationships() {
		final Set<Relationship> relationships = new HashSet<>();
		relationships.add(SUCCESS);
		relationships.add(FAILURE);
		return relationships;
	}

	@Override
	protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
		final List<PropertyDescriptor> properties = new ArrayList<>();
		properties.add(DATA_SOURCE_FORMAT);
		properties.add(SPARQL_HOST);
		properties.add(REPOSITORY_ID);
		properties.add(NAMED_GRAPH);
		properties.add(SIMULTANEOUS_FLOWFILES_TO_PROCESS);
		return properties;
	}

	@OnScheduled
	public void onScheduled(final ProcessContext context) {

		if (materialiser == null) {
			materialiser = new Materialiser(context.getProperty(SPARQL_HOST).getValue(),
					context.getProperty(REPOSITORY_ID).getValue(),
					context.getProperty(NAMED_GRAPH).getValue());
		}
	}

	@Override
	public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
		final List<FlowFile> flowFiles = session.get(Integer.parseInt(
				context.getProperty(SIMULTANEOUS_FLOWFILES_TO_PROCESS).getValue()));

		if (flowFiles.isEmpty()) {
			return;
		}

		Lang dataSourceFormat = getDataSourceFormat(context);

		for (FlowFile flowFile : flowFiles) {
			String content = FlowManager.receiveData(session, flowFile);

			try {
				materialiser.process(RDFParser
						.fromString(content)
						.context(JenaContextProvider.create().getContext())
						.lang(dataSourceFormat)
						.toModel());
				session.transfer(flowFile, SUCCESS);
			} catch (Exception e) {
				getLogger().error("Error sending model to repository", e.getMessage());
				session.transfer(flowFile, FAILURE);
			}
		}
	}
}
